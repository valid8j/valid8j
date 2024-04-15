package com.github.dakusui.valid8j.pcond.core.fluent.builtins;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.github.dakusui.valid8j.pcond.internals.InternalUtils.trivialIdentityFunction;

public interface IntegerTransformer<
    T
    > extends
    ComparableNumberTransformer<
                IntegerTransformer<T>,
        IntegerChecker<T>,
                T,
                Integer> {
  static IntegerTransformer<Integer> create(Supplier<Integer> value) {
    return new Impl<>(value, trivialIdentityFunction());
  }

  @SuppressWarnings("unchecked")
  default IntegerTransformer<T> transform(Function<IntegerTransformer<Integer>, Predicate<Integer>> clause) {
    return this.addTransformAndCheckClause(tx -> clause.apply((IntegerTransformer<Integer>) tx));
  }
  class Impl<
      T
      > extends
      Base<
          IntegerTransformer<T>,
          IntegerChecker<T>,
          T,
          Integer> implements
      IntegerTransformer<T> {
    public Impl(Supplier<T> baseValue, Function<T, Integer> transformFunction) {
      super(baseValue, transformFunction);
    }

    @Override
    public IntegerChecker<T> toChecker(Function<T, Integer> transformFunction) {
      return new IntegerChecker.Impl<>(this::baseValue, transformFunction);
    }

    @Override
    public IntegerTransformer<Integer> rebase() {
      return new Impl<>(this::value, trivialIdentityFunction());
    }
  }
}
