package com.github.valid8j.pcond.core.fluent.builtins;


import com.github.valid8j.pcond.internals.InternalUtils;

import java.util.function.Function;
import java.util.function.Supplier;

public interface LongChecker<
    OIN
    > extends
    ComparableNumberChecker<
                LongChecker<OIN>,
                OIN,
                Long> {

  class Impl<T> extends
      Base<
          LongChecker<T>,
          T,
          Long> implements
      LongChecker<
          T> {
    public Impl(Supplier<T> baseValue, Function<T, Long> transformFunction) {
      super(baseValue, transformFunction);
    }

    @Override
    protected LongChecker<Long> rebase() {
      return new Impl<>(this::value, InternalUtils.trivialIdentityFunction());
    }
  }
}
