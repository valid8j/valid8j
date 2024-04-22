package com.github.valid8j.pcond.core;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

/**
 * The new design:
 *
 * Evaluator: Concentrates on "evaluate" an individual evaluable (form). No aware of how to compose evaluation entries.
 */

public class EvaluationContext<T> {
  final List<EvaluationEntry> evaluationEntries = new LinkedList<>();
  final List<EvaluationEntry> visitorLineage    = new LinkedList<>();

  boolean expectationFlipped = false;

  public EvaluationContext() {
  }

  public EvaluationContext(EvaluationContext<?> parent) {
    this.expectationFlipped = parent.isExpectationFlipped();
  }

  /**
   * @param evaluableIo       An object to hold a form and its I/O.
   * @param evaluatorCallback A callback that executes a logic specific to the {@code evaluable}.
   */
  public <E extends Evaluable<T>, O> void evaluate(EvaluableIo<T, E, O> evaluableIo, BiFunction<E, ValueHolder<T>, ValueHolder<O>> evaluatorCallback) {
    evaluate(resolveEvaluationEntryType(evaluableIo.evaluable()), evaluableIo, evaluatorCallback);
  }

  public <E extends Evaluable<T>, O> void evaluate(EvaluationEntry.Type type, EvaluableIo<T, E, O> evaluableIo, BiFunction<E, ValueHolder<T>, ValueHolder<O>> evaluatorCallback) {
    evaluate(type, evaluableIo, io -> evaluatorCallback.apply(io.evaluable(), io.input()));
  }

  public <E extends Evaluable<T>, O> void evaluate(EvaluationEntry.Type type, EvaluableIo<T, E, O> evaluableIo, Function<EvaluableIo<T, E, O>, ValueHolder<O>> function) {
    evaluate(type, formNameOf(evaluableIo), evaluableIo, function);
  }

  public <E extends Evaluable<T>, O> void evaluate(EvaluationEntry.Type type, String formName, EvaluableIo<T, E, O> evaluableIo, Function<EvaluableIo<T, E, O>, ValueHolder<O>> function) {
    requireNonNull(evaluableIo);
    EvaluableIo<T, E, O> evaluableIoWork = this.enter(evaluableIo.input(), type, formName, evaluableIo.evaluable());
    this.leave(evaluableIoWork, function.apply(evaluableIoWork));
    DebuggingUtils.printTo(this, System.err, 1);
    updateEvaluableIo(evaluableIo, evaluableIoWork);
  }

  public static String formNameOf(EvaluableIo<?, ?, ?> evaluableIo) {
    return formNameOf(evaluableIo.evaluableType(), evaluableIo.evaluable());
  }

  public static String formNameOf(EvaluationEntry.Type type, Evaluable<?> e) {
    return type.formName(e);
  }

  public boolean isExpectationFlipped() {
    return this.expectationFlipped;
  }

  public void flipExpectation() {
    this.expectationFlipped = !expectationFlipped;
  }

  private static <T, E extends Evaluable<T>, O> void updateEvaluableIo(EvaluableIo<T, E, O> evaluableIo, EvaluableIo<T, E, O> evaluableIoWork) {
    evaluableIo.output(evaluableIoWork.output());
  }

  public static <T> EvaluationEntry.Type resolveEvaluationEntryType(Evaluable<T> evaluable) {
    if (evaluable instanceof Evaluable.LeafPred || evaluable instanceof Evaluable.CurriedContextPred || evaluable instanceof Evaluable.StreamPred)
      return EvaluationEntry.Type.LEAF;
    if (evaluable instanceof Evaluable.Func)
      return EvaluationEntry.Type.FUNCTION;
    if (evaluable instanceof Evaluable.Conjunction)
      return EvaluationEntry.Type.AND;
    if (evaluable instanceof Evaluable.Disjunction)
      return EvaluationEntry.Type.OR;
    if (evaluable instanceof Evaluable.Negation)
      return EvaluationEntry.Type.NOT;
    if (evaluable instanceof Evaluable.Transformation)
      return EvaluationEntry.Type.TRANSFORM_AND_CHECK;
    throw new IllegalArgumentException();
  }

  @SuppressWarnings("unchecked")
  private <E extends Evaluable<T>, O> EvaluableIo<T, E, O> enter(ValueHolder<T> input, EvaluationEntry.Type type, String formName, E evaluable) {
    EvaluableIo<T, Evaluable<T>, O> ret = createEvaluableIo(input, type, formName, evaluable);
    this.evaluationEntries.add(createEvaluationEntry(this, ret));
    this.visitorLineage.add(evaluationEntries.get(evaluationEntries.size() - 1));
    return (EvaluableIo<T, E, O>) ret;
  }

  private <E extends Evaluable<T>, O> void leave(EvaluableIo<T, E, O> evaluableIo, ValueHolder<O> output) {
    EvaluationEntry.Impl currentEvaluationEntry = (EvaluationEntry.Impl) this.visitorLineage.remove(this.visitorLineage.size() - 1);
    evaluableIo.output(output);
    currentEvaluationEntry.finalizeValues();
  }

  private static <T, O> EvaluableIo<T, Evaluable<T>, O> createEvaluableIo(ValueHolder<T> input, EvaluationEntry.Type type, String formName, Evaluable<T> evaluable) {
    return new EvaluableIo<>(input, type, formName, evaluable);
  }

  private static <T, E extends Evaluable<T>> EvaluationEntry createEvaluationEntry(
      EvaluationContext<T> evaluationContext,
      EvaluableIo<T, E, ?> evaluableIo) {
    return new EvaluationEntry.Impl(evaluationContext, evaluableIo);
  }

  public List<EvaluationEntry> resultEntries() {
    return new ArrayList<>(this.evaluationEntries);
  }

  public <R> void importEntries(EvaluationContext<R> childContext) {
    importEntries(childContext, currentIndentLevel());
  }

  public <R> void importEntries(EvaluationContext<R> childContext, int indentLevelGap) {
    childContext.evaluationEntries.forEach(each -> each.level += indentLevelGap);
    this.evaluationEntries.addAll(childContext.resultEntries());
  }

  public int currentIndentLevel() {
    return this.visitorLineage.size();
  }
}
