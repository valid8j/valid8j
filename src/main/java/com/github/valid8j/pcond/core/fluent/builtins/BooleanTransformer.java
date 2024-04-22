package com.github.valid8j.pcond.core.fluent.builtins;


import com.github.valid8j.pcond.core.fluent.AbstractObjectTransformer;
import com.github.valid8j.pcond.internals.InternalUtils;

import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

public interface BooleanTransformer<T> extends
    AbstractObjectTransformer<
            BooleanTransformer<T>,
        BooleanChecker<T>,
            T,
            Boolean
            > {
  static BooleanTransformer<Boolean> create(Supplier<Boolean> value) {
    return new Impl<>(value, InternalUtils.trivialIdentityFunction());
  }

  class Impl<T> extends
      Base<
          BooleanTransformer<T>,
          BooleanChecker<T>,
          T,
          Boolean
          > implements
      BooleanTransformer<T> {
    public Impl(Supplier<T> value, Function<T, Boolean> transfomFunction) {
      super(value, transfomFunction);
    }

    @Override
    protected BooleanChecker<T> toChecker(Function<T, Boolean> transformFunction) {
      return new BooleanChecker.Impl<>(this::baseValue, requireNonNull(transformFunction));
    }

    @Override
    protected BooleanTransformer<Boolean> rebase() {
      return new Impl<>(this::value, InternalUtils.trivialIdentityFunction());
    }
  }
}
