package com.github.dakusui.valid8j.pcond.experimentals.currying;

import com.github.dakusui.valid8j.pcond.internals.InternalChecks;

import java.util.function.Supplier;

import static com.github.dakusui.valid8j.pcond.internals.InternalUtils.formatObject;
import static com.github.dakusui.valid8j.pcond.internals.InternalUtils.wrapperClassOf;

/**
 * A utility class for checking values that the "currying" mechanism of the `pcond`
 * library processes.
 */
public enum Checks {
  ;

  static <T extends CurriedFunction<?, ?>> T requireLast(T value) {
    if (value.hasNext())
      throw new IllegalStateException();
    return value;
  }

  /**
   * Validates if a given argument value is appropriate for a parameter type (`paramType`).
   *
   * @param arg              An argument value is to check with `paramType`.
   * @param paramType        An expected type for `arg`.
   * @param messageFormatter A message formatter which generates a message on a failure.
   * @param <T>              The type of the argument value.
   * @return The `arg` value itself.
   */
  static <T> T validateArgumentType(T arg, Class<?> paramType, Supplier<String> messageFormatter) {
    InternalChecks.checkArgument(isValidValueForType(arg, paramType), messageFormatter);
    return arg;
  }

  static boolean isValidValueForType(Object arg, Class<?> paramType) {
    if (paramType.isPrimitive()) {
      if (arg == null)
        return paramType.equals(void.class);
      if (InternalChecks.isPrimitiveWrapperClassOrPrimitive(arg.getClass())) {
        Class<?> wrapperClassForParamType = wrapperClassOf(paramType);
        if (wrapperClassForParamType.equals(arg.getClass()))
          return true;
        return InternalChecks.isWiderThan(wrapperClassForParamType, arg.getClass());
      }
      return false;
    } else {
      if (arg == null)
        return true;
      return paramType.isAssignableFrom(arg.getClass());
    }
  }

  @SuppressWarnings("unchecked")
  static <T> T ensureReturnedValueType(Object value, Class<?> returnType) {
    if (isValidValueForType(value, returnType))
      return (T) value;
    else
      throw new IllegalStateException("Returned value:"
          + formatObject(value)
          + (value != null ? "(" + value.getClass().getName() + ")" : "")
          + " is neither null nor an instance of " + returnType.getName() + ". ");
  }
}
