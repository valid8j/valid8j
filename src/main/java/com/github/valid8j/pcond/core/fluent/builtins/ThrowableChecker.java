package com.github.valid8j.pcond.core.fluent.builtins;

import com.github.valid8j.pcond.core.fluent.AbstractObjectChecker;
import com.github.valid8j.pcond.internals.InternalUtils;

import java.util.function.Function;
import java.util.function.Supplier;

public interface ThrowableChecker<
    T,
    E extends Throwable> extends
        AbstractObjectChecker<
                            ThrowableChecker<T, E>,
                            T,
                            E> {
  class Impl<
      T,
      E extends Throwable
      > extends
      Base<
          ThrowableChecker<T, E>,
          T,
          E
          > implements
      ThrowableChecker<T, E> {
    protected Impl(Supplier<T> baseValue, Function<T, E> transformFunction) {
      super(baseValue, transformFunction);
    }

    @Override
    protected ThrowableChecker<E, E> rebase() {
      return new Impl<>(this::value, InternalUtils.trivialIdentityFunction());
    }
  }
}
