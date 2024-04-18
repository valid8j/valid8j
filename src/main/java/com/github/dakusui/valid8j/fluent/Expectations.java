package com.github.dakusui.valid8j.fluent;

import com.github.dakusui.valid8j.classic.Assertions;
import com.github.dakusui.valid8j.classic.TestAssertions;
import com.github.dakusui.valid8j.fluent.internals.ValidationFluents;
import com.github.dakusui.valid8j.pcond.core.fluent.AbstractObjectTransformer;
import com.github.dakusui.valid8j.pcond.core.fluent.Checker;
import com.github.dakusui.valid8j.pcond.core.fluent.Matcher;
import com.github.dakusui.valid8j.pcond.core.fluent.Transformer;
import com.github.dakusui.valid8j.pcond.core.fluent.builtins.*;
import com.github.dakusui.valid8j.pcond.fluent.ListHolder;
import com.github.dakusui.valid8j.pcond.fluent.Statement;
import com.github.dakusui.valid8j.pcond.validator.Validator;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.github.dakusui.valid8j.pcond.fluent.Statement.*;
import static com.github.dakusui.valid8j.pcond.internals.InternalUtils.trivialIdentityFunction;
import static java.util.stream.Collectors.toList;

/**
 * //@formatter:off
 * A facade class for the "fluent" style programming model of `valid8j` library.
 *
 * Following is an example for "overhead-less precondition checking for DbC style programming".
 *
 * [source,java]
 * ----
 * public class DbC {
 *   public void aMethod(int a) {
 *     assert Expectations.precondition(Expectations.that(a).satisfies().greaterThan(0).lessThan(100));
 *   }
 * }
 * ----
 *
 * Then, do `static import` to improve readability.
 *
 * [source,java]
 * ----
 * public class DbC {
 *   public void aMethod(int a) {
 *     assert precondition(that(a).satisfies().greaterThan(0).lessThan(0));
 *   }
 * }
 * ----
 *
 * Static methods in this class are designed so that the readability will become the best when they are static imported.
 *
 * An example for a test assertion looks like following.
 *
 * [source,java]
 * ----
 * public class TestClass {
 *   @Test
 *   public void aTestMethod() {
 *     assertStatement(that(a).satisfies().isGreaterThan(0).isLessThan(100));
 *   }
 * }
 * ----
 *
 * Also, note that `that` methods and `value` methods are synonyms to each others.
 * Use one which maximizes your code's readability.
 *
 * //@formatter:on
 */
public enum Expectations {
  ;

  /**
   * Checks if all the given `statements` are satisfied.
   *
   * Otherwise, this method throws an exception whose message describes what happened (how expectations are not satisfied.)
   * This method is supposed to be used with `assert` statement of Java and when:
   *
   * - yor are checking invariant conditions in DbC ("Design by Contract") approach.
   * - you are not interested in DbC approach.
   *
   * @param statements Statements to be asserted.
   * @return `true` if all the statements are satisfied.
   */
  public static boolean all(Statement<?>... statements) {
    List<?> values = Arrays.stream(statements).map(Statement::statementValue).collect(toList());
    return Assertions.that(values, Statement.createPredicateForAllOf(statements));
  }

  /**
   * A "singular" version of {@link Expectations#all(Statement[])}.
   *
   * Prefer this method if you only have one statement to be asserted.
   * This method is supposed to be used with `assert` statement of Java and when:
   *
   * - yor are checking invariant conditions in DbC ("Design by Contract") approach.
   * - you are not interested in DbC approach.
   *
   * @param statements to be evaluated by `assert` statement of Java.
   * @return `true` if all the given statements are satisfied.
   * @see Expectations#all(Statement[])
   */
  public static boolean $(Statement<?>... statements) {
    return all(statements);
  }

  /**
   * Checks if all the given `statements` are satisfied.
   *
   * Otherwise, this method throws an exception whose message describes what happened (how expectations are not satisfied.)
   * This method is supposed to be used with `assert` statement of Java and when yor are checking preconditions in DbC ("Design by Contract") approach.
   *
   * @param statements Statements to be asserted.
   * @return `true` if all the statements are satisfied.
   */
  public static boolean preconditions(Statement<?>... statements) {
    List<?> values = Arrays.stream(statements).map(Statement::statementValue).collect(toList());
    return Assertions.precondition(values, Statement.createPredicateForAllOf(statements));
  }

