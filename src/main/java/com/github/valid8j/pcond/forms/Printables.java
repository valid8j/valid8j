package com.github.valid8j.pcond.forms;


import com.github.valid8j.pcond.core.printable.PrintableFunctionFactory;
import com.github.valid8j.pcond.core.printable.PrintablePredicateFactory;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * An entry point class that provides methods to create a new "printable" function from a given conventional function.
 */
public enum Printables {
  ;

  /**
   * Returns a printable predicate for a given one.
   * This is a shorthand of `Printables.predicate(() -> name, predicate)`.
   *
   * @param name      A name of the returned predicate.
   * @param predicate A predicate from which a printable predicate is created.
   * @param <T>       The type that the returned predicate can test.
   * @return A printable predicate.
   */
  public static <T> Predicate<T> predicate(String name, Predicate<T> predicate) {
    return PrintablePredicateFactory.leaf(name, predicate);
  }

  /**
   * Returns a printable predicate for a given one.
   *
   * @param formatter A supplier that gives a string printed on `toString()`.
   * @param predicate A predicate from which a printable predicate is created.
   * @param <T>       The type that the returned predicate can test.
   * @return A printable predicate.
   */
  public static <T> Predicate<T> predicate(Supplier<String> formatter, Predicate<T> predicate) {
    return PrintablePredicateFactory.leaf(formatter, predicate);
  }

  /**
   * Returns a printable function for a given one.
   * This is a short-hand method for `Printables.function(() -> name, function)`.
   *
   * @param name     A name of the function.
   * @param function A function from which a printable function is created.
   * @param <T>      A type of the function's parameter.
   * @param <R>      A type of the function's returned value.
   * @return A printable function.
   */
  public static <T, R> Function<T, R> function(String name, Function<T, R> function) {
    return PrintableFunctionFactory.function(name, function);
  }

  /**
   * Returns a printable function for a given one.
   *
   * @param formatter A supplier that gives a string printed on `toString()`.
   * @param function A function from which a printable function is created.
   * @param <T>      A type of the function's parameter.
   * @param <R>      A type of the function's returned value.
   * @return A printable function.
   */
  public static <T, R> Function<T, R> function(Supplier<String> formatter, Function<T, R> function) {
    return PrintableFunctionFactory.function(formatter, function);
  }
}