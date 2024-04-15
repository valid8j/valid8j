package com.github.dakusui.valid8j.pcond.forms;

import com.github.dakusui.valid8j.pcond.core.printable.PrintableFunctionFactory;
import com.github.dakusui.valid8j.pcond.core.refl.MethodQuery;
import com.github.dakusui.valid8j.pcond.core.refl.Parameter;
import com.github.dakusui.valid8j.pcond.core.refl.ReflUtils;
import com.github.dakusui.valid8j.pcond.experimentals.currying.CurriedFunction;
import com.github.dakusui.valid8j.pcond.experimentals.currying.CurryingUtils;
import com.github.dakusui.valid8j.pcond.experimentals.currying.multi.MultiFunction;
import com.github.dakusui.valid8j.pcond.experimentals.currying.multi.MultiFunctionUtils;
import com.github.dakusui.valid8j.pcond.validator.Validator;

import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.github.dakusui.valid8j.pcond.core.refl.ReflUtils.invokeMethod;
import static com.github.dakusui.valid8j.pcond.forms.Predicates.allOf;
import static com.github.dakusui.valid8j.pcond.forms.Predicates.isInstanceOf;
import static com.github.dakusui.valid8j.pcond.internals.InternalUtils.formatObject;
import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static java.util.Objects.requireNonNull;

/**
 * An entry point for acquiring function objects.
 * Functions retrieved by methods in this class are all "printable".
 */
public class Functions {
  private Functions() {
  
  }
  
  /**
   * Returns a printable function that returns a given object itself.
   *
   * @param <E> The type of the object.
   * @return The function.
   */
  public static <E> Function<E, E> identity() {
    return PrintableFunctionFactory.Simple.IDENTITY.instance();
  }
  
  /**
   * Returns a function that gives a string representation of a object given to it.
   * Internally, the returned function calls `toString` method on a given object.
   *
   * @param <E> The type of the object
   * @return The function.
   */
  public static <E> Function<E, String> stringify() {
    return PrintableFunctionFactory.Simple.STRINGIFY.instance();
  }
  
  /**
   * Returns a function that gives a length of a string passed as an argument.
   *
   * @return The function.
   */
  public static Function<? super String, Integer> length() {
    return PrintableFunctionFactory.Simple.LENGTH.instance();
  }
  
  
  @SuppressWarnings({ "unchecked", "RedundantClassCall" })
  public static <E> Function<List<E>, E> elementAt(int i) {
    return Function.class.cast(PrintableFunctionFactory.Parameterized.ELEMENT_AT.create(singletonList(i)));
  }
  
  /**
   * Returns a function that that returns a size of a given list.
   *
   * @return The function.
   */
  public static <E> Function<Collection<E>, Integer> size() {
    return PrintableFunctionFactory.Simple.SIZE.instance();
  }
  
  /**
   * Returns a function that returns a stream for a given collection.
   *
   * @param <E> Type of elements in the given collection.
   * @return The function.
   */
  public static <E> Function<Collection<? extends E>, Stream<E>> stream() {
    return PrintableFunctionFactory.Simple.STREAM.instance();
  }
  
  /**
   * Returns a function that returns a stream for a given collection.
   *
   * @param elementClass A parameter to let compiler know the type of the element in a collection.
   * @param <E>          A type of elements in a collection.
   * @return The function.
   */
  public static <E> Function<Collection<? extends E>, Stream<E>> stream(@SuppressWarnings("unused") Class<E> elementClass) {
    return stream();
  }
  
  /**
   * Returns a function that returns a stream for a given object.
   * This method corresponds to {@link Stream#of(Object)} method.
   *
   * @param <E> Type of object.
   * @return The function.
   */
  public static <E> Function<E, Stream<E>> streamOf() {
    return PrintableFunctionFactory.Simple.STREAM_OF.instance();
  }
  
  /**
   * Returns a function that casts an object into a given class.
   *
   * @param type The type to which the given object is cast
   * @param <E>  The type to which the object is case.
   * @return The function.
   */
  public static <E> Function<? super Object, E> cast(Class<E> type) {
    return PrintableFunctionFactory.Parameterized.CAST.create(singletonList(type));
  }
  