  /**
   * A "singular" version of {@link Expectations#preconditions(Statement[])}.
   *
   * Use this method if you only have one statement to be asserted.
   * This method is supposed to be used with `assert` statement of Java and when yor are checking a precondition in DbC ("Design by Contract") approach.
   *
   * @param statement to be evaluated by `assert` statement of Java.
   * @return `true` if all the given statements are satisfied.
   * @see Expectations#preconditions(Statement[])
   */
  public static boolean precondition(Statement<?> statement) {
    return preconditions(statement);
  }

  /**
   * Checks if all the given `statements` are satisfied.
   *
   * Otherwise, this method throws an exception whose message describes what happened (how expectations are not satisfied.)
   * This method is supposed to be used with `assert` statement of Java and when yor are checking preconditions in DbC ("Design by Contract") approach.
   *
   * @param statements Statements to be asserted.
   * @return `true` if all the statements are satisfied.
   */
  public static boolean invariants(Statement<?>... statements) {
    List<?> values = Arrays.stream(statements).map(Statement::statementValue).collect(toList());
    return Assertions.that(values, Statement.createPredicateForAllOf(statements));
  }

  /**
   * A singular version of {@link Expectations#preconditions(Statement[])}.
   *
   * Use this method if you only have one statement to be asserted.
   * This method is supposed to be used with `assert` statement of Java and when yor are checking a precondition in DbC ("Design by Contract") approach.
   *
   * @param statement to be evaluated by `assert` statement of Java.
   * @return `true` if all the given statements are satisfied.
   * @see Expectations#preconditions(Statement[])
   */
  public static boolean invariant(Statement<?> statement) {
    return invariants(statement);
  }

  /**
   * Checks if all the given `statements` are satisfied.
   *
   * Otherwise, this method throws an exception whose message describes what happened (how expectations are not satisfied.)
   * This method is supposed to be used with `assert` statement of Java and when yor are checking post-conditions in DbC ("Design by Contract") approach.
   *
   * @param statements Statements to be asserted.
   * @return `true` if all the statements are satisfied.
   */
  public static boolean postconditions(Statement<?>... statements) {
    List<?> values = Arrays.stream(statements).map(Statement::statementValue).collect(toList());
    return Assertions.postcondition(values, Statement.createPredicateForAllOf(statements));
  }

  /**
   * A singular version of {@link Expectations#postconditions(Statement[])}.
   *
   * Use this method if you only have one statement to be asserted.
   * This method is supposed to be used with `assert` statement of Java and when yor are checking a post-condition in DbC ("Design by Contract") approach.
   *
   * @param statement to be evaluated by `assert` statement of Java.
   * @return `true` if all the given statements are satisfied.
   * @see Expectations#postconditions(Statement[])
   */
  public static boolean postcondition(Statement<?> statement) {
    return all(statement);
  }

  /**
   * A method to check the given `statements` as preconditions.
   *
   * @param statements Preconditions to be checked.
   */
  public static void require(Statement<?>... statements) {
    ValidationFluents.requireAll(statements);
  }

  /**
   * A singular version of {@link Expectations#require(Statement[])}.
   *
   * @param statement A precondition to be checked.
   */
  public static <T> T require(Statement<T> statement) {
    ValidationFluents.requireAll(statement);
    return statement.statementValue();
  }

  /**
   * //@formatter:off
   * A method to validate a value held by the given statement.
   * If the value satisfies a predicate of the statement, it will be returned.
   * Otherwise, an exception created by `otherwise` will be thrown.
   *
   * [source, java]
   * ----
   * public class ExpectExample {
   *   public void method(String givenName, String familyName) {
   *     var firstName = expect(that(givenName).satisfies().notNull(), MyException::new);
   *     // ...
   *   }
   * }
   * ----
   *
   * This method doesn't have a plural version.
   * Use {@link Expectations#fail( Function)} method, instead.
   *
   * //@formatter:on
   *
   * @param statement A statement to be validated.
   * @return The target value held by `statement`.
   * @param <T> A type of value to be validated.
   * @see Statement
   * @see Expectations#fail(Function)
   */
  public static <T> T expect(Statement<T> statement, Function<String, Throwable> otherwise) {
    Objects.requireNonNull(statement);
    Objects.requireNonNull(otherwise);
    return fail(otherwise).unless(statement);
  }

