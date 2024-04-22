package com.github.valid8j.pcond.core.fluent.builtins;

import com.github.valid8j.pcond.internals.InternalUtils;

import java.util.function.Function;
import java.util.function.Supplier;

public interface DoubleTransformer<
    T
    > extends
    ComparableNumberTransformer<
                DoubleTransformer<T>,
        DoubleChecker<T>,
                T,
                Double> {
  static DoubleTransformer<Double> create(Supplier<Double> value) {
    return new Impl<>(value, InternalUtils.trivialIdentityFunction());
  }

  class Impl<
      OIN
      > extends
      Base<
          DoubleTransformer<OIN>,
          DoubleChecker<OIN>,
          OIN,
          Double> implements
      DoubleTransformer<OIN> {
    public Impl(Supplier<OIN> baseValue, Function<OIN, Double> transformFunction) {
      super(baseValue, transformFunction);
    }

    @Override
    protected DoubleChecker<OIN> toChecker(Function<OIN, Double> transformFunction) {
      return new DoubleChecker.Impl<>(this::baseValue, transformFunction);
    }

    @Override
    protected DoubleTransformer<Double> rebase() {
      return new Impl<>(this::value, InternalUtils.trivialIdentityFunction());
    }
  }
}
