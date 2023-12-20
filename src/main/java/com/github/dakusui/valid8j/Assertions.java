package com.github.dakusui.valid8j;

import com.github.dakusui.valid8j_pcond.core.fluent.builtins.ObjectTransformer;
import com.github.dakusui.valid8j_pcond.fluent.Statement;
import com.github.dakusui.valid8j_pcond.validator.Validator;

import java.util.function.Predicate;

/**
 * // @formatter:off
 * Use methods in this class with the ```assert``` statement.
 *
 * [source, java]
 * ----
 * import static com.github.dakusui.pcond.functions.Predicates.isNotNull;
 * import static com.github.dakusui.valid8j.Assertions.*
 *
 * public class TestClass {
 *   public static void aMethod(Object value) {
 *     assert that(value, isNotNull());
 *   }
 * }
 * ----
 *
 * This prints a much more readable error message than one you see for a usual assertion failure.
 *
 * ----
 * Exception in thread "main" java.lang.AssertionError: Value:null violated: isNotNull
 * null -> isNotNull -> false
 * 	at com.github.dakusui.pcond.provider.impls.DefaultAssertionProvider.checkValue(DefaultAssertionProvider.java:70)
 * 	at com.github.dakusui.pcond.provider.AssertionProviderBase.checkInvariant(AssertionProviderBase.java:78)
 * 	at com.github.dakusui.valid8j.Assertions.that(Assertions.java:51)
 * 	...
 * ----
 *
 * To enable this feature, do not forget giving ```-ea``` VM option to enable assertions.
 * Otherwise, the evaluation doesn't happen completely and even printable predicates constructed the right side of the `assert` statement.
 * This means you can enable the feature during the development and disable it in the production so that you will not see no performance impact.
 *
 * // @formatter:on
 *
 * Each method in this class accepts {@link Statement} objects.
 * To create a {@code Statement} object, you can call methods in {@link com.github.dakusui.valid8j_pcond.fluent.Fluents}
 * class such as {@link Statement#booleanValue(Boolean)},
 * {@link Statement#stringValue(String)}, etc.
 */
public enum Assertions {
  ;

  public static <E> ObjectTransformer<E, E> value(E object) {
    return Statement.objectValue(object);
  }

  /**
   * A method to be used for checking a value satisfies a given invariant condition.
   * This method is intended to be used in `assert` statements.
   *
   * [source,java]
   * .Example
   * ----
   * public void aMethod(String var) {
   * assert that(var, isNotNull());
   * }
   * ----
   *
   * @param value     A value to be checked.
   * @param predicate An invariant condition to check the {@code value}.
   * @param <T>       The type of {@code value}.
   * @return {@code true}, if the condition given as {@code predicate} is satisfied.
   */
  public static <T> boolean that(T value, Predicate<? super T> predicate) {
    Validator.instance().checkInvariant(value, predicate);
    return trueValue();
  }

  /**
   * A method to be used for checking a value satisfies a given pre-condition.
   * This method is intended to be used in `assert` statements.
   *
   * [source,java]
   * .Example
   * ----
   * public void aMethod(String var) {
   * assert precondition(var, isNotNull());
   * }
   * ----
   *
   * @param value     A value to be checked.
   * @param predicate A pre-condition to check the {@code value}.
   * @param <T>       The type of {@code value}.
   * @return {@code true}, if the condition given as {@code predicate} is satisfied.
   */
  public static <T> boolean precondition(T value, Predicate<? super T> predicate) {
    Validator.instance().checkPrecondition(value, predicate);
    return trueValue();
  }

  /**
   * A method to be used for checking a value satisfies a given post-condition.
   * This method is intended to be used in `assert` statements.
   *
   * [source,java]
   * .Example
   * ----
   * public void aMethod(String var) {
   * assert postcondition(var, isNotNull());
   * }
   * ----
   *
   * @param value     A value to be checked.
   * @param predicate A post-condition to check the {@code value}.
   * @param <T>       The type of {@code value}.
   * @return {@code true}, if the condition given as {@code predicate} is satisfied.
   */
  public static <T> boolean postcondition(T value, Predicate<? super T> predicate) {
    Validator.instance().checkPostcondition(value, predicate);
    return trueValue();
  }

  /**
   * This method always return {@code true}.
   * This method is defined in order not to let an IDE report a warning
   * Condition 'that(methodType, Predicates.isNotNull())' is always 'true'
   * for caller codes of methods in this class.
   *
   * @return {@code true}
   */
  @SuppressWarnings("ConstantConditions")
  private static boolean trueValue() {
    return Assertions.class != null;
  }
}