  /**
   * //@formatter:off
   * This method is useful to specify an exception class to be thrown on a failure.
   *
   * [source, java]
   * ----
   * public class FailUnless {
   *   public void method(int i, String message) {
   *     fail(MyException::new).unless(
   *       that(i).satisfies().greaterThan(0),
   *       that(message).satisfies().notNull());
   *     // or
   *     String price = fail(MyException::new).unless(that(i).lessThan(1_000_000));
   *   }
   * }
   * ----
   *
   * //@formatter:on
   * @param fail A function that creates an exception from a given error message.
   * @return An instance of `{@link Unless} interface.
   */
  public static Unless fail(Function<String, Throwable> fail) {
    Objects.requireNonNull(fail);
    return new Unless() {
      @Override
      public <T> T unless(Statement<T> statement) {
        return Validator.instance().validate(statement.statementValue(), statement.statementPredicate(), fail);
      }
    };
  }

  /**
   * A method to check the given `statements` as invariant conditions.
   *
   * @param statements Invariant conditions.
   */
  public static void hold(Statement<?>... statements) {
    ValidationFluents.all(statements);
  }

  /**
   * A singular version of {@link Expectations#hold(Statement[])}.
   *
   * @param statement An invariant condition.
   */
  public static <T> T hold(Statement<T> statement) {
    ValidationFluents.all(statement);
    return statement.statementValue();
  }

  /**
   * A method to check the given `statements` as post-conditions.
   *
   * @param statements post-conditions.
   */
  public static void ensure(Statement<?>... statements) {
    ValidationFluents.ensureAll(statements);
  }

  /**
   * A singular version of {@link Expectations#ensure(Statement[])}.
   *
   * @param statement A post-condition.
   */
  public static <T> T ensure(Statement<T> statement) {
    return ValidationFluents.ensureStatement(statement);
  }

  /**
   * Checks if the target values of `statements` satisfy the predicates held by the `statements` as argument values.
   * By default, this method throws an {@link IllegalArgumentException} on a failure.
   *
   * @param statements statements to be validated.
   */
  public static void requireArguments(Statement<?>... statements) {
    ValidationFluents.requireArguments(statements);
  }

  /**
   * Checks if the target value of `statement` satisfies the predicate held by the `statement` as an argument value.
   * By default, this method throws an {@link IllegalArgumentException} on a failure.
   *
   * @param statement statement to be validated.
   * @param <T>       The type of the target value of the `statement`.
   * @return The target value of `statement`.
   */
  public static <T> T requireArgument(Statement<T> statement) {
    return ValidationFluents.requireArgument(statement);
  }

  /**
   * Checks if the target values of `statements` satisfy the predicates held by the `statements` as state.
   * By default, this method throws an {@link IllegalStateException} on a failure.
   *
   * @param statements statements to be validated.
   */
  public static void requireStates(Statement<?>... statements) {
    ValidationFluents.requireStates(statements);
  }

  /**
   * Checks if the target value of `statement` satisfies the predicate held by the `statement` as a state value.
   * By default, this method throws an {@link IllegalStateException} on a failure.
   *
   * @param statement statement to be validated.
   * @param <T>       The type of the target value of the `statement`.
   * @return The target value of `statement`.
   */
  public static <T> T requireState(Statement<T> statement) {
    return ValidationFluents.requireState(statement);
  }

  /**
   * Returns a transformer to build a statement for the given `value`.
   * The transformer is created by a given `transformerFunction`.
   * This method is called by the other `that(value)` methods and exposed as an entry-point for a user-defined custom transformer.
   *
   * @param value A value to be validated.
   * @param transformerFactory A function that creates
   * @return A transformer used for the validation.
   * @param <T> The type of the value.
   * @param <TX> The type of the transformer.
   * @param <V> The type of the
   * @see com.github.dakusui.valid8j.pcond.core.fluent.CustomTransformer
   */
  public static <T, TX extends Transformer<TX, V, T, T>, V extends Checker<V, T, T>> TX that(T value, Function<T, TX> transformerFactory) {
    return transformerFactory.apply(value);
  }

  /**
   * Returns a transformer to build a statement for the given `value`.
   *
   * @param value A value to be validated.
   * @return A transformer to build a statement to validate the `value`.
   * @param <T> A type of the `value`.
   */
  public static <T> ObjectTransformer<T, T> that(T value) {
    return that(value, Statement::objectValue);
  }

