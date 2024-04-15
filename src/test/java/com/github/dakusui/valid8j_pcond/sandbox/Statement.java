package com.github.dakusui.valid8j_pcond.sandbox;

import com.github.dakusui.valid8j.pcond.validator.Validator;

import java.util.function.Predicate;
import java.util.function.Supplier;


public interface Statement<T> {
  interface Delegated<T> extends Statement<T> {
    Statement<T> base();

    default T value() {
      return base().value();
    }

    default Predicate<T> predicate() {
      return base().predicate();
    }
  }

  class StatementWasFalsified extends RuntimeException {
    public StatementWasFalsified(String s) {
      super(s);
    }
  }

  T value();

  Predicate<T> predicate();

  default boolean evaluate(boolean evaluationCondition, boolean throwExceptionOnFailure) {
    try {
      T value = this.value();
      return !evaluationCondition || Validator.instance().validate(value, this.predicate(), StatementWasFalsified::new) == value;
    } catch (StatementWasFalsified e) {
      if (throwExceptionOnFailure) {
        throw e;
      }
      return false;
    }
  }


  static <T> Statement<T> create(T value, Supplier<Predicate<T>> predicateSupplier) {
    return new Statement<T>() {
      @Override
      public T value() {
        return value;
      }

      @Override
      public Predicate<T> predicate() {
        return predicateSupplier.get();
      }
    };
  }

  static <T> Statement<T> statement(T value, Supplier<Predicate<T>> predicateSupplier) {
    return create(value, predicateSupplier);
  }

  static boolean isInProduction() {
    return false;
  }

  static boolean inspectionIsEnabled() {
    return false;
  }
}
