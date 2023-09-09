package com.github.dakusui.thincrest;

import com.github.dakusui.valid8j_pcond.validator.Validator;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * An entry-point class for test assertions.
 * You can use the methods to replace the usages of the methods the same names of the `Hamcrest`.
 *
 * Instead of the `Matcher` s of `Hamcrest`, you can use just simple functions and predicates.
 *
 * There are pre-defined printable functions and predicates in the {@link com.github.dakusui.valid8j_pcond.forms.Functions}
 * class and {@link com.github.dakusui.valid8j_pcond.forms.Predicates} class.
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
 * {@link com.github.dakusui.valid8j_pcond.forms.Predicates#transform(Function)} is a method
 * to build a predicate, which first transforms a value into a type that the `pcond` offers
 * a rich support, such as {@link String}, {@link java.util.List}, {@link Comparable}, etc.,
 * and the applies predicates to the transformed value.
 * With this approach, you will not need to create your own matchers, but just create
 * a function that transform your class into easily verifiable types for your verification.
 *
 *
 * @see com.github.dakusui.valid8j_pcond.forms.Predicates
 * @see com.github.dakusui.valid8j_pcond.forms.Functions
 * @see com.github.dakusui.valid8j_pcond.forms.Predicates#transform(Function)
 * @see com.github.dakusui.valid8j_pcond.forms.Printables#predicate(String, Predicate)
 * @see com.github.dakusui.valid8j_pcond.forms.Printables#function(String, Function)
 * @see com.github.dakusui.valid8j_pcond.forms.Printables#predicate(Supplier, Predicate)
 * @see com.github.dakusui.valid8j_pcond.forms.Printables#function(Supplier, Function)
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
}
