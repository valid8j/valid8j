package com.github.valid8j.pcond.validator;

import com.github.valid8j.pcond.internals.InternalUtils;

import java.util.function.Predicate;

import static com.github.valid8j.pcond.internals.InternalUtils.formatObject;
import static java.lang.String.format;

/**
 * An interface that defines methods to compose a message when a value violates
 * a given condition based on a context.
 */
public interface MessageComposer {
  /**
   * Compose a message string for a `value`, which violates a precondition given as `predicate`.
   *
   * @param value     A value for which a message is created.
   * @param predicate A condition that a given `value` violated.
   * @param <T>       The type of the `value`.
   * @return A composed message string.
   */
  <T> String composeMessageForPrecondition(T value, Predicate<? super T> predicate);

  /**
   * Compose a message string for a `value`, which violates a postcondition given as `predicate`.
   *
   * @param value     A value for which a message is created.
   * @param predicate A condition that a given `value` violated.
   * @param <T>       The type of the `value`.
   * @return A composed message string.
   */
  <T> String composeMessageForPostcondition(T value, Predicate<? super T> predicate);

  /**
   * Compose a message string for a `value`, which violates a general condition given as `predicate`.
   * Used for invariant conditions, test assertion (`assertThat`), and test prerequisition
   * checking (`assumeThat`).
   *
   * @param value     A value for which a message is created.
   * @param predicate A condition that a given `value` violated.
   * @param <T>       The type of the `value`.
   * @return A composed message string.
   */
  <T> String composeMessageForAssertion(T value, Predicate<? super T> predicate);

  /**
   * Compose a message string for a `value`, which violates a user input checking
   * condition given as `predicate`.
   *
   * @param value     A value for which a message is created.
   * @param predicate A condition that a given `value` violated.
   * @param <T>       The type of the `value`.
   * @return A composed message string.
   */
  <T> String composeMessageForValidation(T value, Predicate<? super T> predicate);

  /**
   * A default implementation of `MessageComposer`.
   */
  class Default implements MessageComposer {
    @Override
    public <T> String composeMessageForPrecondition(T value, Predicate<? super T> predicate) {
      return String.format("value:<%s> violated precondition:value %s", InternalUtils.formatObject(value), predicate);
    }

    @Override
    public <T> String composeMessageForPostcondition(T value, Predicate<? super T> predicate) {
      return String.format("value:<%s> violated postcondition:value %s", InternalUtils.formatObject(value), predicate);
    }

    @Override
    public <T> String composeMessageForAssertion(T value, Predicate<? super T> predicate) {
      return "Value:" + InternalUtils.formatObject(value) + " violated: " + predicate.toString();
    }

    @Override
    public <T> String composeMessageForValidation(T value, Predicate<? super T> predicate) {
      return "Value:" + InternalUtils.formatObject(value) + " violated: " + predicate.toString();
    }
  }
}
