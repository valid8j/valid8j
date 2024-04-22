package com.github.valid8j.pcond.core;

import com.github.valid8j.pcond.experimentals.currying.context.CurriedContext;
import com.github.valid8j.pcond.internals.InternalUtils;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.github.valid8j.pcond.core.EvaluationContext.formNameOf;
import static com.github.valid8j.pcond.core.EvaluationContext.resolveEvaluationEntryType;
import static com.github.valid8j.pcond.core.ValueHolder.CreatorFormType.FUNC_HEAD;
import static com.github.valid8j.pcond.core.ValueHolder.CreatorFormType.FUNC_TAIL;
import static com.github.valid8j.pcond.core.ValueHolder.State.*;
import static java.util.Objects.requireNonNull;

/**
 * A visitor interface that defines a mechanism to "evaluate" printable predicates.
 */
public interface Evaluator {
  /**
   * Evaluates `value` with `conjunction` predicate ("and").
   *
   * @param <T>               The type of the `value`.
   * @param evaluableIo       An object to hold an evaluable and its input and output.
   * @param evaluationContext An evaluation context.
   * @see Evaluable.Conjunction
   */
  <T> void evaluateConjunction(EvaluableIo<T, Evaluable.Conjunction<T>, Boolean> evaluableIo, EvaluationContext<T> evaluationContext);

  /**
   * Evaluates `value` with a `disjunction` predicate ("or").
   *
   * @param <T>               The type of the `value`.
   * @param evaluableIo       An object to hold an evaluable and its input and output.
   * @param evaluationContext An evaluation context.
   * @see Evaluable.Disjunction
   */
  <T> void evaluateDisjunction(EvaluableIo<T, Evaluable.Disjunction<T>, Boolean> evaluableIo, EvaluationContext<T> evaluationContext);

  /**
   * Evaluates `value` with a `negation` predicate ("not").
   *
   * @param <T>               The type of the `value`.
   * @param evaluableIo       An object to hold an evaluable and its input and output.
   * @param evaluationContext An evaluation context.
   * @see Evaluable.Negation
   */
  <T> void evaluateNegation(EvaluableIo<T, Evaluable.Negation<T>, Boolean> evaluableIo, EvaluationContext<T> evaluationContext);

  /**
   * Evaluates `value` with a leaf predicate.
   *
   * @param <T>               The type of the `value`.
   * @param evaluableIo       An object to hold an evaluable and its input and output.
   * @param evaluationContext An evaluation context.
   * @see Evaluable.LeafPred
   */
  <T> void evaluateLeaf(EvaluableIo<T, Evaluable.LeafPred<T>, Boolean> evaluableIo, EvaluationContext<T> evaluationContext);

  /**
   * Evaluates `value` with a "function" predicate.
   *
   * @param evaluableIo       An object to hold an evaluable and its input and output.
   * @param evaluationContext An evaluation context.
   * @see Evaluable.Func
   */
  <T, R> void evaluateFunction(EvaluableIo<T, Evaluable.Func<T>, R> evaluableIo, EvaluationContext<T> evaluationContext);

  /**
   * Evaluates `value` with a context predicate.
   *
   * @param evaluableIo       An object to hold an evaluable and its input and output.
   * @param evaluationContext An evaluation context.
   * @see Evaluable.CurriedContextPred
   */
  void evaluateCurriedContextPredicate(EvaluableIo<CurriedContext, Evaluable.CurriedContextPred, Boolean> evaluableIo, EvaluationContext<CurriedContext> evaluationContext);

  /**
   * Evaluates `value` with a "transformation" predicate.
   *
   * @param evaluableIo       An object to hold an evaluable and its input and output.
   * @param evaluationContext An evaluation context.
   * @see Evaluable.Transformation
   */
  <T, R> void evaluateTransformation(EvaluableIo<T, Evaluable.Transformation<T, R>, Boolean> evaluableIo, EvaluationContext<T> evaluationContext);

