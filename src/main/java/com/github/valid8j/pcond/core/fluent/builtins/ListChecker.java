package com.github.valid8j.pcond.core.fluent.builtins;

import com.github.valid8j.pcond.core.fluent.AbstractObjectChecker;
import com.github.valid8j.pcond.experimentals.cursor.Cursors;
import com.github.valid8j.pcond.forms.Predicates;
import com.github.valid8j.pcond.forms.Printables;
import com.github.valid8j.pcond.internals.InternalUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public interface ListChecker<
    T,
    E
    > extends
    AbstractObjectChecker<
                    ListChecker<T, E>,
                    T,
                    List<E>> {
  default ListChecker<T, E> empty() {
    return checkWithPredicate(Predicates.isEmpty());
  }

  default ListChecker<T, E> notEmpty() {
    return checkWithPredicate(Predicates.not(Predicates.isEmpty()));
  }

  default ListChecker<T, E> containing(E element) {
    return checkWithPredicate(Predicates.contains(element));
  }

  @SuppressWarnings("unchecked")
  default ListChecker<T, E> containingElementsInOrder(List<Predicate<E>> predicates) {
    return checkWithPredicate(Cursors.findElements(predicates.toArray(new Predicate[0])));
  }

  @SuppressWarnings("unchecked")
  default ListChecker<T, E> containingElementsInOrder(E... elements) {
    return this.containingElementsInOrder(
        Arrays.stream(elements)
            .map(v -> Printables.predicate("[" + v + "]", e -> Objects.equals(v, e)))
            .map(p -> (Predicate<E>) p)
            .collect(Collectors.toList()));
  }

  class Impl<
      T,
      E
      > extends Base<
      ListChecker<T, E>,
      T,
      List<E>> implements
      ListChecker<T, E> {
    public Impl(Supplier<T> rootValue, Function<T, List<E>> transformFunction) {
      super(rootValue, transformFunction);
    }

    @Override
    protected ListChecker<List<E>, E> rebase() {
      return new Impl<>(this::value, InternalUtils.trivialIdentityFunction());
    }
  }
}
