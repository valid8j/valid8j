package com.github.valid8j.pcond.core.fluent.builtins;

import com.github.valid8j.pcond.internals.InternalUtils;

import java.util.function.Function;
import java.util.function.Supplier;

public interface ShortTransformer<
    T
    > extends
    ComparableNumberTransformer<
                ShortTransformer<T>,
        ShortChecker<T>,
                T,
                Short> {
  static ShortTransformer<Short> create(Supplier<Short> value) {
    return new Impl<>(value, InternalUtils.trivialIdentityFunction());
  }

  class Impl<
      T
      > extends
      Base<
          ShortTransformer<T>,
          ShortChecker<T>,
          T,
          Short> implements
      ShortTransformer<T> {
    public Impl(Supplier<T> baseValue, Function<T, Short> transformFunction) {
      super(baseValue, transformFunction);
    }

    @Override
    protected ShortChecker<T> toChecker(Function<T, Short> transformFunction) {
      return new ShortChecker.Impl<>(this::baseValue, transformFunction);
    }

    @Override
    protected ShortTransformer<Short> rebase() {
      return new Impl<>(this::value, InternalUtils.trivialIdentityFunction());
    }
  }
}
