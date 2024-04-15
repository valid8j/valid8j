package com.github.dakusui.valid8j.pcond.core.fluent.builtins;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.github.dakusui.valid8j.pcond.internals.InternalUtils.trivialIdentityFunction;

public interface ShortTransformer<
    T
    > extends
    ComparableNumberTransformer<
                ShortTransformer<T>,
        ShortChecker<T>,
                T,
                Short> {
  static ShortTransformer<Short> create(Supplier<Short> value) {
    return new Impl<>(value, trivialIdentityFunction());
  }

  @SuppressWarnings("unchecked")
  default ShortTransformer<T> transform(Function<ShortTransformer<Short>, Predicate<Short>> clause) {
    return this.addTransformAndCheckClause(tx -> clause.apply((ShortTransformer<Short>) tx));
  }

  class Impl<
      T
      > extends
      Base<
          ShortTransformer<T>,
          ShortChecker<T>,
          T,
          Short> implements
      ShortTransformer<T> {
    public Impl(Supplier<T> baseValue, Function<T, Short> transformFunction) {
      super(baseValue, transformFunction);
    }

    @Override
    protected ShortChecker<T> toChecker(Function<T, Short> transformFunction) {
      return new ShortChecker.Impl<>(this::baseValue, transformFunction);
    }

    @Override
    protected ShortTransformer<Short> rebase() {
      return new Impl<>(this::value, trivialIdentityFunction());
    }
  }
}
