package com.github.dakusui.valid8j_pcond.propertybased.utils;

import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

public enum ReportCheckUtils {
  ;

  public static <T> Predicate<T> makePrintablePredicate(String s, Predicate<T> predicate) {
    return new Predicate<T>() {
      @Override
      public boolean test(T t) {
        return predicate.test(t);
      }

      @Override
      public String toString() {
        return s;
      }
    };
  }

  public static <T, R> BiPredicate<T, R> makePrintableBiPredicate(String s, BiPredicate<T, R> predicate) {
    return new BiPredicate<T, R>() {
      @Override
      public boolean test(T t, R r) {
        return predicate.test(t, r);
      }

      @Override
      public String toString() {
        return s;
      }
    };
  }

  public static <T, R> Function<T, R> makePrintableFunction(String s, Function<T, R> function) {
    return new Function<T, R>() {
      @Override
      public R apply(T t) {
        return function.apply(t);
      }

      @Override
      public String toString() {
        return s;
      }
    };
  }
}
