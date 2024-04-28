package com.github.valid8j.pcond.core;

import com.github.valid8j.pcond.core.ValueHolder.State;
import com.github.valid8j.pcond.validator.Validator;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import static com.github.valid8j.pcond.core.EvaluationContext.resolveEvaluationEntryType;
import static com.github.valid8j.pcond.core.Evaluator.Explainable.*;
import static com.github.valid8j.pcond.core.Evaluator.Impl.EVALUATION_SKIPPED;
import static com.github.valid8j.pcond.core.Evaluator.Snapshottable.toSnapshotIfPossible;
import static com.github.valid8j.pcond.core.ValueHolder.CreatorFormType.FUNC_TAIL;
import static com.github.valid8j.pcond.core.ValueHolder.State.VALUE_RETURNED;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static com.github.valid8j.pcond.core.EvaluationEntry.Type.*;

/**
 *
 * // @formatter:off
 * A class to hold an entry of execution history of the {@link Evaluator}.
 * When an evaluator enters into one {@link Evaluable} (actually a predicate or a function),
 * an {@code OnGoing} entry is created and held by the evaluator as a current
 * one.
 * Since one evaluate can have its children and only one child can be evaluated at once,
 * on-going entries are held as a list (stack).
 *
 * When the evaluator leaves the evaluable, the entry is "finalized".
 * From the data held by an entry, "expectation" and "actual behavior" reports are generated.
 *
 * .Evaluation Summary Format
 * ----
 *
 * +----------------------------------------------------------------------------- Failure Detail Index
 * |  +-------------------------------------------------------------------------- Input
 * |  |                                            +----------------------------- Form (Function/Predicate)
 * |  |                                            |                           +- Output
 * |  |                                            |                           |
 * V  V                                            V                           V
 *     Book:[title:<De Bello G...i appellantur.>]->check:allOf               ->false
 *                                                    transform:title       ->"De Bello Gallico"
 *                       "De Bello Gallico"     ->    check:allOf           ->false
 *                                                        isNotNull         ->true
 * [0]                                                    transform:parseInt->NumberFormatException:"For input s...ico""
 *                          null                ->        check:allOf       ->false
 *                                                            >=[10]        ->true
 *                                                            <[40]         ->true
 *    Book:[title:<De Bello G...i appellantur.>]->    transform:title       ->"Gallia est omnis divis...li appellantur."
 *    "Gallia est omnis divis...li appellantur."->    check:allOf           ->false
 *                                                        isNotNull         ->true
 *                                                        transform:length  ->145
 *    145                                       ->        check:allOf       ->false
 * [1]                                                         >=[200]       ->true
 * <[400]        ->true
 *
 * ----
 *
 * Failure Detail Index::
 * In the full format of a failure report, detailed descriptions of mismatching forms are provided if the form is {@link Evaluator.Explainable}.
 * This index points an item in the detail part of the full report.
 * Input::
 * Values given to forms are printed here.
 * If the previous line uses the same value, the value will not be printed.
 * Form (Function/Predicate)::
 * This part displays names of forms (predicates and functions).
 * If a form is marked trivial, the framework may merge the form with the next line.
 * Output::
 * For predicates, expected boolean value is printed.
 * For functions, if a function does not throw an exception during its evaluation, the result will be printed here both for expectation and actual behavior summary.
 * If it throws an exception, the exception will be printed here in actual behavior summary.
 *
 * // @formatter:on
 */
public abstract class EvaluationEntry {
  private final Type   type;
  /**
   * A name of a form (evaluable; function, predicate)
   */
  private final String formName;
  int level;

  Object inputExpectation;
  Object detailInputExpectation;

  Object inputActualValue;
  Object detailInputActualValue;

  Object outputExpectation;
  Object detailOutputExpectation;


  /**
   * A flag to let the framework know this entry should be printed in a less outstanding form.
   */
  final boolean squashable;

  EvaluationEntry(String formName, Type type, int level, Object inputExpectation_, Object detailInputExpectation_, Object outputExpectation, Object detailOutputExpectation, Object inputActualValue, Object detailInputActualValue, boolean squashable) {
    this.type = type;
    this.level = level;
    this.formName = formName;
    this.inputExpectation = inputExpectation_;
    this.detailInputExpectation = detailInputExpectation_;
    this.outputExpectation = outputExpectation;
    this.detailOutputExpectation = detailOutputExpectation;
    this.inputActualValue = inputActualValue;
    this.detailInputActualValue = detailInputActualValue;
    this.squashable = squashable;
  }

