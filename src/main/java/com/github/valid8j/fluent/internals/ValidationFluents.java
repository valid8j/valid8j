package com.github.valid8j.fluent.internals;

import com.github.valid8j.classic.Assertions;
import com.github.valid8j.classic.Ensures;
import com.github.valid8j.classic.Requires;
import com.github.valid8j.pcond.core.fluent.builtins.ObjectTransformer;
import com.github.valid8j.pcond.fluent.Statement;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;

/**
 * A facade class to provide fluent versions of `valid8j` entry points.
 * You can pass `Statement` objects created by static methods in `Statement` interface such as
 * `objectValue`, `stringValue`, `intValue`, etc.
 *
 * @see Statement
 */
public enum ValidationFluents {
  ;

  /**
   * Fluent version of {@link Requires#requireArgument(Object, Predicate)}.
   *
   * @param statement A statement to be verified
   */
  public static <T> T requireArgument(Statement<T> statement) {
    return Requires.requireArgument(statement.statementValue(), statement.statementPredicate());
  }

  /**
   * Fluent version of {@link Requires#requireArgument(Object, Predicate)}.
   * Use this method when you need to verify multiple values.
   *
   * @param statements Statements to be verified
   */
  public static void requireArguments(Statement<?>... statements) {
    List<?> values = Arrays.stream(statements).map(Statement::statementValue).collect(toList());
    Requires.requireArgument(values, Statement.createPredicateForAllOf(statements));
  }

  public static <T> T requireStatement(Statement<T> statement) {
    return Requires.require(statement.statementValue(), statement.statementPredicate());
  }

  /**
   * Fluent version of {@link Requires#require(Object, Predicate)}.
   * Use this method when you need to verify multiple values.
   *
   * @param statements Statements to be verified
   */
  public static void requireAll(Statement<?>... statements) {
    List<?> values = Arrays.stream(statements).map(Statement::statementValue).collect(toList());
    Requires.require(values, Statement.createPredicateForAllOf(statements));
  }

  public static <T> T requireState(Statement<T> statement) {
    return Requires.requireState(statement.statementValue(), statement.statementPredicate());
  }

  /**
   * Fluent version of {@link Requires#requireState(Object, Predicate)}.
   * Use this method when you need to verify multiple values.
   *
   * @param statements Statements to be verified
   */
  public static void requireStates(Statement<?>... statements) {
    List<?> values = Arrays.stream(statements).map(Statement::statementValue).collect(toList());
    Requires.requireState(values, Statement.createPredicateForAllOf(statements));
  }

  public static <T> T ensureStatement(Statement<T> statement) {
    return Ensures.ensure(statement.statementValue(), statement.statementPredicate());
  }

  /**
   * Fluent version of {@link Ensures#ensure(Object, Predicate)}.
   * Use this method when you need to verify multiple values.
   *
   * @param statements Statements to be verified
   */
  public static void ensureAll(Statement<?>... statements) {
    List<?> values = Arrays.stream(statements).map(Statement::statementValue).collect(toList());
    Ensures.ensure(values, Statement.createPredicateForAllOf(statements));
  }

  /**
   * Fluent version of {@link Ensures#ensureState(Object, Predicate)}.
   * Use this method when you need to verify multiple values.
   *
   * @param statement A statement to be verified
   */
  public static <T> T ensureState(Statement<T> statement) {
    return Ensures.ensureState(statement.statementValue(), statement.statementPredicate());
  }

  /**
   * Fluent version of {@link Ensures#ensureState(Object, Predicate)}.
   * Use this method when you need to verify multiple values.
   *
   * @param statements Statements to be verified
   */
  public static void ensureStates(Statement<?>... statements) {
    List<?> values = Arrays.stream(statements).map(Statement::statementValue).collect(toList());
    Ensures.ensureState(values, Statement.createPredicateForAllOf(statements));
  }

  /**
   * Fluent version of {@link Assertions#that(Object, Predicate)}.
   * Use this method when you need to verify multiple values.
   *
   * @param statement A statement to be verified
   */
  public static <T> boolean that(Statement<T> statement) {
    return Assertions.that(statement.statementValue(), statement.statementPredicate());
  }

  /**
   * Fluent version of {@link Assertions#that(Object, Predicate)}.
   * Use this method when you need to verify multiple values.
   *
   * @param statements Statements to be verified
   */
  public static boolean all(Statement<?>... statements) {
    List<?> values = Arrays.stream(statements).map(Statement::statementValue).collect(toList());
    return Assertions.that(values, Statement.createPredicateForAllOf(statements));
  }

  /**
   * Fluent version of {@link Assertions#precondition(Object, Predicate)}.
   * Use this method when you need to verify multiple values.
   *
   * @param statement A statement to be verified
   */
  public static <T> boolean precondition(Statement<T> statement) {
    return Assertions.precondition(statement.statementValue(), statement.statementPredicate());
  }

  /**
   * Fluent version of {@link Assertions#precondition(Object, Predicate)}.
   * Use this method when you need to verify multiple values.
   *
   * @param statements Statements to be verified
   */
  public static boolean preconditions(Statement<?>... statements) {
    List<?> values = Arrays.stream(statements).map(Statement::statementValue).collect(toList());
    return Assertions.precondition(values, Statement.createPredicateForAllOf(statements));
  }

  /**
   * Fluent version of {@link Assertions#postcondition(Object, Predicate)}.
   *
   * @param statement A statement to be verified
   */
  public static <T> boolean postcondition(Statement<T> statement) {
    return Assertions.postcondition(statement.statementValue(), statement.statementPredicate());
  }

  /**
   * Fluent version of {@link Assertions#postcondition(Object, Predicate)}.
   * Use this method when you need to verify multiple values.
   *
   * @param statements Statements to be verified
   */
  public static boolean postconditions(Statement<?>... statements) {
    List<?> values = Arrays.stream(statements).map(Statement::statementValue).collect(toList());
    return Assertions.postcondition(values, Statement.createPredicateForAllOf(statements));
  }

  public static <E> ObjectTransformer<E, E> value(E object) {
    return Statement.objectValue(object);
  }
}