  /**
   * Returns a transformer to build a statement for the given list `value`.
   *
   * @param value A value to be validated.
   * @return A transformer to build a statement to validate the `value`.
   * @param <T> A type of element of the `value`.
   */
  public static <T> ListTransformer<List<T>, T> that(List<T> value) {
    return that(value, Statement::listValue);
  }

  /**
   * Returns a transformer to build a statement for the given stream `value`.
   *
   * @param value A value to be validated.
   * @return A transformer to build a statement to validate the `value`.
   * @param <T> A type of element of the `value`.
   */
  public static <T> StreamTransformer<Stream<T>, T> that(Stream<T> value) {
    return that(value, Statement::streamValue);
  }

  /**
   * Returns a transformer to build a statement for the given string `value`.
   *
   * @param value A value to be evaluated.
   * @return A transformer to build a statement to validate the `value`.
   */
  public static StringTransformer<String> that(String value) {
    return that(value, Statement::stringValue);
  }

  /**
   * Returns a transformer to build a statement for the given integer `value`.
   *
   * @param value A value to be validated.
   * @return A transformer to build a statement to validate the `value`.
   */
  public static IntegerTransformer<Integer> that(int value) {
    return that(value, Statement::integerValue);
  }

  /**
   * Returns a transformer to build a statement for the given long `value`.
   *
   * @param value A value to be validated.
   * @return A transformer to build a statement to validate the `value`.
   */
  public static LongTransformer<Long> that(long value) {
    return that(value, Statement::longValue);
  }

  /**
   * Returns a transformer to build a statement for the given short `value`.
   *
   * @param value A value to be validated.
   * @return A transformer to build a statement to validate the `value`.
   */
  public static ShortTransformer<Short> that(short value) {
    return that(value, Statement::shortValue);
  }

  /**
   * Returns a transformer to build a statement for the given double `value`.
   *
   * @param value A value to be validated.
   * @return A transformer to build a statement to validate the `value`.
   */
  public static DoubleTransformer<Double> that(double value) {
    return that(value, Statement::doubleValue);
  }

  /**
   * Returns a transformer to build a statement for the given float `value`.
   *
   * @param value A value to be validated.
   * @return A transformer to build a statement to validate the `value`.
   */
  public static FloatTransformer<Float> that(float value) {
    return that(value, Statement::floatValue);
  }

  /**
   * Returns a transformer to build a statement for the given boolean `value`.
   *
   * @param value A value to be validated.
   * @return A transformer to build a statement to validate the `value`.
   */
  public static BooleanTransformer<Boolean> that(boolean value) {
    return that(value, Statement::booleanValue);
  }

  /**
   * Returns a transformer to build a statement for the given throwable `value`.
   *
   * @param value A value to be validated.
   * @return A transformer to build a statement to validate the `value`.
   */
  public static <T extends Throwable> ThrowableTransformer<T, T> that(T value) {
    return that(value, Statement::throwableValue);
  }

  /**
   * A synonym of {@link Expectations#that(Object, Function)} method.
   *
   * @param value       A value to be validated.
   * @param transformer A function to create a transformer for the validation.
   * @param <T>         The type of `value`.
   * @param <TX>        The type of {@link Transformer}.
   * @param <V>         The type of {@link Checker}.
   * @return A transformer used for building a statement to validate the `value`.
   * @see com.github.dakusui.valid8j.pcond.core.fluent.CustomTransformer
   * @see Expectations#that(Object, Function)
   */
  public static <T, TX extends Transformer<TX, V, T, T>, V extends Checker<V, T, T>> TX value(T value, Function<T, TX> transformer) {
    return that(value, transformer);
  }

  /**
   * A synonym of {@link Expectations#that(Object)}.
   *
   * @param value A value to be validated.
   * @param <T>   The type of `value`.
   * @return A transformer used for building a statement to be evaluated.
   * @see Expectations#that(Object)
   */
  public static <T> ObjectTransformer<T, T> value(T value) {
    return value(value, Statement::objectValue);
  }

  /**
   * A synonym of {@link Expectations#that(List)}.
   *
   * @param value A list to be validated.
   * @param <T>   The type of element in `value`.
   * @return A transformer used for building a statement to be evaluated.
   * @see Expectations#that(List)
   */
  public static <T> ListTransformer<List<T>, T> value(List<T> value) {
    return value(value, Statement::listValue);
  }

