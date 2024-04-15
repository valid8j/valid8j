package com.github.dakusui.shared;

import com.github.dakusui.valid8j.pcond.fluent.Statement;
import com.github.dakusui.valid8j.pcond.validator.Validator;

import java.util.function.Function;
import java.util.function.Predicate;

public enum TestUtils {
  ;
  static final Validator validator = Validator.instance();

  public static <T> T validate(T value, Predicate<? super T> predicate) {
    return validate(value, predicate, IllegalValueException::new);
  }

  public static <T> T validateStatement(Statement<T> statement) {
    return validate(statement.statementValue(), statement.statementPredicate(), IllegalValueException::new);
  }

  public static <T> T validate(T value, Predicate<? super T> predicate, Function<String, Throwable> exceptionFactory) {
    return validator.validate(value, predicate, exceptionFactory);
  }
}
