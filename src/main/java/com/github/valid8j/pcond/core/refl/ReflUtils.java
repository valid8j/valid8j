package com.github.valid8j.pcond.core.refl;

import com.github.valid8j.pcond.internals.MethodAmbiguous;
import com.github.valid8j.pcond.internals.MethodInvocationException;
import com.github.valid8j.pcond.internals.InternalUtils;
import com.github.valid8j.pcond.internals.MethodNotFound;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.*;

/**
 * This class consists of {@code static} utility methods for creating printable functions and predicate
 * on objects.
 */
public enum ReflUtils {
  ;

  public static final Map<Class<?>, Set<Class<?>>> WIDER_TYPES = new HashMap<Class<?>, Set<Class<?>>>() {
    {
      // https://docs.oracle.com/javase/specs/jls/se7/html/jls-5.html#jls-5.1.2
      put(InternalUtils.wrapperClassOf(byte.class), wrapperClassesOf(asSet(short.class, int.class, long.class, float.class, double.class)));
      put(InternalUtils.wrapperClassOf(short.class), wrapperClassesOf(asSet(int.class, long.class, float.class, double.class)));
      put(InternalUtils.wrapperClassOf(char.class), wrapperClassesOf(asSet(int.class, long.class, float.class, double.class)));
      put(InternalUtils.wrapperClassOf(int.class), wrapperClassesOf(asSet(long.class, float.class, double.class)));
      put(InternalUtils.wrapperClassOf(long.class), wrapperClassesOf(asSet(float.class, double.class)));
      put(InternalUtils.wrapperClassOf(float.class), wrapperClassesOf(asSet(double.class)));
    }

    private Set<Class<?>> wrapperClassesOf(Set<Class<?>> primitiveClasses) {
      return primitiveClasses.stream().map(InternalUtils::wrapperClassOf).collect(toSet());
    }

    private Set<Class<?>> asSet(Class<?>... classes) {
      return new HashSet<Class<?>>() {{
        addAll(asList(classes));
      }};
    }
  };

  /**
   * Invokes a method found by {@code methodQuery}.
   * All parameters in the query needs to be bound before calling this method.
   * When a query matches no or more than one methods, an exception will be thrown.
   *
   * If an exception is thrown by the method, it will be wrapped by {@link RuntimeException} and re-thrown.
   *
   * @param methodQuery A query that speficies the method to be executed.
   * @param <R>         Type of the returned value.
   * @return The value returned from the method found by the query.
   * @see MethodQuery
   * @see ReflUtils#findMethod(Class, String, Object[])
   */
  public static <R> R invokeMethod(MethodQuery methodQuery) {
    return invokeMethod(
        findMethod(methodQuery.targetClass(), methodQuery.methodName(), methodQuery.arguments()),
        methodQuery.targetObject(),
        methodQuery.arguments());
  }

  /**
   * Invokes a given {@code method} on the object with arguments passed as {@code obj} and {@code arguments}.
   *
   * @param method    A method to be invoked.
   * @param obj       An object on which the {@code method} is invoked.
   * @param arguments Arguments passed to the {@code method}.
   * @param <R>       The type of the value returned from the {@code method}.
   * @return The value returned by {@code method}.
   */
  @SuppressWarnings("unchecked")
  public static <R> R invokeMethod(Method method, Object obj, Object[] arguments) {
    boolean wasAccessible = method.isAccessible();
    try {
      ////
      // Issue-42
      // Without setting accessible, a public method defined in a private class
      // overriding a public method cannot be invoked.
      method.setAccessible(true);
      return (R) method.invoke(obj, arguments);
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new MethodInvocationException(format("Method invocation of '%s' was failed", method), e.getCause());
    } finally {
      method.setAccessible(wasAccessible);
    }
  }

  /**
   * Tries to find a method whose name is {@code methodName} from a given class {@code aClass}
   * and that can be invoked with parameter values {@code args}.
   *
   * Unless one and only one method is found appropriate, an exception will be
   * thrown.
   *
   * In this version, boxing/unboxing and casting are not attempted to determine
   * the methodto be returned during the search. This means, if there are overloaded
   * methods of the {@code methodName} that can be invoked with {@code args}, this
   * method will fail. Also even if there is a method of the {@code methodName}
   * that can be invoked if boxing/unboxing happens, this method will fail.
   *
   * @param aClass     A class from which the method is searched.
   * @param methodName A name of the method
   * @param args       Arguments which should be given to the method
   * @return A method for given class {@code aClass}, {@code method}, and {@code args}.
   */
  public static Method findMethod(Class<?> aClass, String methodName, Object[] args) {
    MethodSelector methodSelector = new MethodSelector.Default()
        .andThen(new MethodSelector.PreferNarrower())
        .andThen(new MethodSelector.PreferExact());
    return getIfOnlyOneElseThrow(
        methodSelector.select(
            Arrays.stream(aClass.getMethods())
                .filter((Method m) -> m.getName().equals(methodName))
                .collect(toMethodList()),
            args),
        () -> exceptionOnAmbiguity(aClass, methodName, args, methodSelector),
        () -> exceptionOnMethodNotFound(aClass, methodName, args, methodSelector));
  }