  /**
   * A synonym of {@link Expectations#that(Stream)}.
   *
   * @param value A stream to be validated.
   * @param <T>   The type of element in `value`.
   * @return A transformer used for building a statement to be evaluated.
   * @see Expectations#that(Stream)
   */
  public static <T> StreamTransformer<Stream<T>, T> value(Stream<T> value) {
    return value(value, Statement::streamValue);
  }

  /**
   * A synonym of {@link Expectations#that(String)}.
   *
   * @param value A string to be validated.
   * @return A transformer used for building a statement to be evaluated.
   * @see Expectations#that(String)
   */
  public static StringTransformer<String> value(String value) {
    return value(value, Statement::stringValue);
  }

  /**
   * A synonym of {@link Expectations#that(int)}.
   *
   * @param value A `int` to be validated.
   * @return A transformer used for building a statement to be evaluated.
   * @see Expectations#that(int)
   */
  public static IntegerTransformer<Integer> value(int value) {
    return value(value, Statement::integerValue);
  }

  /**
   * A synonym of {@link Expectations#that(long)}.
   *
   * @param value A `long` to be validated.
   * @return A transformer used for building a statement to be evaluated.
   * @see Expectations#that(long)
   */
  public static LongTransformer<Long> value(long value) {
    return value(value, Statement::longValue);
  }

  /**
   * A synonym of {@link Expectations#that(short)}.
   *
   * @param value A `short` to be validated.
   * @return A transformer used for building a statement to be evaluated.
   * @see Expectations#that(short)
   */
  public static ShortTransformer<Short> value(short value) {
    return value(value, Statement::shortValue);
  }

  /**
   * A synonym of {@link Expectations#that(double)}.
   *
   * @param value A `double` to be validated.
   * @return A transformer used for building a statement to be evaluated.
   * @see Expectations#that(double)
   */
  public static DoubleTransformer<Double> value(double value) {
    return value(value, Statement::doubleValue);
  }

  /**
   * A synonym of {@link Expectations#that(float)}.
   *
   * @param value A `float` to be validated.
   * @return A transformer used for building a statement to be evaluated.
   * @see Expectations#that(float)
   */
  public static FloatTransformer<Float> value(float value) {
    return value(value, Statement::floatValue);
  }

  /**
   * A synonym of {@link Expectations#that(boolean)}.
   *
   * @param value A `boolean` to be validated.
   * @return A transformer used for building a statement to be evaluated.
   * @see Expectations#that(boolean)
   */
  public static BooleanTransformer<Boolean> value(boolean value) {
    return value(value, Statement::booleanValue);
  }

  /**
   * A synonym of {@link Expectations#that(Throwable)}.
   *
   * @param value A `boolean` to be validated.
   * @return A transformer used for building a statement to be evaluated.
   * @see Expectations#that(boolean)
   */
  public static <E extends Throwable> ThrowableTransformer<E, E> value(E value) {
    return value(value, Statement::throwableValue);
  }

  /**
   * Returns a checker to build a statement for the given `value`.
   * The checker is created by the given `checkerFactory` function.
   *
   * @param value A value to be validated.
   * @return A checker to build a statement to validate the `value`.
   */
  public static <T, V extends Checker<V, T, T>> V satisfies(T value, Function<T, V> checkerFactory) {
    return new LocalTransformer<>(() -> value, checkerFactory).satisfies();
  }

  /**
   * Returns a checker to build a statement for the given `value`.
   *
   * @param value A value to be validated.
   * @return A checker to build a statement to validate the `value`.
   */
  public static <T> ObjectChecker<T, T> satisfies(T value) {
    return satisfies(value, v -> objectValue(v).satisfies());
  }


  /**
   * Returns a checker to build a statement for the given list `value`.
   *
   * @param value A value to be validated.
   * @return A checker to build a statement to validate the `value`.
   */
  public static <T> ListChecker<List<T>, T> satisfies(List<T> value) {
    return satisfies(value, v -> listValue(v).satisfies());
  }

  /**
   * Returns a checker to build a statement for the given stream `value`.
   *
   * @param value A value to be validated.
   * @return A checker to build a statement to validate the `value`.
   */
  public static <T> StreamChecker<Stream<T>, T> satisfies(Stream<T> value) {
    return satisfies(value, v -> streamValue(v).satisfies());
  }

