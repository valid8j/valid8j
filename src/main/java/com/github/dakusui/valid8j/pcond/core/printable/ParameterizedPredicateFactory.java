package com.github.dakusui.valid8j.pcond.core.printable;

import com.github.dakusui.valid8j.pcond.forms.Printables;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.Arrays.asList;

public interface ParameterizedPredicateFactory<T> extends ParameterizedIdentifiableFactory<Predicate<T>> {
  class Builder<T> extends ParameterizedIdentifiableFactory.Builder<Predicate<T>, Builder<T>> {
    public ParameterizedPredicateFactory<T> buildProtected() {
      return new ParameterizedPredicateFactory<T>() {
        final Function<List<Object>, Predicate<T>> factory = Builder.this.factory;
        @Override
        public Predicate<T> create(Object... args) {
          List<Object> args_ = asList(args);
          return Printables.predicate(
              formatterFactory.apply(args_),
              this.toPrintablePredicate(args_));
        }

        private Predicate<T> toPrintablePredicate(List<Object> args) {
          Predicate<T> predicate = factory.apply(args);
          if (predicate instanceof PrintablePredicate)
            return predicate;
          return PrintablePredicateFactory.leaf(() -> {
                assert false;
                return null;
              },
              predicate,
              this);
        }
      };
    }
  }
}