  public String formName() {
    return formName;
  }

  public Type type() {
    return this.type;
  }

  @SuppressWarnings("BooleanMethodIsAlwaysInverted")
  public boolean isSquashable(EvaluationEntry nextEntry) {
    return this.squashable;
  }

  public abstract boolean requiresExplanation();

  public int level() {
    return level;
  }

  public Object inputExpectation() {
    return this.inputExpectation;
  }

  public Object detailInputExpectation() {
    return this.detailInputExpectation;
  }

  public Object outputExpectation() {
    return this.outputExpectation;
  }

  public Object detailOutputExpectation() {
    return this.detailOutputExpectation;
  }

  public Object inputActualValue() {
    return this.inputActualValue;
  }

  public abstract Object outputActualValue();

  public abstract Object detailOutputActualValue();

  public abstract boolean ignored();

  @Override
  public String toString() {
    return String.format("%s(%s)", formName(), inputActualValue());
  }

  static String composeDetailOutputActualValueFromInputAndThrowable(Object input, Throwable throwable) {
    StringBuilder b = new StringBuilder();
    b.append("Input: '").append(input).append("'").append(format("%n"));
    b.append("Input Type: ").append(input == null ? "(null)" : input.getClass().getName()).append(format("%n"));
    b.append("Thrown Exception: '").append(throwable.getClass().getName()).append("'").append(format("%n"));
    b.append("Exception Message: ").append(sanitizeExceptionMessage(throwable)).append(format("%n"));

    for (StackTraceElement each : foldInternalPackageElements(throwable)) {
      b.append("\t");
      b.append(each);
      b.append(format("%n"));
    }
    return b.toString();
  }

  private static String sanitizeExceptionMessage(Throwable throwable) {
    if (throwable.getMessage() == null)
      return null;
    return Arrays.stream(throwable.getMessage().split("\n"))
        .map(s -> "> " + s)
        .collect(joining(String.format("%n")));
  }

  static <T, E extends Evaluable<T>> Object computeInputActualValue(EvaluableIo<T, E, ?> evaluableIo) {
    return evaluableIo.input().value();
  }

  static <T, E extends Evaluable<T>> Object computeOutputExpectation(EvaluableIo<T, E, ?> evaluableIo, boolean expectationFlipped) {
    final State state = evaluableIo.output().state();
    if (state == VALUE_RETURNED) {
      if (evaluableIo.evaluableType() == FUNCTION || evaluableIo.evaluableType() == TRANSFORM)
        return toSnapshotIfPossible(evaluableIo.output().returnedValue());
      return !expectationFlipped;
    } else if (state == State.EXCEPTION_THROWN || state == State.EVALUATION_SKIPPED)
      return EVALUATION_SKIPPED;
    else
      throw new AssertionError("output state=<" + state + ">");
  }

  static <T, E extends Evaluable<T>> Object computeOutputActualValue(EvaluableIo<T, E, ?> evaluableIo) {
    if (evaluableIo.output().state() == State.VALUE_RETURNED)
      return toSnapshotIfPossible(evaluableIo.output().returnedValue());
    if (evaluableIo.output().state() == State.EXCEPTION_THROWN)
      return evaluableIo.output().thrownException();
    else
      return EVALUATION_SKIPPED;
  }

  static <T, E extends Evaluable<T>> boolean isExplanationRequired(EvaluableIo<T, E, ?> evaluableIo, boolean expectationFlipped) {
    return asList(FUNCTION, LEAF).contains(evaluableIo.evaluableType()) && (
        evaluableIo.output().state() == State.EXCEPTION_THROWN || (
            evaluableIo.evaluable() instanceof Evaluable.LeafPred && returnedValueOrVoidIfSkipped(expectationFlipped, evaluableIo)));
  }

  private static List<StackTraceElement> foldInternalPackageElements(Throwable throwable) {
    AtomicReference<StackTraceElement> firstInternalStackElement = new AtomicReference<>();
    String lastPackageNameElementPattern = "\\.[a-zA-Z0-9_.]+$";
    String internalPackageName = Validator.class.getPackage().getName()
        .replaceFirst(lastPackageNameElementPattern, "")
        .replaceFirst(lastPackageNameElementPattern, "");
    return Arrays.stream(throwable.getStackTrace())
        .filter(e -> {
          if (e.getClassName().startsWith(internalPackageName)) {
            if (firstInternalStackElement.get() == null) {
              firstInternalStackElement.set(e);
              return true;
            }
            return false;
          }
          firstInternalStackElement.set(null);
          return true;
        })
        .map(e -> {
          if (e.getClassName().startsWith(internalPackageName)) {
            return new StackTraceElement("...internal.package.InternalClass", "internalMethod", "InternalClass.java", 0);
          }
          return e;
        })
        .collect(toList());
  }

