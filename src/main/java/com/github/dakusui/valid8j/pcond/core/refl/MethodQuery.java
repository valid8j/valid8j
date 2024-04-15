package com.github.dakusui.valid8j.pcond.core.refl;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.github.dakusui.valid8j.pcond.core.refl.ReflUtils.replacePlaceHolderWithActualArgument;
import static com.github.dakusui.valid8j.pcond.internals.InternalChecks.requireArgument;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;

/**
 * An interface that models a query to specify a method.
 */
public interface MethodQuery {
  boolean isStatic();

  Object targetObject();

  Class<?> targetClass();

  String methodName();

  Object[] arguments();

  /**
   * Returns a string that describes this object.
   *
   * @return A description of this object.
   */
  String describe();

  default MethodQuery bindActualArguments(Predicate<Object> isPlaceHolder, Function<Object, Object> replace) {
    Function<Object, Object> argReplacer = object -> replacePlaceHolderWithActualArgument(object, isPlaceHolder, replace);
    Object targetObject = argReplacer.apply(this.targetObject());
    return create(
        this.isStatic(),
        targetObject,
        this.isStatic() ? this.targetClass() : targetObject.getClass(),
        this.methodName(),
        Arrays.stream(this.arguments()).map(argReplacer).toArray());
  }

  static MethodQuery instanceMethod(Object targetObject, String methodName, Object... arguments) {
    return create(false, requireNonNull(targetObject), ReflUtils.targetTypeOf(targetObject), methodName, arguments);
  }

  /**
   * Create a `MethodQuery` object to search matching static methods.
   *
   * @param targetClass A class from which a method is searched.
   * @param methodName  A name of a method to be searched.
   * @param arguments   Argument values;
   * @return A `MethodQuery` object.
   */
  static MethodQuery classMethod(Class<?> targetClass, String methodName, Object... arguments) {
    return create(true, null, targetClass, methodName, arguments);
  }

  static MethodQuery create(boolean isStatic, Object targetObject, Class<?> targetClass, String methodName, Object[] arguments) {
    requireNonNull(targetClass);
    requireNonNull(arguments);
    requireNonNull(methodName);
    if (isStatic)
      requireArgument(targetObject, Objects::isNull, () -> "targetObject must be null when isStatic is true.");
    else {
      requireNonNull(targetObject);
      requireArgument(targetObject, v -> targetClass.isAssignableFrom(v.getClass()), () -> format("Incompatible object '%s' was given it needs to be assignable to '%s'.", targetObject, targetClass.getName()));
    }

    return new MethodQuery() {
      @Override
      public boolean isStatic() {
        return isStatic;
      }

      @Override
      public Object targetObject() {
        return targetObject;
      }

      @Override
      public String methodName() {
        return methodName;
      }

      @Override
      public Class<?> targetClass() {
        return targetClass;
      }

      @Override
      public Object[] arguments() {
        return arguments;
      }

      @Override
      public String describe() {
        Function<Object, String> parameterFormatter = s -> "<" + s + ">";
        return format("%s.%s(%s)",
            isStatic ?
                targetClass.getName() :
                parameterFormatter.apply(targetObject),
            methodName,
            Arrays.stream(arguments)
                .map(parameterFormatter)
                .collect(joining(",")));
      }
    };
  }
}