  /**
   * Returns a checker to build a statement for the given string `value`.
   *
   * @param value A value to be validated.
   * @return A checker to build a statement to validate the `value`.
   */
  public static StringChecker<String> satisfies(String value) {
    return satisfies(value, v -> stringValue(v).satisfies());
  }

  /**
   * Returns a checker to build a statement for the given int `value`.
   *
   * @param value A value to be validated.
   * @return A checker to build a statement to validate the `value`.
   */
  public static IntegerChecker<Integer> satisfies(int value) {
    return satisfies(value, v -> integerValue(v).satisfies());
  }

  /**
   * Returns a checker to build a statement for the given long `value`.
   *
   * @param value A value to be validated.
   * @return A checker to build a statement to validate the `value`.
   */
  public static LongChecker<Long> satisfies(long value) {
    return satisfies(value, v -> longValue(v).satisfies());
  }

  /**
   * Returns a checker to build a statement for the given short `value`.
   *
   * @param value A value to be validated.
   * @return A checker to build a statement to validate the `value`.
   */
  public static ShortChecker<Short> satisfies(short value) {
    return satisfies(value, v -> shortValue(v).satisfies());
  }

  /**
   * Returns a checker to build a statement for the given double `value`.
   *
   * @param value A value to be validated.
   * @return A checker to build a statement to validate the `value`.
   */
  public static DoubleChecker<Double> satisfies(double value) {
    return satisfies(value, v -> doubleValue(v).satisfies());
  }

  /**
   * Returns a checker to build a statement for the given float `value`.
   *
   * @param value A value to be validated.
   * @return A checker to build a statement to validate the `value`.
   */
  public static FloatChecker<Float> satisfies(float value) {
    return satisfies(value, v -> floatValue(v).satisfies());
  }

  /**
   * Returns a checker to build a statement for the given boolean `value`.
   *
   * @param value A value to be validated.
   * @return A checker to build a statement to validate the `value`.
   */
  public static BooleanChecker<Boolean> satisfies(boolean value) {
    return satisfies(value, v -> booleanValue(v).satisfies());
  }

  /**
   * Returns a statement whose target is the given `value`.
   *
   * @param value A value to be validated.
   * @return A checker to build a statement to validate the `value`.
   */
  public static <T> Statement<T> statement(T value, Predicate<T> cond) {
    return satisfies(value).predicate(cond);
  }

  /**
   * Returns a statement whose target is the given list `value`.
   *
   * @param value A value to be validated.
   * @return A checker to build a statement to validate the `value`.
   */
  public static <T> Statement<List<T>> statement(List<T> value, Predicate<List<T>> cond) {
    return satisfies(value).predicate(cond);
  }

  /**
   * Returns a statement whose target is the given stream `value`.
   *
   * @param value A value to be validated.
   * @return A checker to build a statement to validate the `value`.
   */
  public static <T> Statement<Stream<T>> statement(Stream<T> value, Predicate<Stream<T>> cond) {
    return satisfies(value).predicate(cond);
  }

  /**
   * Returns a statement whose target is the given string `value`.
   *
   * @param value A value to be validated.
   * @return A checker to build a statement to validate the `value`.
   */
  public static Statement<String> statement(String value, Predicate<String> cond) {
    return satisfies(value).predicate(cond);
  }

  /**
   * Returns a statement whose target is the given int `value`.
   *
   * @param value A value to be validated.
   * @return A checker to build a statement to validate the `value`.
   */
  public static Statement<Integer> statement(int value, Predicate<Integer> cond) {
    return satisfies(value).predicate(cond);
  }

  /**
   * Returns a statement whose target is the given long `value`.
   *
   * @param value A value to be validated.
   * @return A checker to build a statement to validate the `value`.
   */
  public static Statement<Long> statement(long value, Predicate<Long> cond) {
    return satisfies(value).predicate(cond);
  }

  /**
   * Returns a statement whose target is the given short `value`.
   *
   * @param value A value to be validated.
   * @return A checker to build a statement to validate the `value`.
   */
  public static Statement<Short> statement(short value, Predicate<Short> cond) {
    return satisfies(value).predicate(cond);
  }

