package com.github.valid8j.pcond.core.fluent.builtins;


import com.github.valid8j.pcond.internals.InternalUtils;

import java.util.function.Function;
import java.util.function.Supplier;

public interface DoubleChecker<
    T
    > extends
    ComparableNumberChecker<
                DoubleChecker<T>,
                T,
                Double> {

  class Impl<
      T
      > extends
      Base<
          DoubleChecker<T>,
          T,
          Double> implements
      DoubleChecker<
          T> {
    public Impl(Supplier<T> baseValue, Function<T, Double> transformFunction) {
      super(baseValue, transformFunction);
    }

    @Override
    protected DoubleChecker<Double> rebase() {
      return new Impl<>(this::value, InternalUtils.trivialIdentityFunction());
    }
  }
}