  /**
   * Evaluates `value` with a predicate for a stream.
   *
   * @param evaluableIo       An object to hold an evaluable and its input and output.
   * @param evaluationContext An evaluation context.
   * @see Evaluable.StreamPred
   */
  <E> void evaluateStreamPredicate(EvaluableIo<Stream<E>, Evaluable.StreamPred<E>, Boolean> evaluableIo, EvaluationContext<Stream<E>> evaluationContext);

  /**
   * Returns a new instance of this interface.
   *
   * @return a new instance of this interface.
   */
  static Evaluator create() {
    return new Impl();
  }

  class Impl implements Evaluator {
    public static final Object EVALUATION_SKIPPED = new Object() {
      @Override
      public String toString() {
        return "(not evaluated)";
      }
    };

    private static final Object NULL_VALUE = new Object() {
      public String toString() {
        return "null";
      }
    };

    public Impl() {
    }

    @Override
    public <T> void evaluateConjunction(EvaluableIo<T, Evaluable.Conjunction<T>, Boolean> evaluableIo, EvaluationContext<T> evaluationContext) {
      evaluationContext.evaluate(
          evaluableIo,
          (Evaluable.Conjunction<T> evaluable, ValueHolder<T> input) -> {
            ValueHolder<Boolean> ret = ValueHolder.create();
            boolean result = true;
            ValueHolder<Boolean> retSkipped = null;
            for (Evaluable<T> each : evaluable.children()) {
              EvaluableIo<T, Evaluable<T>, Boolean> child = createChildEvaluableIoOf(each, input);
              each.accept(child, evaluationContext, this);
              ValueHolder<Boolean> outputFromEach = child.output();
              if (outputFromEach.isValueReturned()) {
                result &= outputFromEach.returnedValue();
                ret = ValueHolder.forValue(result);
              } else if (child.output().isExceptionThrown()) {
                ret = ValueHolder.<Boolean>create().evaluationSkipped();
                retSkipped = retSkipped != null ? retSkipped : ret;
              } else if (child.output().isEvaluationSkipped()) {
                ret = ValueHolder.<Boolean>create().evaluationSkipped();
                retSkipped = retSkipped != null ? retSkipped : ret;
              } else
                assert false;
              if (evaluable.shortcut() && (ret.isEvaluationSkipped() || !result))
                break;
            }
            return retSkipped != null ? retSkipped : ret;
          });
    }

    @Override
    public <T> void evaluateDisjunction(EvaluableIo<T, Evaluable.Disjunction<T>, Boolean> evaluableIo, EvaluationContext<T> evaluationContext) {
      evaluationContext.evaluate(
          evaluableIo,
          (Evaluable.Disjunction<T> evaluable, ValueHolder<T> input) -> {
            ValueHolder<Boolean> ret = ValueHolder.create();
            boolean result = false;
            ValueHolder<Boolean> retSkipped = null;
            for (Evaluable<T> each : evaluable.children()) {
              EvaluableIo<T, Evaluable<T>, Boolean> child = createChildEvaluableIoOf(each, input);
              each.accept(child, evaluationContext, this);
              ValueHolder<Boolean> outputFromEach = child.output();
              if (outputFromEach.isValueReturned()) {
                result |= outputFromEach.returnedValue();
                ret = ValueHolder.forValue(result);
              } else if (outputFromEach.isExceptionThrown()) {
                ret = ValueHolder.<Boolean>create().evaluationSkipped();
                retSkipped = retSkipped != null ? retSkipped : ret;
              } else if (outputFromEach.isEvaluationSkipped()) {
                ret = ValueHolder.<Boolean>create().evaluationSkipped();
                retSkipped = retSkipped != null ? retSkipped : ret;
              } else
                assert false;
              if (evaluable.shortcut() && (ret.isEvaluationSkipped() || result))
                break;
            }
            return retSkipped != null ? retSkipped : ret;
          });
    }

