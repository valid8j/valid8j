package com.github.dakusui.valid8j;

import com.github.dakusui.valid8j_pcond.validator.Validator;

import java.util.function.Predicate;

/**
 * A class that offers entry-points for checking "postconditions" with a normal
 * conditional statement of Java language.
 * That is, `if` or ternary operator, not an `assert` statement.
 * This means, the user of your product is not able to disable the checks you
 * write at runtime, unlike ones written using `assert` statements.
 *
 * The author of library thinks these methods should be used in "public facing"
 * methods, when you should claim that the "external" party introduced the bug.
 *
 * The author of library thinks these methods should be used in "public facing"
 * context when the provider of the value to be examined introduced
 * a bug, which makes the condition `false`.
 *
 * For instance, it is an intended use-case, where you call `ensure` at the end of
 * your method in order to express a condition to be satisfied by the value returned
 * from your method.
 */
public enum Ensures {
  ;

  public static <T> T ensureNonNull(T value) {
    return Validator.instance().ensureNonNull(value);
  }

  public static <T> T ensureState(T value, Predicate<? super T> cond) {
    return Validator.instance().ensureState(value, cond);
  }

  public static <T> T ensure(T value, Predicate<? super T> cond) {
    return Validator.instance().ensure(value, cond);
  }
}
