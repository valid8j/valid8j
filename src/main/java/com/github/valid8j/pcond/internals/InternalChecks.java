package com.github.valid8j.pcond.internals;

import com.github.valid8j.pcond.core.refl.ReflUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static java.lang.String.format;
import static java.util.Collections.emptySet;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toList;

public enum InternalChecks {
  ;

  private static final Set<Class<?>> PRIMITIVE_WRAPPERS = new HashSet<Class<?>>() {{
    add(Integer.class);
    add(Long.class);
    add(Boolean.class);
    add(Byte.class);
    add(Character.class);
    add(Float.class);
    add(Double.class);
    add(Short.class);
    add(Void.class);
  }};

  public static void checkArgument(boolean b, Supplier<String> messageFormatter) {
    if (!b)
      throw new IllegalArgumentException(messageFormatter.get());
  }

  public static <V> V requireArgument(V arg, Predicate<? super V> predicate, Supplier<String> messageFormatter) {
    if (!predicate.test(arg))
      throw new IllegalArgumentException(messageFormatter.get());
    return arg;
  }

  public static <V> V ensureValue(V value, Predicate<? super V> predicate, Function<V, String> messageFormatter) {
    if (!predicate.test(value))
      throw new IllegalStateException(messageFormatter.apply(value));
    return value;
  }

  public static <V> V requireState(V arg, Predicate<? super V> predicate, Function<V, String> messageFormatter) {
    if (!predicate.test(arg))
      throw new IllegalStateException(messageFormatter.apply(arg));
    return arg;
  }

  public static Method requireStaticMethod(Method method) {
    if (!Modifier.isStatic(method.getModifiers()))
      throw new IllegalArgumentException(String.format("The specified method '%s' is not static", method));
    return method;
  }

  public static List<Object> requireArgumentListSize(List<Object> args, int requiredSize) {
    if (requiredSize != args.size())
      throw new IllegalArgumentException(String.format("Wrong number of arguments are given: required: %s, actual: %s", requiredSize, args.size()));
    return args;
  }

  public static boolean isWiderThanOrEqualTo(Class<?> classA, Class<?> classB) {
    requireArgument(classA, InternalChecks::isPrimitiveWrapperClass, () -> format("'%s' is not a primitive wrapper class", classA));
    requireArgument(classB, InternalChecks::isPrimitiveWrapperClass, () -> format("'%s' is not a primitive wrapper class", classB));
    return classA.equals(classB) || isWiderThan(classA, classB);
  }

  public static boolean isPrimitiveWrapperClass(Class<?> aClass) {
    return PRIMITIVE_WRAPPERS.contains(aClass);
  }

  public static boolean isPrimitiveWrapperClassOrPrimitive(Class<?> aClass) {
    return aClass.isPrimitive() || isPrimitiveWrapperClass(aClass);
  }

  /**
   * @param classA A non-primitive type class.
   * @param classB Another non-primitive type class.
   * @return {@code true} iff {@code classA} is a "wider" wrapper class than {@code classB}.
   */
  public static boolean isWiderThan(Class<?> classA, Class<?> classB) {
    requireArgument(classA, InternalChecks::isPrimitiveWrapperClass, () -> format("'%s' is not a primitive wrapper class", classA));
    requireArgument(classB, InternalChecks::isPrimitiveWrapperClass, () -> format("'%s' is not a primitive wrapper class", classB));
    Set<Class<?>> widerBoxedClassesForClassA = widerTypesThan(classB);
    return widerBoxedClassesForClassA.contains(classA);
  }

  private static Set<Class<?>> widerTypesThan(Class<?> classB) {
    return ReflUtils.WIDER_TYPES.getOrDefault(classB, emptySet());
  }

  public static List<Integer> validateParamOrderList(List<Integer> order, int numParameters) {
    final List<Integer> paramOrder = unmodifiableList(order.stream().distinct().collect(toList()));
    requireArgument(order, o -> o.size() == paramOrder.size(), () -> "Duplicated elements are found in the 'order' argument:" + order.toString() + " " + paramOrder);
    requireArgument(order, o -> o.size() == numParameters, () -> "Inconsistent number of parameters are supplied by 'order'. Expected:" + numParameters + ", Actual: " + order.size());
    return paramOrder;
  }
}