  /**
   * Returns a statement whose target is the given double `value`.
   *
   * @param value A value to be validated.
   * @return A checker to build a statement to validate the `value`.
   */
  public static Statement<Double> statement(double value, Predicate<Double> cond) {
    return satisfies(value).predicate(cond);
  }

  /**
   * Returns a statement whose target is the given float `value`.
   *
   * @param value A value to be validated.
   * @return A checker to build a statement to validate the `value`.
   */
  public static Statement<Float> statement(float value, Predicate<Float> cond) {
    return satisfies(value).predicate(cond);
  }

  /**
   * Returns a statement whose target is the given boolean `value`.
   *
   * @param value A value to be validated.
   * @return A checker to build a statement to validate the `value`.
   */
  public static Statement<Boolean> statement(boolean value, Predicate<Boolean> cond) {
    return satisfies(value).predicate(cond);
  }

  /**
   * Fluent version of {@link TestAssertions#assertThat(Object, Predicate)}.
   *
   * @param statement A statement to be verified
   * @param <T>       The type of the value to be verified which a given statement holds.
   */
  public static <T> void assertStatement(Statement<T> statement) {
    TestAssertions.assertThat(statement.statementValue(), statement.statementPredicate());
  }

  /**
   * Fluent version of {@link TestAssertions#assertThat(Object, Predicate)}.
   * Use this method when you need to verify multiple values.
   *
   * You can use {@link Expectations#assertStatement(Statement)}, if you have only one statement to be verified, for readability's sake.
   *
   * @param statements Statements to be verified
   * @see Expectations#assertStatement(Statement)
   */
  public static void assertAll(Statement<?>... statements) {
    List<?> values = Arrays.stream(statements).map(Statement::statementValue).collect(toList());
    TestAssertions.assertThat(ListHolder.fromList(values), createPredicateForAllOf(statements));
  }

  /**
   * Fluent version of {@link TestAssertions#assumeThat(Object, Predicate)}.
   *
   * @param statement A statement to be verified
   */
  public static <T> void assumeStatement(Statement<T> statement) {
    TestAssertions.assumeThat(statement.statementValue(), statement.statementPredicate());
  }

  /**
   * Fluent version of {@link TestAssertions#assumeThat(Object, Predicate)}.
   * Use this method when you need to verify multiple values.
   *
   * You can use {@link Expectations#assumeStatement(Statement)}}, if you have only one statement to be verified, for readability's sake.
   *
   * @param statements Statements to be verified
   * @see Expectations#assumeStatement(Statement)
   */
  public static void assumeAll(Statement<?>... statements) {
    List<?> values = Arrays.stream(statements).map(Statement::statementValue).collect(toList());
    TestAssertions.assumeThat(ListHolder.fromList(values), createPredicateForAllOf(statements));
  }

  /**
   * An interface used as a return value of {@link Expectations#fail(Function)} method.
   */
  @FunctionalInterface
  public interface Unless {
    /**
     * Validates a given statement and if it is valid, the target value of it will be returned.
     * Otherwise, an exception should be thrown.
     *
     * @param statement A statement to be validated.
     * @param <T>       The type of the target value of `statement`.
     * @return A target value of `statement`, if the `statement` is evaluated `true`.
     */
    <T> T unless(Statement<T> statement);

    /**
     * This doesn't return any value because it cannot be determined which value should be the one to be returned.
     *
     * @param statements Statements to be validated.
     */
    default void unless(Statement<?>... statements) {
      unless(statement(
          Arrays.stream(statements).map(Statement::statementValue).collect(toList()),
          Statement.createPredicateForAllOf(statements)));
    }
  }

  private static class LocalTransformer<V extends Checker<V, T, T>, T> extends AbstractObjectTransformer.Base<LocalTransformer<V, T>, V, T, T> {
    private final Function<T, V> checkerFactory;

    protected LocalTransformer(Supplier<T> value, Function<T, V> checkerFactory) {
      super(value, trivialIdentityFunction());
      this.checkerFactory = checkerFactory;
    }

    @Override
    protected V toChecker(Function<T, T> transformFunction) {
      return checkerFactory.apply(transformFunction.apply(this.baseValue()));
    }

    @Override
    protected Matcher<?, T, T> rebase() {
      return new LocalTransformer<>(this::value, checkerFactory);
    }
  }

  public static void main(String... args) {
    int a = 100;
    assertStatement((that(a).satisfies().greaterThanOrEqualTo(0).lessThan(100)));
  }
}
