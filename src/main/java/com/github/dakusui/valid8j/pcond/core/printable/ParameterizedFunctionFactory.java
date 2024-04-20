package com.github.dakusui.valid8j.pcond.core.printable;

import com.github.dakusui.valid8j.pcond.forms.Printables;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Arrays.asList;

public interface ParameterizedFunctionFactory<T, R> extends ParameterizedIdentifiableFactory<Function<T, R>> {
  class Builder<T, R> extends ParameterizedIdentifiableFactory.Builder<Function<T, R>, Builder<T, R>> {
    public ParameterizedFunctionFactory<T, R> buildProtected() {
      return new ParameterizedFunctionFactory<T, R>() {
        final Function<List<Object>, Supplier<String>> formatterFactory = Builder.this.formatterFactory;
        @Override
        public Function<T, R> create(Object... args) {
          List<Object> args_ = asList(args);
          return Printables.function(
              formatterFactory.apply(args_),
              this.toPrintableFunction(args_));
        }

        public Function<T, R> toPrintableFunction(List<Object> args) {
          Function<T, R> function = factory.apply(args);
          if (function instanceof PrintableFunction)
            return function;
          return PrintableFunctionFactory.function(() -> {
                assert false;
                return null;
              },
              function,
              this);
        }
      };
    }
  }
}
