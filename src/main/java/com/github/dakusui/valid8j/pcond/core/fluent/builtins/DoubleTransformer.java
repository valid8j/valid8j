package com.github.dakusui.valid8j.pcond.core.fluent.builtins;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.github.dakusui.valid8j.pcond.internals.InternalUtils.trivialIdentityFunction;

public interface DoubleTransformer<
    T
    > extends
    ComparableNumberTransformer<
                DoubleTransformer<T>,
        DoubleChecker<T>,
                T,
                Double> {
  static DoubleTransformer<Double> create(Supplier<Double> value) {
    return new Impl<>(value, trivialIdentityFunction());
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
      return new Impl<>(this::value, trivialIdentityFunction());
    }
  }
}
