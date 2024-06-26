package com.github.valid8j.pcond.core.fluent.builtins;

import com.github.valid8j.pcond.core.fluent.AbstractObjectTransformer;
import com.github.valid8j.pcond.forms.Functions;
import com.github.valid8j.pcond.forms.Printables;
import com.github.valid8j.pcond.internals.InternalUtils;

import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

public interface StringTransformer<T> extends
    AbstractObjectTransformer<
            StringTransformer<T>,
            StringChecker<T>,
            T,
            String> {
  static StringTransformer<String> create(Supplier<String> value) {
    return new Impl<>(value, InternalUtils.trivialIdentityFunction());
  }

  default StringTransformer<T> substring(int begin) {
    return this.toString(Printables.function(() -> "substring[" + begin + "]", (String s) -> s.substring(begin)));
  }

  default StringTransformer<T> toUpperCase() {
    return this.toString(Printables.function("toUpperCase", String::toUpperCase));
  }

  default StringTransformer<T> toLowerCase() {
    return this.toString(Printables.function("toLowerCase", String::toLowerCase));
  }

  default ListTransformer<T, String> split(String regex) {
    return this.toList(Printables.function("split[" + regex + "]", (String s) -> asList((s.split(regex)))));
  }

  default IntegerTransformer<T> length() {
    return toInteger(Functions.length());
  }

  default BooleanTransformer<T> parseBoolean() {
    return toBoolean(Printables.function("parseBoolean", Boolean::parseBoolean));
  }

  default IntegerTransformer<T> parseInt() {
    return toInteger(Printables.function("parseInt", Integer::parseInt));
  }

  default LongTransformer<T> parseLong() {
    return toLong(Printables.function("parseLong", Long::parseLong));
  }

  default ShortTransformer<T> parseShort() {
    return toShort(Printables.function("parseBoolean", Short::parseShort));
  }

  default DoubleTransformer<T> parseDouble() {
    return toDouble(Printables.function("parseDouble", Double::parseDouble));
  }

  default FloatTransformer<T> parseFloat() {
    return toFloat(Printables.function("parseFloat", Float::parseFloat));
  }

  default StringTransformer<T> substringAfter(String token) {
    return toString(Printables.function("substringAfter[" + token + "]", str -> str.substring(str.indexOf(token) + token.length())));
  }

  class Impl<T> extends
      Base<
          StringTransformer<T>,
          StringChecker<T>,
          T,
          String> implements
      StringTransformer<T> {

    public Impl(Supplier<T> rootValue, Function<T, String> transformFunction) {
      super(rootValue, transformFunction);
    }

    @Override
    public StringChecker<T> toChecker(Function<T, String> transformFunction) {
      return new StringChecker.Impl<>(this::baseValue, requireNonNull(transformFunction));
    }

    @Override
    public StringTransformer<String> rebase() {
      return new Impl<>(this::value, InternalUtils.trivialIdentityFunction());
    }
  }
}
