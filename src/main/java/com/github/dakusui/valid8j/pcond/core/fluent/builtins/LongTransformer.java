package com.github.dakusui.valid8j.pcond.core.fluent.builtins;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.github.dakusui.valid8j.pcond.internals.InternalUtils.trivialIdentityFunction;

public interface LongTransformer<
    T
    > extends
    ComparableNumberTransformer<
                LongTransformer<T>,
        LongChecker<T>,
                T,
                Long> {
  static LongTransformer<Long> create(Supplier<Long> value) {
    return new Impl<>(value, trivialIdentityFunction());
  }

  @SuppressWarnings("unchecked")
  default LongTransformer<T> transform(Function<LongTransformer<Long>, Predicate<Long>> clause) {
    return this.addTransformAndCheckClause(tx -> clause.apply((LongTransformer<Long>) tx));
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
      return new Impl<>(this::value, trivialIdentityFunction());
    }
  }

}
