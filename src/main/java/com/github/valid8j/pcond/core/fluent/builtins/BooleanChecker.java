package com.github.valid8j.pcond.core.fluent.builtins;

import com.github.valid8j.pcond.core.fluent.AbstractObjectChecker;
import com.github.valid8j.pcond.forms.Predicates;
import com.github.valid8j.pcond.internals.InternalUtils;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

public interface BooleanChecker<T> extends
    AbstractObjectChecker<
                    BooleanChecker<T>,
                    T,
                    Boolean> {

  default BooleanChecker<T> isTrue() {
    return this.checkWithPredicate(Predicates.isTrue());
  }

  default BooleanChecker<T> isFalse() {
    return this.checkWithPredicate(Predicates.isFalse());
  }

  @SuppressWarnings("unchecked")
  default BooleanChecker<T> check(Function<BooleanChecker<Boolean>, Predicate<Boolean>> phrase) {
    requireNonNull(phrase);
    return this.addCheckPhrase(v -> phrase.apply((BooleanChecker<Boolean>) v));
  }
  class Impl<T> extends
      Base<
          BooleanChecker<T>,
          T,
          Boolean> implements
      BooleanChecker<T> {
    public Impl(Supplier<T> baseValue, Function<T, Boolean> transformingFunction) {
      super(baseValue, transformingFunction);
    }

    @Override
    protected BooleanChecker<Boolean> rebase() {
      return new Impl<>(this::value, InternalUtils.trivialIdentityFunction());
    }
  }
}
