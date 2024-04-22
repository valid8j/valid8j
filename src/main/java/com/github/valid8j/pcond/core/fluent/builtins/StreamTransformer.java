package com.github.valid8j.pcond.core.fluent.builtins;

import com.github.valid8j.pcond.core.fluent.AbstractObjectTransformer;
import com.github.valid8j.pcond.internals.InternalUtils;

import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public interface StreamTransformer<
    T,
    E> extends
    AbstractObjectTransformer<
            StreamTransformer<T, E>,
        StreamChecker<T, E>,
            T,
            Stream<E>
            > {
  static <E> StreamTransformer<Stream<E>, E> create(Supplier<Stream<E>> value) {
    return new Impl<>(value, InternalUtils.trivialIdentityFunction());
  }

  class Impl<
      T,
      E> extends
      Base<
          StreamTransformer<T, E>,
          StreamChecker<T, E>,
          T,
          Stream<E>> implements
      StreamTransformer<T, E> {

    public Impl(Supplier<T> rootValue, Function<T, Stream<E>> root) {
      super(rootValue, root);
    }

    @Override
    protected StreamChecker<T, E> toChecker(Function<T, Stream<E>> transformFunction) {
      return new StreamChecker.Impl<>(this::baseValue, transformFunction);
    }

    @Override
    protected StreamTransformer<Stream<E>, E> rebase() {
      return new Impl<>(this::value, InternalUtils.trivialIdentityFunction());
    }
  }
}
