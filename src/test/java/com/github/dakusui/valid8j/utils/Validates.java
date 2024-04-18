package com.github.dakusui.valid8j.utils;

import com.github.dakusui.valid8j.pcond.validator.ExceptionComposer;

import java.util.function.Function;
import java.util.function.Predicate;

public enum Validates {
  ;
  static final Validator VALIDATOR = createValidator(com.github.dakusui.valid8j.pcond.validator.Validator.instance().configuration().exceptionComposer().defaultForValidate());

  public static <T> T validate(T value, Predicate<? super T> cond) {
    return VALIDATOR.validate(value, cond);
  }

  public static <T, E extends RuntimeException> T validate(T value, Predicate<? super T> cond, Function<String, E> exceptionFactory) {
    return VALIDATOR.validate(value, cond, exceptionFactory);
  }

  public static <T> T validateNonNull(T value) {
    return VALIDATOR.validateNonNull(value);
  }

  public static <T> T validateArgument(T value, Predicate<? super T> cond) {
    return VALIDATOR.validateArgument(value, cond);
  }

  public static <T> T validateState(T value, Predicate<? super T> cond) {
    return VALIDATOR.validateState(value, cond);
  }

  public static Validator createValidator(ExceptionComposer.ForValidate exceptionComposerForValidate) {
    return Validator.create(exceptionComposerForValidate);
  }

  interface Validator {
    ExceptionComposer.ForValidate exceptionComposerForValidate();


    default <T> T validate(T value, Predicate<? super T> cond) {
      return com.github.dakusui.valid8j.pcond.validator.Validator.instance().validate(value, cond, exceptionComposerForValidate());
    }

    default <T, E extends RuntimeException> T validate(T value, Predicate<? super T> cond, Function<String, E> exceptionFactory) {
      return com.github.dakusui.valid8j.pcond.validator.Validator.instance().validate(value, cond, exceptionFactory::apply);
    }

    default <T> T validateNonNull(T value) {
      return com.github.dakusui.valid8j.pcond.validator.Validator.instance().validateNonNull(value, exceptionComposerForValidate());
    }

    default <T> T validateArgument(T value, Predicate<? super T> cond) {
      return com.github.dakusui.valid8j.pcond.validator.Validator.instance().validateArgument(value, cond, exceptionComposerForValidate());
    }

    default <T> T validateState(T value, Predicate<? super T> cond) {
      return com.github.dakusui.valid8j.pcond.validator.Validator.instance().validateState(value, cond, exceptionComposerForValidate());
    }

    static Validator create(ExceptionComposer.ForValidate exceptionComposerForValidate) {
      return () -> exceptionComposerForValidate;
    }
  }
}
