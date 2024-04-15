package com.github.dakusui.valid8j.pcond.core;

import com.github.dakusui.valid8j.pcond.experimentals.currying.context.CurriedContext;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * An interface that models "forms".
 * A form is a general idea that covers predicates, functions, and "special-forms".
 *
 * A simple form (such as one returned by {@link Predicate#isEqual(Object)}) is
 * modeled as and held by {@link LeafPred}.
 * The framework just delegates the evaluation to the object.
 *
 * However, for a form which has an internal structure, such as one returned by
 * {@link Predicate#and(Predicate)}, this approach doesn't work.
 * Because, in order to make the evaluation process visible and readable for human,
 * we need intermediate evaluation results.
 *
 * That is, when we evaluate a form `v != null && v.startsWith("hello")`, we want
 * information about which predicate was violated.
 * Just showing the actual value of `v` is not sufficient, because the `v` and the
 * predicates in the evaluation might have internal structures or logics that make
 * it difficult/impossible to infer which predicate is violated.
 *
 * @param <T> The type of the value evaluated by this object.
 */
public interface Evaluable<T> {
  /**
   * Performs an evaluation of the `evaluationContext` with a given `evaluator`.
   *
   * @param evaluableIo       An execution occurrence of an evaluable.
   * @param evaluationContext An evaluation context.
   * @param evaluator         An evaluator with which the `evaluationContext` is evaluated.
   */
  <O> void accept(EvaluableIo<T, Evaluable<T>, O> evaluableIo, EvaluationContext<T> evaluationContext, Evaluator evaluator);

  default boolean isSquashable() {
    return false;
  }

  default Evaluable<T> makeTrivial() {
    throw new UnsupportedOperationException();
  }

  /**
   * A base interface to model all the predicates in the model of the evaluation
   * framework.
   *
   * @param <T> The type of the value to be tested.
   */
  interface Pred<T> extends Evaluable<T> {
  }

  /**
   * A base interface for conjunction (and) and disjunction (or) of predicates.
   *
   * @param <T> The type of the value to be evaluated.
   */
  interface Composite<T> extends Pred<T> {
    /**
     * Returns the predicates with which the target value is evaluated.
     *
     * @return The list of the child predicates.
     */
    List<Evaluable<T>> children();

    /**
     * Returns `true` if the "shortcut" evaluation is enabled.
     *
     * Suppose you have a following predicate.
     *
     * ----
     * a && b && c
     * ----
     *
     * If the `a` results in `false`, the `b` and `c` doesn't need to be evaluated.
     * The optimization, where the evaluations for the `b` and `c` are skipped,
     * is called "shortcut".
     *
     * However, in the context of testing, sometimes we want to know the evaluation
     * results for the `b` and `c`.
     * Otherwise, we cannot avoid getting into a fail->fix->run->fail... loop,
     * sometimes.
     *
     * @return `true` if the "shortcut" evaluation is enabled.
     */
    boolean shortcut();

    @Override
    default boolean isSquashable() {
      return children().size() <= 1;
    }
  }

  /**
   * An interface to model a conjunction (`and`, `&&`) predicate.
   *
   * @param <T> The type of the value to be evaluated.
   */
  interface Conjunction<T> extends Composite<T> {
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    default <O> void accept(EvaluableIo<T, Evaluable<T>, O> evaluableIo, EvaluationContext<T> evaluationContext, Evaluator evaluator) {
      evaluator.evaluateConjunction((EvaluableIo<T, Conjunction<T>, Boolean>) (EvaluableIo) evaluableIo, evaluationContext);
    }
  }

  /**
   * An interface to model a disjunction (`or`, `||`) predicate.
   *
   * @param <T> The type of the value to be evaluated.
   */
  interface Disjunction<T> extends Composite<T> {
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    default <O> void accept(EvaluableIo<T, Evaluable<T>, O> evaluableIo, EvaluationContext<T> evaluationContext, Evaluator evaluator) {
      evaluator.evaluateDisjunction((EvaluableIo<T, Disjunction<T>, Boolean>) (EvaluableIo) evaluableIo, evaluationContext);
    }
  }

  /**
   * An interface to model a negation (`not`, `negate`, `!`) of a predicate.
   *
   * @param <T> The type of the value to be evaluated.
   */
  interface Negation<T> extends Pred<T> {
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    default <O> void accept(EvaluableIo<T, Evaluable<T>, O> evaluableIo, EvaluationContext<T> evaluationContext, Evaluator evaluator) {
      evaluator.evaluateNegation((EvaluableIo<T, Negation<T>, Boolean>) (EvaluableIo) evaluableIo, evaluationContext);
    }

    /**
     * The predicate the negation is applied.
     *
     * @return A target predicate.
     */
    Evaluable<T> target();

    @Override
    default boolean isSquashable() {
      return true;
    }
  }

  /**
   * An interface to model a simple predicate in the evaluation framework.
   *
   * @param <T> The type of the value to be evaluated.
   */
  interface LeafPred<T> extends Pred<T> {
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    default <O> void accept(EvaluableIo<T, Evaluable<T>, O> evaluableIo, EvaluationContext<T> evaluationContext, Evaluator evaluator) {
      evaluator.evaluateLeaf((EvaluableIo<T, LeafPred<T>, Boolean>) (EvaluableIo) evaluableIo, evaluationContext);
    }

    /**
     * Returns an actual predicate modeled by this interface.
     *
     * @return The predicate modeled by this interface.
     */
    Predicate<? super T> predicate();
  }

  /**
   * An interface to model a predicate for {@link CurriedContext}.
   *
   * @see CurriedContext
   */
  interface CurriedContextPred extends Pred<CurriedContext> {
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    default <O> void accept(EvaluableIo<CurriedContext, Evaluable<CurriedContext>, O> evaluableIo, EvaluationContext<CurriedContext> evaluationContext, Evaluator evaluator) {
      evaluator.evaluateCurriedContextPredicate((EvaluableIo<CurriedContext, CurriedContextPred, Boolean>) (EvaluableIo) evaluableIo, evaluationContext);
    }

    <T> Evaluable<T> enclosed();

    int argIndex();
  }

  /**
   * An interface to model a predicate for {@link Stream}.
   *
   * @param <E> The type of elements in the stream to be evaluated.
   */
  interface StreamPred<E> extends Pred<Stream<E>> {
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    default <O> void accept(EvaluableIo<Stream<E>, Evaluable<Stream<E>>, O> evaluableIo, EvaluationContext<Stream<E>> evaluationContext, Evaluator evaluator) {
      evaluator.evaluateStreamPredicate((EvaluableIo<Stream<E>, StreamPred<E>, Boolean>) (EvaluableIo) evaluableIo, evaluationContext);
    }

    /**
     * Returns a default value returned as the entire result of this predicate.
     * The `value` is used when a "cut" happens.
     *
     * @return a default value to fallback of this object.
     */
    boolean defaultValue();

    /**
     * Returns an evaluable object which makes "cut" happen.
     * If the result of the evaluation of the returned object becomes equal to the
     * returned value of the {@link StreamPred#valueToCut()}, a "cut" will actually happen.
     *
     * @return An evaluable which triggers a "cut".
     */
    Evaluable<E> cut();

    /**
     * Returns a value to make a "cut" happen.
     *
     * A "cut" is a situation, where an evaluation process for the elements in the
     * stream is ended without reaching the last one.
     * This is necessary to model a functionalities of `Stream`, such as
     * `allMatch`, `noneMatch`, and `anyMatch`.
     *
     * @return value ( `true` / `false` ) to make a "cut" happen.
     */
    boolean valueToCut();

    /**
     * In order to generate an informative report, the framework needs information
     * about the expected value for each predicate.
     *
     * The "expected" value of a predicate can be different inside the tree of the `Evaluables`,
     * when a negation is used.
     *
     * If this `Evaluable` node requests to flip the expectation value under the node,
     * this method should return `true`.
     *
     * @return `true`, if the expectation flip is requested.
     */
    default boolean requestExpectationFlip() {
      return false;
    }  }

  /**
   * An interface to model a "transforming predicate", which models the "transform and check" style of value validation.
   * The idea of the style is to first  transform a value into a type, which is easy to read for human and to check for machine, such as list, integer, string, etc., in order to validate a value.
   *
   * @param <T> The type of the value to be evaluated.
   * @param <R> The type to which the value (`T`) is transformed and then tested.
   */
  interface Transformation<T, R> extends Pred<T> {
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    default <O> void accept(EvaluableIo<T, Evaluable<T>, O> evaluableIo, EvaluationContext<T> evaluationContext, Evaluator evaluator) {
      evaluator.evaluateTransformation((EvaluableIo<T, Transformation<T, R>, Boolean>) (EvaluableIo) evaluableIo, evaluationContext);
    }

    /**
     * Returns a transformer of this object.
     *
     * @return A transformer function.
     */
    Evaluable<T> mapper();

    Evaluable<R> checker();

    /**
     * Returns a name of a transformer, if any.
     *
     * @return An optional to store a name of the transformer.
     */
    Optional<String> mapperName();

    /**
     * Returns a name of a checker, if any.
     *
     * @return An optional to store a name of the checker.
     */
    Optional<String> checkerName();
  }

  /**
   * An interface to model a function ({@link Function}) in the evaluation framework
   * of the `pcond`.
   *
   * @param <T> The type of the value to be evaluated.
   */
  interface Func<T> extends Evaluable<T> {
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    default <O> void accept(EvaluableIo<T, Evaluable<T>, O> evaluableIo, EvaluationContext<T> evaluationContext, Evaluator evaluator) {
      evaluator.evaluateFunction((EvaluableIo<T, Func<T>, O>) (EvaluableIo) evaluableIo, evaluationContext);
    }

    Function<? super T, Object> head();

    Optional<Evaluable<Object>> tail();
  }
}