  /**
   * Returns a function that casts an object into a given class.
   * ```java
   *     assertThat(
   *         asList(lastName, fullName),
   *         allOf(
   *             transform(elementAt(0).andThen(cast(String.class))).check(allOf(isNotNull(), not(isEmptyString()))),
   *             transform(elementAt(1).andThen(castTo((List<String>)value()))).check(Predicates.contains(lastName))));
   *  ```
   *
   * @param value A type place-holder.
   *              Always use a value returned from {@link Functions#value()} method.
   * @param <E>   The type to which the object is case.
   * @return The function.
   */
  public static <E> Function<? super Object, E> castTo(@SuppressWarnings("unused") E value) {
    return PrintableFunctionFactory.Simple.CAST_TO.instance();
  }

  /**
   * Returns a function that creates and returns a list that contains all the elements in the given list.
   *
   * @param <I> The type of the input collection.
   * @param <E> Type of the elements in the collection
   * @return The function.
   */
  public static <I extends Collection<E>, E> Function<I, List<E>> collectionToList() {
    return PrintableFunctionFactory.Simple.COLLECTION_TO_LIST.instance();
  }
  
  /**
   * Returns a function that converts a given array into a list.
   *
   * @param <E> Type of elements in a given array.
   * @return The function.
   */
  public static <E> Function<E[], List<E>> arrayToList() {
    return PrintableFunctionFactory.Simple.ARRAY_TO_LIST.instance();
  }
  
  /**
   * Returns a function the counts lines in a given string.
   *
   * @return The function.
   */
  public static Function<String, Integer> countLines() {
    return PrintableFunctionFactory.Simple.COUNT_LINES.instance();
  }
  
  /**
   * //@formatter:off
   * The returned function tries to find a {@code substring} after a given string.
   * If found, it returns the result of the following statement.
   *
   * [source,java]
   * ----
   * s.substring(s.indexOf(substring) + substring.length())
   * ----
   *
   * If not found, a {@link StringIndexOutOfBoundsException} will be thrown.
   * //@formatter:on
   *
   * @param substring A substring to find in a given string.
   * @return The string after the {@code substring}.
   */
  public static Function<String, String> findString(String substring) {
    requireNonNull(substring);
    return PrintableFunctionFactory.function(
        () -> format("findString[%s]", substring),
        s -> {
          int index = s.indexOf(substring);
          if (index >= 0)
            return s.substring(s.indexOf(substring) + substring.length());
          throw new NoSuchElementException(format("'%s' was not found in '%s'", substring, s));
        });
  }
  
  /**
   * https://en.wikipedia.org/wiki/Currying[Curries] a static method specified by the given arguments.
   *
   * @param aClass         A class to which the method to be curried belongs to.
   * @param methodName     A name of the method to be curried.
   * @param parameterTypes Parameters types of the method.
   * @return A printable and curried function of the target method.
   */
  @SuppressWarnings("JavadocLinkAsPlainText")
  public static CurriedFunction<Object, Object> curry(Class<?> aClass, String methodName, Class<?>... parameterTypes) {
    return curry(multifunction(aClass, methodName, parameterTypes));
  }
  
  /**
   * Curries a given multi-function.
   *
   * @param function A multi-function to be curried
   * @return A curried function
   * @see Functions#curry(Class, String, Class[])
   */
  public static CurriedFunction<Object, Object> curry(MultiFunction<Object> function) {
    return CurryingUtils.curry(function);
  }
  
  public static <R> MultiFunction<R> multifunction(Class<?> aClass, String methodName, Class<?>... parameterTypes) {
    return MultiFunctionUtils.multifunction(IntStream.range(0, parameterTypes.length).toArray(), aClass, methodName, parameterTypes);
  }
  