  private static RuntimeException exceptionOnMethodNotFound(Class<?> aClass, String methodName, Object[] args, MethodSelector methodSelector) {
    return new MethodNotFound(format(
        "Method matching '%s%s' was not found by selector=%s in %s.",
        methodName,
        asList(args),
        methodSelector,
        aClass.getCanonicalName()
    ));
  }

  private static RuntimeException exceptionOnAmbiguity(Class<?> aClass, String methodName, Object[] args, MethodSelector methodSelector) {
    return new MethodAmbiguous(format(
        "Methods matching '%s%s' were found more than one in %s by selector=%s.: %s ",
        methodName,
        asList(args),
        aClass.getCanonicalName(),
        methodSelector,
        summarizeMethods(methodSelector.select(
            Arrays.stream(aClass.getMethods())
                .filter((Method m) -> m.getName().equals(methodName))
                .collect(toMethodList()),
            args))));
  }

  static Class<?> targetTypeOf(Object targetObject) {
    requireNonNull(targetObject);
    return targetObject instanceof Parameter ?
        Object.class :
        targetObject.getClass();
  }

  /**
   * A collector to gather methods which have narrowest possible signatures.
   *
   * @return A collector.
   */
  private static Collector<Method, List<Method>, List<Method>> toMethodList() {
    return Collector.of(
        LinkedList::new,
        ReflUtils::addMethodIfNecessary,
        createCombinerForMethodList());
  }

  /**
   * This method is made public in order only for unit testing since with Java 8,
   * the combiner returned by this method will never be used.
   * - https://stackoverflow.com/questions/29210176/can-a-collectors-combiner-function-ever-be-used-on-sequential-streams[Can a Collector's combiner function ever be used on sequential streams?]
   *
   * @return A combiner for method list.
   */
  public static BinaryOperator<List<Method>> createCombinerForMethodList() {
    return new BinaryOperator<List<Method>>() {
      @Override
      public List<Method> apply(List<Method> methods, List<Method> methods2) {
        return new LinkedList<Method>() {{
          addAll(methods);
          methods2.forEach(each -> addMethodIfNecessary(this, each));
        }};
      }
    };
  }

  private static Method getIfOnlyOneElseThrow(List<Method> foundMethods, Supplier<RuntimeException> exceptionSupplierOnAmbiguity, Supplier<RuntimeException> exceptionSupplierOnNotFound) {
    if (foundMethods.isEmpty())
      throw exceptionSupplierOnNotFound.get();
    if (foundMethods.size() == 1)
      return foundMethods.get(0);
    throw exceptionSupplierOnAmbiguity.get();
  }

  private static List<String> summarizeMethods(List<Method> methods) {
    return methods
        .stream()
        .map(ReflUtils::summarizeMethodName)
        .collect(toList());
  }

  private static String summarizeMethodName(Method method) {
    return method.toString().replace(
        method.getDeclaringClass().getCanonicalName() + "." + method.getName(),
        method.getName());
  }

  /**
   * Add {@code method} to {@code methods} if necessary.
   * Since {@link Class#getMethods()} may return methods of the same signature when
   * a method is overridden in a sub-class with returning a narrow class in the super,
   * this consideration is necessary.
   *
   * @param methods A list of methods
   * @param method  A method to be examined if it is necessay to be added to {@code methods}.
   */
  private static void addMethodIfNecessary(List<Method> methods, Method method) {
    Optional<Method> found = methods
        .stream()
        .filter(each -> Arrays.equals(each.getParameterTypes(), method.getParameterTypes()))
        .findAny();
    if (found.isPresent()) {
      if (found.get().getDeclaringClass().isAssignableFrom(method.getDeclaringClass()))
        methods.remove(found.get());
    }
    methods.add(method);
  }

  static Object replacePlaceHolderWithActualArgument(Object object, Predicate<Object> isPlaceHolder, Function<Object, Object> replace) {
    if (isPlaceHolder.test(object)) {
      return replace.apply(object);
    }
    return object;
  }

  @SuppressWarnings("unchecked")
  public static <R> R invokeStaticMethod(Method method, Object[] args) {
    try {
      return (R) method.invoke(null, args);
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw InternalUtils.executionFailure(
          format("Invoked method:%s threw an exception", formatMethodName(method)),
          e instanceof InvocationTargetException ? e.getCause() : e);
    }
  }

  public static String formatMethodName(Method method) {
    return format("%s.%s(%s)",
        method.getDeclaringClass().getName(),
        method.getName(),
        Arrays.stream(method.getParameterTypes()).map(Class::getSimpleName).collect(joining(",")));
  }
}
