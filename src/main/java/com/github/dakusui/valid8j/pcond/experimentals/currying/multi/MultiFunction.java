package com.github.dakusui.valid8j.pcond.experimentals.currying.multi;

import com.github.dakusui.valid8j.pcond.forms.Functions;
import com.github.dakusui.valid8j.pcond.core.printable.PrintableFunction;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.github.dakusui.valid8j.pcond.internals.InternalChecks.requireArgumentListSize;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;

/**
 * An interface that represents a function that can have more than one parameters.
 * This interface is often used in combination with {@link Functions#curry(MultiFunction)} method.
 *
 * @param <R> The type of the returned value.
 */
public interface MultiFunction<R> extends Function<List<? super Object>, R> {
  /**
   * Returns a name of this function.
   *
   * @return The name of this function.
   */
  String name();

  /**
   * Returns the number of parameters that this function can take.
   *
   * @return the number of parameters
   */
  int arity();

  /**
   * The expected type of the {@code i}th parameter.
   *
   * @param i The parameter index.
   * @return The type of {@code i}th parameter.
   */
  Class<?> parameterType(int i);

  /**
   * The type of the value returned by this function.
   *
   * @return The type of the returned value.
   */
  Class<? extends R> returnType();

  class Impl<R> extends PrintableFunction<List<? super Object>, R> implements MultiFunction<R> {
    private final String         name;
    private final List<Class<?>> parameterTypes;

    protected Impl(
        Object creator,
        List<Object> args,
        Supplier<String> formatter,
        String name,
        Function<? super List<? super Object>, ? extends R> function,
        List<Class<?>> parameterTypes) {
      super(
          creator,
          args,
          formatter,
          function);
      this.name = name;
      this.parameterTypes = new ArrayList<>(parameterTypes);
    }

    @Override
    public String name() {
      return name;
    }

    @Override
    public int arity() {
      return parameterTypes.size();
    }

    @Override
    public Class<?> parameterType(int i) {
      return parameterTypes.get(i);
    }

  }

  /**
   * A builder for a {@link MultiFunction} instance.
   *
   * @param <R> The type of value returned by the multi-function built by this object.
   */
  class Builder<R> {
    private final Object                                              creator        = Builder.class;
    private       List<Object>                                        identityArgs;
    private       String                                              name           = "(anonymous)";
    private final Function<? super List<? super Object>, ? extends R> body;
    private final List<Class<?>>                                      parameterTypes = new LinkedList<>();
    private       Supplier<String>                                    formatter      = () -> name + "(" + parameterTypes.stream().map(Class::getSimpleName).collect(joining(",")) + ")";

    public Builder(Function<List<Object>, R> body) {
      requireNonNull(body);
      this.body = args -> body.apply(requireArgumentListSize(requireNonNull(args), parameterTypes.size()));
    }

    public Builder<R> addParameters(List<Class<?>> parameterTypes) {
      requireNonNull(parameterTypes).stream().map(this::addParameter).forEach(Objects::requireNonNull);
      return this;
    }

    public Builder<R> identityArgs(List<Object> identity) {
      this.identityArgs = requireNonNull(identity);
      return this;
    }

    public Builder<R> name(String name) {
      this.name = name;
      return this;
    }

    public Builder<R> addParameter(Class<?> parameterType) {
      this.parameterTypes.add(requireNonNull(parameterType));
      return this;
    }

    public Builder<R> formatter(Supplier<String> formatter) {
      this.formatter = requireNonNull(formatter);
      return this;
    }

    public MultiFunction<R> $() {
      return new Impl<R>(
          this.creator,
          this.identityArgs,
          this.formatter,
          this.name,
          this.body,
          this.parameterTypes
      );
    }
  }
}