  private static boolean returnedValueOrVoidIfSkipped(boolean expectationFlipped, EvaluableIo<?, ?, ?> io) {
    if (io.output().state() == State.EVALUATION_SKIPPED)
      return false;
    return expectationFlipped ^ !(Boolean) io.output().returnedValue();
  }

  public enum Type {
    TRANSFORM_AND_CHECK {
      @Override
      String formName(Evaluable<?> evaluable) {
        return "transformAndCheck";
      }
    },
    TRANSFORM {
      @Override
      String formName(Evaluable<?> evaluable) {
        return "transform";
      }

      @Override
      boolean isSquashableWith(Impl nextEntry) {
        if (Objects.equals(FUNCTION, nextEntry.evaluableIo().evaluableType()))
          return !((Evaluable.Func<?>) nextEntry.evaluableIo().evaluable()).tail().isPresent();
        return false;
      }
    },
    CHECK {
      @Override
      String formName(Evaluable<?> evaluable) {
        return resolveEvaluationEntryType(evaluable).formName(evaluable);
      }

      @Override
      boolean isSquashableWith(Impl nextEntry) {
        return asList(LEAF, NOT, AND, OR, TRANSFORM).contains(nextEntry.evaluableIo().evaluableType());
      }
    },
    AND {
      @Override
      String formName(Evaluable<?> evaluable) {
        return ((Evaluable.Conjunction<?>) evaluable).shortcut() ? "and" : "allOf";
      }
    },
    OR {
      @Override
      String formName(Evaluable<?> evaluable) {
        return ((Evaluable.Disjunction<?>) evaluable).shortcut() ? "or" : "anyOf";
      }
    },
    NOT {
      @Override
      String formName(Evaluable<?> evaluable) {
        return "not";
      }

      @Override
      boolean isSquashableWith(Impl nextEntry) {
        return Objects.equals(LEAF, nextEntry.evaluableIo().evaluableType());
      }
    },
    LEAF {
      @Override
      String formName(Evaluable<?> evaluable) {
        return evaluable.toString();
      }
    },
    FUNCTION {
      @Override
      String formName(Evaluable<?> evaluable) {
        if (DebuggingUtils.showEvaluableDetail()) {
          if (!((Evaluable.Func<?>) evaluable).tail().isPresent())
            return ((Evaluable.Func<?>) evaluable).head().toString();
          return ((Evaluable.Func<?>) evaluable).head().toString() + "(" + ((Evaluable.Func<?>) evaluable).tail().get() + ")";
        }
        return ((Evaluable.Func<?>) evaluable).head().toString();
      }
    };

    abstract String formName(Evaluable<?> evaluable);

    boolean isSquashableWith(Impl nextEntry) {
      return false;
    }
  }

  static class Finalized extends EvaluationEntry {
    final         Object  outputActualValue;
    final         Object  detailOutputActualValue;
    private final boolean requiresExplanation;
    private final boolean ignored;

    Finalized(
        String formName,
        Type type,
        int level,
        Object inputExpectation_, Object detailInputExpectation_,
        Object outputExpectation, Object detailOutputExpectation,
        Object inputActualValue, Object detailInputActualValue,
        Object outputActualValue, Object detailOutputActualValue,
        boolean squashable, boolean requiresExplanation, boolean ignored) {
      super(
          formName, type, level,
          inputExpectation_, detailInputExpectation_,
          outputExpectation, detailOutputExpectation,
          inputActualValue, detailInputActualValue, squashable);
      this.outputActualValue = outputActualValue;
      this.detailOutputActualValue = detailOutputActualValue;
      this.requiresExplanation = requiresExplanation;
      this.ignored = ignored;
    }

    @Override
    public Object outputActualValue() {
      return outputActualValue;
    }

    @Override
    public Object detailOutputActualValue() {
      return this.detailOutputActualValue;
    }

    @Override
    public boolean ignored() {
      return this.ignored;
    }

    @Override
    public boolean requiresExplanation() {
      return this.requiresExplanation;
    }
  }

