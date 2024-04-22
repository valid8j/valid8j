package com.github.valid8j.pcond.core.fluent.builtins;

import com.github.valid8j.pcond.internals.InternalUtils;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A transformer interface for float.
 *
 * @param <T> A type of value from which it is transformed to `float`.
 */
public interface FloatTransformer<
    T
    > extends
    ComparableNumberTransformer<
                FloatTransformer<T>,
        FloatChecker<T>,
                T,
                Float> {
  static FloatTransformer<Float> create(Supplier<Float> value) {
    return new Impl<>(value, InternalUtils.trivialIdentityFunction());
  }

  class Impl<
      T
      > extends
      Base<
          FloatTransformer<T>,
          FloatChecker<T>,
          T,
          Float> implements
      FloatTransformer<T> {
    public Impl(Supplier<T> rootValue, Function<T, Float> root) {
      super(rootValue, root);
    }

    @Override
    protected FloatChecker<T> toChecker(Function<T, Float> transformFunction) {
      return new FloatChecker.Impl<>(this::baseValue, transformFunction);
    }

    @Override
    protected FloatTransformer<Float> rebase() {
      return new Impl<>(this::value, InternalUtils.trivialIdentityFunction());
    }
  }
}