  /**
   * Returns a {@link Function} created from a method specified by a {@code methodQuery}.
   * If the {@code methodQuery} matches none or more than one methods, a {@code RuntimeException} will be thrown.
   *
   * To pass an input value given to an entry point method, such as `TestAssertions.assertThat`, to the method, use a place-holder value returned by {@link Functions#parameter()}.
   *
   * @param methodQuery A query object that specifies a method to be invoked by the returned function.
   * @param <T>         the type of the input to the returned function
   * @return Created function.
   * @see Functions#classMethod(Class, String, Object[])
   * @see Functions#instanceMethod(Object, String, Object[])
   * @see Functions#parameter()
   */
  public static <T, R> Function<T, R> call(MethodQuery methodQuery) {
    return Printables.function(methodQuery.describe(), t -> invokeMethod(methodQuery.bindActualArguments((o) -> o instanceof Parameter, o -> t)));
  }
  
  
  /**
   * // @formatter:off
   * Creates a {@link MethodQuery} object from given arguments to search for {@code static} methods.
   * Note that {@code arguments} are actual argument values, not the formal parameter types.
   * The pcond library searches for the "best" matching method for you.
   * In case no matching method is found or more than one methods are found, a {@link RuntimeException}
   * will be thrown.
   *
   * In order to specify a parameter which should be passed to the returned function at applying,
   * you can use an object returned by {@link Functions#parameter} method.
   * This is useful to construct a function from an existing method.
   *
   * To pass an input value given to an entry point method, such as `TestAssertions.assertThat`, to the method, use a place-holder value returned by {@link Functions#parameter()}.
   *
   * That is, in order to create a function which computes sin using query a method {@link Math#sin(double)},
   * you can do following
   *
   * [source, java]
   * ----
   * public class Example
   *   public void buildSinFunction() {
   *     MethodQuery mq = classMethod(Math.class, "sin", parameter());
   *     Function<Double, Double> sin = call(mq);
   *     System.out.println(sin(Math.PI/2));
   *   }
   * }
   * ----
   * This prints {@code 1.0}.
   *
   * In case your arguments do not contain any {@link Parameter} object, the input
   * argument passed to the built function will be simply ignored.
   *
   * // @formatter:on
   *
   * @param targetClass A class
   * @param methodName  A method name
   * @param arguments   Arguments
   * @return A method query for static methods specified by arguments.
   * @see ReflUtils#findMethod(Class, String, Object[])
   * @see Functions#parameter()
   */
  public static MethodQuery classMethod(Class<?> targetClass, String methodName, Object... arguments) {
    return MethodQuery.classMethod(targetClass, methodName, arguments);
  }
  
  /**
   * // @formatter:off
   * Creates a {@link MethodQuery} object from given arguments to search for {@code static} methods.
   * Excepting that this method returns a query for instance methods, it is quite
   * similar to {@link Functions#classMethod(Class, String, Object[])}.
   *
   * This method is useful to build a function from an instance method.
   * That is, you can create a function which returns the length of a given string
   * from a method {@link String#length()} with a following code snippet.
   *
   * [source, java]
   * ----
   * public void buildLengthFunction() {
   *   Function<String, Integer> length = call(instanceMethod(parameter(), "length"));
   * }
   * ----
   *
   * In case the {@code targetObject} is not an instance of {@link Parameter} and {@code arguments}
   * contain no {@code Parameter} object, the function will simply ignore the input passed to it.
   *
   * To pass an input value given to an entry point method, such as `TestAssertions.assertThat`, to the method, use a place-holder value returned by {@link Functions#parameter()}.
   *
   * // @formatter:on
   *
   * @param targetObject An object on which methods matching returned query should be invoked.
   * @param methodName   A name of method.
   * @param arguments    Arguments passed to the method.
   * @return A method query for instance methods specified by arguments.
   * @see Functions#classMethod(Class, String, Object[])
   * @see Functions#parameter()
   */
  public static MethodQuery instanceMethod(Object targetObject, String methodName, Object... arguments) {
    return MethodQuery.instanceMethod(targetObject, methodName, arguments);
  }
  
  /**
   * // @formatter:off
   * A short hand method to call
   *
   * [source, java]
   * ---
   * call(instanceMethod(object, methodName, args))
   * ---
   * // @formatter:on
   *
   * To pass an input value given to an entry point method, such as `TestAssertions.assertThat`, to the method, use a place-holder value returned by {@link Functions#parameter()}.
   *
   * @param targetObject An object on which methods matching returned query should be invoked.
   * @param methodName   A name of method.
   * @param arguments    Arguments passed to the method.
   * @param <T>          The type of the input to the returned function.
   * @param <R>          The type of the output from the returned function.
   * @return The function that calls a method matching a query built from the given arguments.
   * @see Functions#call(MethodQuery)
   * @see Functions#instanceMethod(Object, String, Object[])
   * @see Functions#parameter()
   */
  private static <T, R> Function<T, R> callInstanceMethod(Object targetObject, String methodName, Object... arguments) {
    return call(instanceMethod(targetObject, methodName, arguments));
  }
  