  public static EvaluationEntry create(
      String formName, Type type,
      int level,
      Object inputExpectation_, Object detailInputExpectation_,
      Object outputExpectation, Object detailOutputExpectation,
      Object inputActualValue, Object detailInputActualValue,
      Object outputActualValue, Object detailOutputActualValue,
      boolean trivial, boolean requiresExplanation, boolean ignored) {
    return new Finalized(
        formName, type,
        level,
        inputExpectation_, detailInputExpectation_,
        outputExpectation, detailOutputExpectation,
        inputActualValue, detailInputActualValue,
        outputActualValue, detailOutputActualValue,
        trivial, requiresExplanation, ignored
    );
  }

  public static class Impl extends EvaluationEntry {

    private final EvaluableIo<?, ?, ?> evaluableIo;
    private final boolean              expectationFlipped;
    private       boolean              ignored;

    private boolean finalized = false;
    private Object  outputActualValue;
    private Object  detailOutputActualValue;

    <T, E extends Evaluable<T>> Impl(
        EvaluationContext<T> evaluationContext,
        EvaluableIo<T, E, ?> evaluableIo) {
      super(
          EvaluationContext.formNameOf(evaluableIo),
          evaluableIo.evaluableType(),
          evaluationContext.visitorLineage.size(),
          computeInputExpectation(evaluableIo),                   // inputExpectation        == inputActualValue
          explainInputExpectation(evaluableIo),                   // detailInputExpectation  == detailInputActualValue
          null, // not necessary                                  // outputExpectation
          explainOutputExpectation(evaluableIo.evaluable(), evaluableIo),      // detailOutputExpectation
          computeInputActualValue(evaluableIo),                   // inputActualValue
          explainInputActualValue(evaluableIo.evaluable(), computeInputActualValue(evaluableIo)), // detailInputActualValue
          evaluableIo.evaluable().isSquashable());
      this.evaluableIo = evaluableIo;
      this.expectationFlipped = evaluationContext.isExpectationFlipped();
      this.ignored = false;
    }

    private static <E extends Evaluable<T>, T> Object explainInputExpectation(EvaluableIo<T, E, ?> evaluableIo) {
      return explainInputActualValue(evaluableIo, computeInputExpectation(evaluableIo));
    }

    private static <E extends Evaluable<T>, T> Object computeInputExpectation(EvaluableIo<T, E, ?> evaluableIo) {
      return computeInputActualValue(evaluableIo);
    }

    @Override
    public boolean requiresExplanation() {
      return isExplanationRequired(evaluableIo(), this.expectationFlipped);
    }

    @SuppressWarnings("unchecked")
    public <I, O> EvaluableIo<I, Evaluable<I>, O> evaluableIo() {
      return (EvaluableIo<I, Evaluable<I>, O>) this.evaluableIo;
    }

    public Object outputExpectation() {
      assert finalized;
      return outputExpectation;
    }

    @Override
    public Object outputActualValue() {
      assert finalized;
      return outputActualValue;
    }

    @Override
    public Object detailOutputActualValue() {
      assert finalized;
      return detailOutputActualValue;
    }

    public boolean ignored() {
      assert finalized;
      return this.ignored;
    }

    public boolean isSquashable(EvaluationEntry nextEntry) {
      if (nextEntry instanceof Impl)
        return this.type().isSquashableWith((Impl) nextEntry);
      return false;
    }

    public String formName() {
      if (DebuggingUtils.showEvaluableDetail())
        return evaluableIo.formName() + "(" +
            evaluableIo.evaluableType() + ":" +
            evaluableIo.input().creatorFormType() + ":" +
            evaluableIo.output().creatorFormType() +
            (finalized && this.ignored() ? ":ignored" : "") + ")";
      return this.evaluableIo.formName();
    }

    public void finalizeValues() {
      this.outputExpectation = computeOutputExpectation(evaluableIo(), expectationFlipped);
      this.outputActualValue = computeOutputActualValue(evaluableIo());
      this.detailOutputActualValue = explainActual(evaluableIo());
      this.ignored =
          (this.evaluableIo.evaluableType() == TRANSFORM_AND_CHECK && this.evaluableIo.formName().equals("transformAndCheck")) ||
              (this.evaluableIo.evaluableType() == FUNCTION && this.evaluableIo.output().creatorFormType() == FUNC_TAIL);
      this.finalized = true;
    }

    @Override
    public String toString() {
      return String.format("%s(%s)=%s (expected:=%s):%s", formName(), inputActualValue(), finalized ? outputActualValue() : "(n/a)", finalized ? outputExpectation() : "(n/a)", this.level());
    }
  }
}