    @Override
    public <T> void evaluateNegation(EvaluableIo<T, Evaluable.Negation<T>, Boolean> evaluableIo, EvaluationContext<T> evaluationContext) {
      evaluationContext.evaluate(
          evaluableIo,
          (Evaluable.Negation<T> evaluable, ValueHolder<T> input) -> {
            evaluationContext.flipExpectation();
            try {
              EvaluableIo<T, Evaluable<T>, Boolean> childIo = createChildEvaluableIoOf(evaluable.target(), input);
              evaluable.target().accept(childIo, evaluationContext, this);
              return childIo.output().isValueReturned() ?
                  ValueHolder.forValue(evaluationContext.isExpectationFlipped() ^ childIo.output().returnedValue()) :
                  childIo.output();
            } finally {
              evaluationContext.flipExpectation();
            }
          }
      );
    }

    @Override
    public <T> void evaluateLeaf(EvaluableIo<T, Evaluable.LeafPred<T>, Boolean> evaluableIo, EvaluationContext<T> evaluationContext) {
      evaluationContext.evaluate(
          EvaluationEntry.Type.LEAF,
          evaluableIo,
          (evaluable, input) -> {
            ValueHolder<Boolean> ret = ValueHolder.create();
            if (input.isValueReturned()) {
              T value = input.returnedValue();
              Predicate<? super T> predicate = requireNonNull(evaluable.predicate());
              try {
                return ret.valueReturned(predicate.test(value));
              } catch (Throwable t) {
                return ret.exceptionThrown(t);
              }
            } else
              return ret.evaluationSkipped();
          });
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T, R> void evaluateFunction(EvaluableIo<T, Evaluable.Func<T>, R> evaluableIo, EvaluationContext<T> evaluationContext) {
      evaluationContext.evaluate( //#2
          EvaluationEntry.Type.FUNCTION,
          evaluableIo,
          (Evaluable.Func<T> evaluable, ValueHolder<T> input) -> {
            ValueHolder<R> ret;
            {
              EvaluableIo<T, Evaluable<T>, Object> ioForHead = createChildEvaluableIoOf(evaluable, input);
              EvaluationContext<T> childContext = new EvaluationContext<>(evaluationContext);
              childContext.evaluate(EvaluationEntry.Type.FUNCTION, ioForHead, io -> {
                ValueHolder<Object> tmp = ValueHolder.create();
                if (io.input().isValueReturned())
                  tmp = applyFunction(tmp, io.input().returnedValue(), ((Evaluable.Func<T>) io.evaluable()).head());
                else
                  tmp = tmp.evaluationSkipped();
                return tmp.creatorFormType(FUNC_HEAD);
              });
              evaluationContext.importEntries(childContext, 1);
              ret = (ValueHolder<R>) ioForHead.output().creatorFormType(FUNC_TAIL);
            }
            ValueHolder<Object> finalRet = (ValueHolder<Object>) ret;
            return evaluable.tail().map((Evaluable<Object> e) -> {
                  EvaluableIo<Object, Evaluable<Object>, R> ioForTail = createChildEvaluableIoOf(e, finalRet);
                  DebuggingUtils.printIo("FUNC_TAIL:BEFORE", ioForTail);
                  e.accept(ioForTail, (EvaluationContext<Object>) evaluationContext, this);
                  DebuggingUtils.printIo("FUNC_TAIL:AFTER", ioForTail);
                  return ioForTail.output().creatorFormType(FUNC_TAIL);
                })
                .orElse(ret);
          });
    }

    @SuppressWarnings("unchecked")
    private static <T, R> ValueHolder<R> applyFunction(ValueHolder<R> ret, T in, Function<? super T, Object> function) {
      try {
        R returnedValue;
        returnedValue = (R) function.apply(in);
        return ret.valueReturned(returnedValue);
      } catch (Throwable t) {
        return ret.exceptionThrown(t);
      }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public <T, R> void evaluateTransformation(EvaluableIo<T, Evaluable.Transformation<T, R>, Boolean> evaluableIo, EvaluationContext<T> evaluationContext) {
      if (InternalUtils.isDummyFunction((Function<?, ?>) evaluableIo.evaluable().mapper())) {
        evaluableIo.evaluable().checker().accept((EvaluableIo<R, Evaluable<R>, Boolean>) (Evaluable) evaluableIo, (EvaluationContext<R>) evaluationContext, this);
        return;
      }
      EvaluationContext<T> childContext = new EvaluationContext<>(evaluationContext);
      childContext.evaluate(
          evaluableIo,
          (Evaluable.Transformation<T, R> evaluable, ValueHolder<T> input) -> {
            DebuggingUtils.printInput("TRANSFORMATION:BEFORE", evaluable, input);
            EvaluableIo<T, Evaluable<T>, R> mapperIo = evaluateMapper(evaluable.mapperName().orElse("transform"), evaluable.mapper(), input, childContext);
            EvaluableIo<R, Evaluable<R>, Boolean> checkerIo = evaluateChecker(evaluable.checkerName().orElse("check"), evaluable.checker(), mapperIo.output(), childContext);
            DebuggingUtils.printInputAndOutput(evaluable, input, checkerIo.output());
            return checkerIo.output();
          }
      );
      evaluationContext.importEntries(childContext, 1);
    }

    private <T, R> EvaluableIo<T, Evaluable<T>, R> evaluateMapper(String mapperName, Evaluable<T> mapper, ValueHolder<T> input, EvaluationContext<T> evaluationContext) {
      EvaluableIo<T, Evaluable<T>, R> ioForMapper = createChildEvaluableIoOf(mapper, input.creatorFormType(ValueHolder.CreatorFormType.TRANSFORM));
      {
        EvaluationContext<T> childContext = new EvaluationContext<>(evaluationContext);

        // #1
        childContext.evaluate(EvaluationEntry.Type.TRANSFORM, mapperName, ioForMapper, io -> {
          DebuggingUtils.printIo("TRANSFORM:BEFORE", io);
          io.evaluable().accept(io, childContext, this);
          DebuggingUtils.printIo("TRANSFORM:AFTER", io);
          return io.output();
        });

        evaluationContext.importEntries(childContext, 0);
      }
      return ioForMapper;
    }

    private <T, R> EvaluableIo<R, Evaluable<R>, Boolean> evaluateChecker(String checkerName, Evaluable<R> checker, ValueHolder<R> input, EvaluationContext<T> evaluationContext) {
      EvaluableIo<R, Evaluable<R>, Boolean> ioForChecker = createChildEvaluableIoOf(checker, input);
      {
        EvaluationContext<R> childContext = new EvaluationContext<>(evaluationContext);

        childContext.evaluate(EvaluationEntry.Type.CHECK, checkerName, ioForChecker, io -> {
          DebuggingUtils.printIo("CHECK:BEFORE", io);
          io.evaluable().accept(io, childContext, this);
          DebuggingUtils.printIo("CHECK:AFTER", io);
          return io.output();
        });

        evaluationContext.importEntries(childContext, 0);
      }
      return ioForChecker;
    }

    //             ValueToCut  ValueOnCut ValueForNone(=default)
    // NoneMatch         true       false                   true
    // AnyMatch          true        true                  false
    // AllMatch         false       false                   true

    @Override
    public <E> void evaluateStreamPredicate(EvaluableIo<Stream<E>, Evaluable.StreamPred<E>, Boolean> evaluableIo, EvaluationContext<Stream<E>> evaluationContext) {
      evaluationContext.evaluate(
          evaluableIo,
          (Evaluable.StreamPred<E> evaluable, ValueHolder<Stream<E>> input) -> input.returnedValue()
              .map((E e) -> {
                if (evaluable.requestExpectationFlip())
                  evaluationContext.flipExpectation();
                try {
                  EvaluationContext<E> childContext = new EvaluationContext<>(evaluationContext);
                  EvaluableIo<E, Evaluable<E>, Boolean> ioForCutPredicate = createChildEvaluableIoOf(evaluable.cut(), ValueHolder.forValue(e));
                  evaluable.cut().accept(ioForCutPredicate, childContext, this);
                  evaluationContext.importEntries(childContext);
                  return ioForCutPredicate.output();
                } finally {
                  if (evaluable.requestExpectationFlip())
                    evaluationContext.flipExpectation();
                }
              })
              .filter(eachResult -> {
                if (!eachResult.isValueReturned())
                  return true;
                return eachResult.returnedValue() == evaluable.valueToCut();
              })
              .map(eachResult -> eachResult.valueReturned(!evaluable.defaultValue())) // compute Value on cut
              .findFirst()
              .orElseGet(() -> ValueHolder.forValue(evaluable.defaultValue())));      // compute Value for none
    }

    @Override
    public void evaluateCurriedContextPredicate(EvaluableIo<CurriedContext, Evaluable.CurriedContextPred, Boolean> evaluableIo, EvaluationContext<CurriedContext> evaluationContext) {
      evaluationContext.evaluate(evaluableIo, (Evaluable.CurriedContextPred evaluable, ValueHolder<CurriedContext> input) -> {
        EvaluableIo<Object, Evaluable<Object>, Boolean> io = createChildEvaluableIoOf(evaluable.enclosed(), ValueHolder.forValue(input.returnedValue().valueAt(evaluable.argIndex())));
        EvaluationContext<Object> childContext = new EvaluationContext<>(evaluationContext);
        evaluable.enclosed().accept(io, childContext, this);
        evaluationContext.importEntries(childContext);
        return io.output();
      });
    }

    private static <T, E extends Evaluable<T>, O> EvaluableIo<T, Evaluable<T>, O> createChildEvaluableIoOf(E evaluable, ValueHolder<T> input) {
      return createChildEvaluableIoOf(resolveEvaluationEntryType(evaluable).formName(evaluable), evaluable, input);
    }

    private static <T, E extends Evaluable<T>, O> EvaluableIo<T, Evaluable<T>, O> createChildEvaluableIoOf(String formName, E evaluable, ValueHolder<T> input) {
      EvaluationEntry.Type evaluableType = resolveEvaluationEntryType(evaluable);
      return createChildEvaluableIoOf(evaluableType, formName, evaluable, input);
    }

    private static <T, E extends Evaluable<T>, O> EvaluableIo<T, Evaluable<T>, O> createChildEvaluableIoOf(EvaluationEntry.Type evaluableType, String formName, E evaluable, ValueHolder<T> input) {
      return new EvaluableIo<>(input, evaluableType, formName, evaluable);
    }
  }

  /**
   * If an input or an output value object of a form implements this interface,
   * The value returned by `snapshot` method is stored in a {@link EvaluationEntry}
   * record, instead of the value itself.
   *
   * An implementation of this interface should override `toString()` method to return a string form of the original state of this object.
   */
  interface Snapshottable {

    Object NULL = new Object() {
      @Override
      public String toString() {
        return "null";
      }
    };

    Object snapshot();

    static Object toSnapshotIfPossible(Object value) {
      if (value instanceof Snapshottable)
        return ((Snapshottable) value).snapshot();
      if (value == null)
        return NULL;
      else
        return value;
    }
  }

  /**
   * An interface to define methods that make a predicate "explainable" to humans.
   */
  interface Explainable {
    Object explainOutputExpectation();

    Object explainActual(Object actualValue);

    static Object explainOutputExpectation(Object evaluable, EvaluableIo<?, ?, ?> evaluableIo) {
      if (evaluable instanceof Explainable)
        return InternalUtils.explainValue(((Explainable) evaluable).explainOutputExpectation());
      if (evaluable instanceof Evaluable)
        return formNameOf(evaluableIo);
      return null;
    }

    static Object explainInputActualValue(Object evaluable, Object actualValue) {
      if (evaluable instanceof Explainable)
        return InternalUtils.explainValue(((Explainable) evaluable).explainActual(actualValue));
      return null;
    }

    static <T, E extends Evaluable<T>> Object explainActual(EvaluableIo<T, E, ?> evaluableIo) {
      if (evaluableIo.output().state() == VALUE_RETURNED) {
        T ret = evaluableIo.input().returnedValue();
        return ret != null ? ret : Impl.NULL_VALUE;
      } else if (evaluableIo.output().state() == EXCEPTION_THROWN)
        return EvaluationEntry.composeDetailOutputActualValueFromInputAndThrowable(evaluableIo.input().value(), evaluableIo.output().thrownException());
      else if (evaluableIo.output().state() == EVALUATION_SKIPPED) {
        return EVALUATION_SKIPPED;
      } else
        throw new AssertionError("evaluableIo:" + evaluableIo);
    }
  }
}