  /**
   * Returns a function that calls a method which matches the given {@code methodName}
   * and {@code args} on the object given as input to it.
   *
   * Note that method look up is done when the predicate is applied.
   * This means this method does not throw any exception by itself and in case
   * you give wrong {@code methodName} or {@code arguments}, an exception will be
   * thrown when the returned function is applied.
   *
   * // @formatter:off
   * [source, java]
   * ----
   * public class Example {
   *   public void method() {
   *     assertThat(value, transform(call("toString")).check(isNotNull()));
   *   }
   * }
   * ----
   *
   * To pass an input value given to an entry point method, such as `TestAssertions.assertThat`, to the method, use a place-holder value returned by {@link Functions#parameter()}.
   *
   * // @formatter:on
   *
   * @param methodName The method name
   * @param arguments  Arguments passed to the method.
   * @param <T>        The type of input to the returned function
   * @param <R>        The type of output from the returned function
   * @return A function that invokes the method matching the {@code methodName} and {@code args}
   * @see Functions#parameter()
   */
  public static <T, R> Function<T, R> call(String methodName, Object... arguments) {
    return callInstanceMethod(parameter(), methodName, arguments);
  }
  
  /**
   * Returns a function that converts an input value to an exception object, which is thrown by `func`, when it is applied.
   * If it does not throw an exception, or even if thrown, it is not an instance of {@code exceptionClass}, an assertion executed inside this method will fail and an exception
   * to indicate it will be thrown.
   * The exception will be typically an {@link AssertionError}.
   *
   * @param exceptionClass An exception class to be thrown.
   * @param func           A function to be exercised
   * @param <T>            A type of exception value to be thrown by {@code func}.
   * @param <E>            An input value type of {@code func}.
   * @return A function that maps an input value to an exception.
   */
  @SuppressWarnings("unchecked")
  public static <T, E extends Throwable> Function<T, E> expectingException(Class<E> exceptionClass, Function<? super T, ?> func) {
    return Printables.function(
        () -> String.format("expectingException(%s,%s)", exceptionClass.getSimpleName(), func),
        in -> {
          Object out;
          try {
            out = func.apply(in);
          } catch (Throwable e) {
            Validator.instance().assertThat(e, isInstanceOf(exceptionClass));
            return (E) e;
          }
          Validator.instance().assertThat(
              String.format("%s(%s)->%s", func, formatObject(in, 12), formatObject(out, 12)),
              allOf(exceptionThrown(), exceptionClassWas(exceptionClass)));
          throw new AssertionError("A line that shouldn't be reached. File a ticket.");
        });
  }
  
  /**
   * Returns a {@link Parameter} object, which is used in combination with {@link Functions#instanceMethod(Object, String, Object[])},
   * {@link Functions#classMethod(Class, String, Object[])}, or their shorthand methods.
   * The object returned by this method is replaced with the actual input value passed to a function built
   * through {@link Functions#call(MethodQuery)} or {@link Predicates#callp(MethodQuery)}
   * when it is applied.
   *
   * @return a {@code Parameter} object
   * @see Functions#classMethod(Class, String, Object[])
   * @see Functions#instanceMethod(Object, String, Object[])
   * @see Functions#call(MethodQuery)
   * @see Functions#call(String, Object[])
   * @see Predicates#callp(MethodQuery)
   * @see Predicates#callp(String, Object[])
   */
  public static Parameter parameter() {
    return Parameter.INSTANCE;
  }
  
  private static Predicate<Object> exceptionThrown() {
    return Printables.predicate("exceptionThrown", v -> false);
  }
  
  private static Predicate<Object> exceptionClassWas(Class<? extends Throwable> exceptionClass) {
    return Printables.predicate(() -> "exceptionClass:" + requireNonNull(exceptionClass).getSimpleName(), v -> false);
  }

  /**
   * A method to return a value for a "casting placeholder value".
   *
   * @param <E> Type to cast to.
   * @return Casting placeholder value
   */
  public static <E> E value() {
    return null;
  }
}
