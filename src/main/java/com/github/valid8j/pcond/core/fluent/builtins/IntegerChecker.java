package com.github.valid8j.pcond.core.fluent.builtins;


import com.github.valid8j.pcond.internals.InternalUtils;

import java.util.function.Function;
import java.util.function.Supplier;

public interface IntegerChecker<
    OIN
    > extends
    ComparableNumberChecker<
                IntegerChecker<OIN>,
                OIN,
                Integer> {

  class Impl<
      T> extends
      Base<
          IntegerChecker<T>,
          T,
          Integer>
      implements IntegerChecker<T> {
    public Impl(Supplier<T> baseValue, Function<T, Integer> transformFunction) {
      super(baseValue, transformFunction);
    }

    @Override
    public IntegerChecker<Integer> rebase() {
      return new Impl<>(this::value, InternalUtils.trivialIdentityFunction());
    }
  }
}
