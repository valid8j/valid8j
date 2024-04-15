package com.github.dakusui.valid8j.pcond.core.printable;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

public interface ParameterizedIdentifiableFactory<T> {
  T create(Object... args);

  abstract class Builder<T, B extends Builder<T, B>> {
    Function<List<Object>, Supplier<String>> formatterFactory;

    Function<List<Object>, T> factory;

    public B name(String name) {
      return this.formatterFactory((List<Object> args) -> () -> String.format("%s%s", name, args));
    }


    public ParameterizedIdentifiableFactory<T> factory(Function<List<Object>, T> predicateFactory) {
      this.factory = Objects.requireNonNull(predicateFactory);
      return this.build();
    }

    @SuppressWarnings("unchecked")
    public B formatterFactory(Function<List<Object>, Supplier<String>> formatterFactory) {
      this.formatterFactory = Objects.requireNonNull(formatterFactory);
      return (B) this;
    }

    final public ParameterizedIdentifiableFactory<T> build() {
      Objects.requireNonNull(this.factory);
      return buildProtected();
    }

    public abstract ParameterizedIdentifiableFactory<T> buildProtected();
  }
}
