package com.github.dakusui.valid8j.pcond.core.fluent.builtins;


import com.github.dakusui.valid8j.pcond.core.fluent.AbstractObjectChecker;
import com.github.dakusui.valid8j.pcond.forms.Predicates;

public interface ComparableNumberChecker<
    V extends ComparableNumberChecker<V, T, N>,
    T,
    N extends Number & Comparable<N>
    > extends AbstractObjectChecker<V, T, N> {
  default V equalTo(N v) {
    return checkWithPredicate(Predicates.equalTo(v));
  }

  default V lessThan(N v) {
    return checkWithPredicate(Predicates.lessThan(v));
  }

  default V lessThanOrEqualTo(N v) {
    return checkWithPredicate(Predicates.lessThanOrEqualTo(v));
  }

  default V greaterThan(N v) {
    return checkWithPredicate(Predicates.greaterThan(v));
  }

  default V greaterThanOrEqualTo(N v) {
    return checkWithPredicate(Predicates.greaterThanOrEqualTo(v));
  }
}
