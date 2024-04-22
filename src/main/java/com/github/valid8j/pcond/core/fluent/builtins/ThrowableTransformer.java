package com.github.valid8j.pcond.core.fluent.builtins;


import com.github.valid8j.pcond.forms.Printables;
import com.github.valid8j.pcond.core.fluent.AbstractObjectTransformer;
import com.github.valid8j.pcond.internals.InternalUtils;

import java.util.function.Function;
import java.util.function.Supplier;

public interface ThrowableTransformer<
    T,
    E extends Throwable> extends
    AbstractObjectTransformer<
            ThrowableTransformer<T, E>,
        ThrowableChecker<T, E>,
            T,
            E> {
  static <E extends Throwable> ThrowableTransformer<E, E> create(Supplier<E> value) {
    return new Impl<>(value, InternalUtils.trivialIdentityFunction());
  }

  @SuppressWarnings("unchecked")
  default <OUT2 extends Throwable> ThrowableTransformer<T, OUT2> getCause() {
    return this.toThrowable(Printables.function("getCause", e -> (OUT2) e.getCause()));
  }

  default StringTransformer<T> getMessage() {
    return this.toString(Printables.function("getMessage", Throwable::getMessage));
  }

  class Impl<
      T,
      E extends Throwable
      > extends
      Base<
          ThrowableTransformer<T, E>,
          ThrowableChecker<T, E>,
          T,
          E> implements
      ThrowableTransformer<
          T,
          E
          > {

    public Impl(Supplier<T> baseValue, Function<T, E> transformFunction) {
      super(baseValue, transformFunction);
    }

    @Override
    public ThrowableChecker<T, E> toChecker(Function<T, E> transformFunction) {
      return new ThrowableChecker.Impl<>(this::baseValue, transformFunction);
    }

    @Override
    protected ThrowableTransformer<E, E> rebase() {
      return new Impl<>(this::value, InternalUtils.trivialIdentityFunction());
    }
  }
}
