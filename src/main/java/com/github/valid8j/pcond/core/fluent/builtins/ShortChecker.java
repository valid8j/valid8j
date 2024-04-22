package com.github.valid8j.pcond.core.fluent.builtins;


import com.github.valid8j.pcond.internals.InternalUtils;

import java.util.function.Function;
import java.util.function.Supplier;

public interface ShortChecker<
    T> extends
    ComparableNumberChecker<
                ShortChecker<T>,
                T,
                Short> {

  class Impl<
      T> extends
      Base<
          ShortChecker<T>,
          T,
          Short>
      implements ShortChecker<T> {
    public Impl(Supplier<T> baseValue, Function<T, Short> transformFunction) {
      super(baseValue, transformFunction);
    }

    @Override
    public ShortChecker<Short> rebase() {
      return new Impl<>(this::value, InternalUtils.trivialIdentityFunction());
    }
  }
}
