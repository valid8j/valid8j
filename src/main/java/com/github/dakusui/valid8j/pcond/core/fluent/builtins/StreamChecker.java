package com.github.dakusui.valid8j.pcond.core.fluent.builtins;

import com.github.dakusui.valid8j.pcond.core.fluent.AbstractObjectChecker;
import com.github.dakusui.valid8j.pcond.forms.Predicates;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.github.dakusui.valid8j.pcond.internals.InternalUtils.trivialIdentityFunction;

public interface StreamChecker<
    T,
    E> extends
    AbstractObjectChecker<
                    StreamChecker<T, E>,
                    T,
                    Stream<E>> {
  default StreamChecker<T, E> noneMatch(Predicate<E> p) {
    return this.checkWithPredicate(Predicates.noneMatch(p));
  }

  default StreamChecker<T, E> anyMatch(Predicate<E> p) {
    return this.checkWithPredicate(Predicates.anyMatch(p));
  }

  default StreamChecker<T, E> allMatch(Predicate<E> p) {
    return this.checkWithPredicate(Predicates.allMatch(p));
  }

  class Impl<
      T,
      E> extends
      Base<
          StreamChecker<T, E>,
          T,
          Stream<E>
          >

      implements StreamChecker<T, E> {
    public Impl(Supplier<T> rootValue, Function<T, Stream<E>> root) {
      super(rootValue, root);
    }

    @Override
    protected StreamChecker<Stream<E>, E> rebase() {
      return new Impl<>(this::value, trivialIdentityFunction());
    }
  }
}
