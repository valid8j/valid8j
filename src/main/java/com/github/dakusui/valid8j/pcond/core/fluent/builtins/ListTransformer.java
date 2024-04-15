package com.github.dakusui.valid8j.pcond.core.fluent.builtins;

import com.github.dakusui.valid8j.pcond.core.fluent.Matcher;
import com.github.dakusui.valid8j.pcond.forms.Functions;
import com.github.dakusui.valid8j.pcond.forms.Printables;
import com.github.dakusui.valid8j.pcond.core.fluent.AbstractObjectTransformer;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.github.dakusui.valid8j.pcond.internals.InternalUtils.trivialIdentityFunction;

public interface ListTransformer<
    T,
    E
    > extends
    AbstractObjectTransformer<
            ListTransformer<T, E>,
            ListChecker<T, E>,
            T,
            List<E>> {
  static <E> ListTransformer<List<E>, E> create(Supplier<List<E>> value) {
    return new Impl<>(value, trivialIdentityFunction());
  }

  @SuppressWarnings("unchecked")
  default ListTransformer<T, E> transform(Function<ListTransformer<T, E>, Predicate<List<E>>> clause) {
    return this.addTransformAndCheckClause(tx -> clause.apply((ListTransformer<T, E>) tx));
  }

  default ObjectTransformer<T, E> elementAt(int i) {
    return this.toObject(Functions.elementAt(i));
  }

  default IntegerTransformer<T> size() {
    return this.toInteger(Functions.size());
  }

  default ListTransformer<T, E> subList(int begin, int end) {
    return this.toList(Printables.function("subList[" + begin + "," + end + "]", v -> v.subList(begin, end)));
  }

  default ListTransformer<T, E> subList(int begin) {
    return this.toList(Printables.function("subList[" + begin + "]", v -> v.subList(begin, v.size())));
  }

  default StreamTransformer<T, E> stream() {
    return this.toStream(Printables.function("listStream", Collection::stream));
  }

  default BooleanTransformer<T> isEmpty() {
    return this.toBoolean(Printables.function("listIsEmpty", List::isEmpty));
  }

  class Impl<T, E> extends
      Base<
          ListTransformer<T, E>,
          ListChecker<T, E>,
          T,
          List<E>> implements
      ListTransformer<T, E> {
    public Impl(Supplier<T> value, Function<T, List<E>> transformFunction) {
      super(value, transformFunction);
    }

    @Override
    protected ListChecker<T, E> toChecker(Function<T, List<E>> transformFunction) {
      return new ListChecker.Impl<>(this::baseValue, transformFunction);
    }

    @Override
    protected Matcher<?, List<E>, List<E>> rebase() {
      return new Impl<>(this::value, trivialIdentityFunction());
    }
  }
}
