package com.github.dakusui.valid8j.classic;

import com.github.dakusui.valid8j.pcond.fluent.ListHolder;
import com.github.dakusui.valid8j.pcond.fluent.Statement;
import com.github.dakusui.valid8j.pcond.validator.Validator;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static java.util.stream.Collectors.toList;

/**
 * An entry-point class for test assertions.
 * You can use the methods to replace the usages of the methods the same names of the `Hamcrest`.
 *
 * Instead of the `Matcher` s of `Hamcrest`, you can use just simple functions and predicates.
 *
 * There are pre-defined printable functions and predicates in the {@link com.github.dakusui.valid8j.pcond.forms.Functions}
 * class and {@link com.github.dakusui.valid8j.pcond.forms.Predicates} class.
 *
 * You can build your matchers by composing them using methods in the Java's
 * {@link Function} and {@link Predicate} such as {@link Function#andThen(Function)},
 * {@link Function#compose(Function)}, {@link Predicate#and(Predicate)}, {@link Predicate#or(Predicate)},
 * and {@link Predicate#negate()}.
 *
 * To create your own function or predicate that renders a human-readable message,
 * you can use the `function` and `predicate` methods defined in the `Printables`
 * class.
 *
 * {@link com.github.dakusui.valid8j.pcond.forms.Predicates#transform(Function)} is a method
 * to build a predicate, which first transforms a value into a type that the `pcond` offers
 * a rich support, such as {@link String}, {@link List}, {@link Comparable}, etc.,
 * and the applies predicates to the transformed value.
 * With this approach, you will not need to create your own matchers, but just create
 * a function that transform your class into easily verifiable types for your verification.
 *
 * Each method, which ends with `Statement` or `all`, in this class accepts {@link Statement} objects.
 * To create a {@link Statement} object, you can call static methods in {@link Statement} itself.
 * class such as {@link Statement#booleanValue(Boolean)}, {@link Statement#stringValue(String)},
 * etc.
 *
 * @see com.github.dakusui.valid8j.pcond.forms.Predicates
 * @see com.github.dakusui.valid8j.pcond.forms.Functions
 * @see com.github.dakusui.valid8j.pcond.forms.Predicates#transform(Function)
 * @see com.github.dakusui.valid8j.pcond.forms.Printables#predicate(String, Predicate)
 * @see com.github.dakusui.valid8j.pcond.forms.Printables#function(String, Function)
 * @see com.github.dakusui.valid8j.pcond.forms.Printables#predicate(Supplier, Predicate)
 * @see com.github.dakusui.valid8j.pcond.forms.Printables#function(Supplier, Function)
 */
public enum TestAssertions {

  ;

  /**
   * A method to check a given `value` satisfies a condition `predicate`, to be verified by the test.
   * If it is not satisfied, the test should fail.
   *
   * @param value     The value to be checked.
   * @param predicate A condition to check the `value`.
   * @param <T>       The type of the `value`.
   */
  public static <T> void assertThat(T value, Predicate<? super T> predicate) {
    Validator.instance().assertThat(value, predicate);
  }

  /**
   * A method to check a given `value` satisfies a condition `predicate`, which is required by the *test's design* to execute it.
   * If it is not satisfied, that means, the value violates an assumption of the test, therefore the test should be ignored, not fail.
   * If you are using *JUnit4*, an `AssumptionViolatedException` should be thrown.
   *
   * @param value     The value to be checked.
   * @param predicate A condition to check the `value`.
   * @param <T>       The type of the `value`.
   */
  public static <T> void assumeThat(T value, Predicate<? super T> predicate) {
    Validator.instance().assumeThat(value, predicate);
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
   * You can use {@link TestAssertions#assertStatement(Statement)}, if you have only one statement to be verified, for readability's sake.
   *
   * @param statements Statements to be verified
   * @see TestAssertions#assertStatement(Statement)
   */
  public static void assertAll(Statement<?>... statements) {
    List<?> values = Arrays.stream(statements).map(Statement::statementValue).collect(toList());
    TestAssertions.assertThat(ListHolder.fromList(values), Statement.createPredicateForAllOf(statements));
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
   * You can use {@link TestAssertions#assumeStatement(Statement)}}, if you have only one statement to be verified, for readability's sake.
   *
   * @param statements Statements to be verified
   * @see TestAssertions#assumeStatement(Statement)
   */
  public static void assumeAll(Statement<?>... statements) {
    List<?> values = Arrays.stream(statements).map(Statement::statementValue).collect(toList());
    TestAssertions.assumeThat(ListHolder.fromList(values), Statement.createPredicateForAllOf(statements));
  }
}
