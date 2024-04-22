package com.github.valid8j.pcond.core.fluent.builtins;

import com.github.valid8j.pcond.internals.InternalUtils;

import java.util.function.Function;
import java.util.function.Supplier;

public interface LongTransformer<
    T
    > extends
    ComparableNumberTransformer<
                LongTransformer<T>,
        LongChecker<T>,
                T,
                Long> {
  static LongTransformer<Long> create(Supplier<Long> value) {
    return new Impl<>(value, InternalUtils.trivialIdentityFunction());
  }

  class Impl<
      T
      > extends
      Base<
          LongTransformer<T>,
          LongChecker<T>,
          T,
          Long> implements
      LongTransformer<T> {
    public Impl(Supplier<T> baseValue, Function<T, Long> transformFunction) {
      super(baseValue, transformFunction);
    }

    @Override
    public LongChecker<T> toChecker(Function<T, Long> transformFunction) {
      return new LongChecker.Impl<>(this::baseValue, transformFunction);
    }

    @Override
    protected LongTransformer<Long> rebase() {
      return new Impl<>(this::value, InternalUtils.trivialIdentityFunction());
    }
  }

}
