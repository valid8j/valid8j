package com.github.dakusui.valid8j.pcond.core.fluent;

import com.github.dakusui.valid8j.pcond.core.fluent.builtins.*;
import com.github.dakusui.valid8j.pcond.forms.Functions;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.github.dakusui.valid8j.pcond.core.refl.MethodQuery.classMethod;
import static com.github.dakusui.valid8j.pcond.core.refl.MethodQuery.instanceMethod;
import static com.github.dakusui.valid8j.pcond.forms.Functions.call;
import static com.github.dakusui.valid8j.pcond.forms.Functions.parameter;
import static java.util.Objects.requireNonNull;

/**
 *
 * @param <TX> The type of the extending class itself.
 * @param <V> A type of checker produced by this transformer.
 * @param <T> The type of the target value of the instance of this interface.
 * @param <R> The type of the target value of the checker instance `V`
 */
public interface AbstractObjectTransformer<
    TX extends AbstractObjectTransformer<TX, V, T, R>,
    V extends AbstractObjectChecker<V, T, R>,
    T,
    R
    > extends
    Transformer<TX, V, T, R> {

  /**
   * Corresponds to {@code toString()} method.
   *
   * @return this object the method appended.
   */
  @SuppressWarnings("unchecked")
  default StringTransformer<String> stringify() {
    return (StringTransformer<String>) this.toString(Functions.stringify());
  }
  
  default <E> ObjectTransformer<T, E> function(Function<R, E> function) {
    return this.toObject(Objects.requireNonNull(function));
  }
  
  default <E> ObjectTransformer<T, E> invoke(String methodName, Object... args) {
    return this.function(call(instanceMethod(parameter(), methodName, args)));
  }

  default <E> ObjectTransformer<T, E> invokeStatic(Class<?> klass, String methodName, Object... args) {
    return this.function(call(classMethod(klass, methodName, args)));
  }

  /**
   * Applies `f`, which is expected to throw an exception object of class `exceptionClass`, to the target value of this transformer object.
   * In case no exception is thrown, or a different type of exception is thrown, an exception will be thrown.
   * But its type is not specified.
   *
   * @param exceptionClass A class of exception object of the exception to be thrown by `f`.
   * @param f A function that is expected to throw an exception object of `exceptionClass`.
   * @return A {@link ThrowableTransformer} instance constructed for the thrown exception by `f`.
   * @param <O> An exception class to be thrown by `f`.
   * @see Functions#expectingException(Class, Function)
   */
  default <O extends Throwable> ThrowableTransformer<T, O> expectException(Class<O> exceptionClass, Function<? super R, ?> f) {
    requireNonNull(exceptionClass);
    return this.toThrowable(Functions.expectingException(exceptionClass, f));
  }

  /**
   * Mainly used for internal purposes
   * @return Another transformer objecct.
   * @param <E>
   */
  @SuppressWarnings("unchecked")
  default <E> ObjectTransformer<T, E> asObject() {
    return (ObjectTransformer<T, E>)this.toObject(Functions.identity());
  }

  /**
   * Applies a given `function` to the currently targeted value and returns a new `ObjectTransformer` whose target value is the returned value from the `function`.
   *
   * @param function A function to be applied to the target value.
   * @return A new {@link ObjectTransformer} which targets the result of the `function`.
   * @param <E> The type of the returned value of the `function`.
   */
  default <E> ObjectTransformer<T, E> toObject(Function<R, E> function) {
    return this.transformValueWith(function, ObjectTransformer.Impl::new);
  }

  default BooleanTransformer<T> toBoolean(Function<? super R, Boolean> function) {
    return this.transformValueWith(function, BooleanTransformer.Impl::new);
  }

  default IntegerTransformer<T> toInteger(Function<? super R, Integer> function) {
    return this.transformValueWith(function, IntegerTransformer.Impl::new);
  }

  default LongTransformer<T> toLong(Function<? super R, Long> function) {
    return this.transformValueWith(function, LongTransformer.Impl::new);
  }

  default ShortTransformer<T> toShort(Function<? super R, Short> function) {
    return this.transformValueWith(function, ShortTransformer.Impl::new);
  }

  default DoubleTransformer<T> toDouble(Function<? super R, Double> function) {
    return this.transformValueWith(function, DoubleTransformer.Impl::new);
  }

  default FloatTransformer<T> toFloat(Function<? super R, Float> function) {
    return this.transformValueWith(function, FloatTransformer.Impl::new);
  }

  default StringTransformer<T> toString(Function<? super R, String> function) {
    return this.transformValueWith(function, StringTransformer.Impl::new);
  }

  default <E> ListTransformer<T, E> toList(Function<? super R, List<E>> function) {
    return this.transformValueWith(function, ListTransformer.Impl::new);
  }

  default <E> StreamTransformer<T, E> toStream(Function<? super R, Stream<E>> function) {
    return this.transformValueWith(function, StreamTransformer.Impl::new);
  }

  default <E extends Throwable> ThrowableTransformer<T, E> toThrowable(Function<? super R, E> function) {
    return this.transformValueWith(function, ThrowableTransformer.Impl::new);

  }
}
