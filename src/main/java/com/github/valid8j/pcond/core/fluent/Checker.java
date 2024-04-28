package com.github.valid8j.pcond.core.fluent;

import com.github.valid8j.pcond.fluent.Statement;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

/**
 * An interface that defines methods to check a target value.
 * By default, calls of checking methods defined in this and sub-interfaces of this interface will be a conjunction of them.
 * To make it a disjunction, call {@link this#anyOf()}.
 *
 * User-exposing methods to check values in this method names should be progressive and its objective so that they can
 * follow "to be" or "satisfies" in an English sentence.
 *
 * @param <V> The type of the checker.
 * @param <T> The type from which the target value is transformed.
 * @param <R> The type of the target value.
 */
public interface Checker<
    V extends Checker<V, T, R>,
    T,
    R> extends
    Matcher<V, T, R>,
        Statement<T> {
  V addCheckPhrase(Function<Checker<?, R, R>, Predicate<R>> clause);
  
  /**
   * Checks if the target value satisfies the given `predicate`.
   *
   * @param predicate A predicate to check the target value.
   * @return This object.
   */
  @SuppressWarnings("unchecked")
  default V checkWithPredicate(Predicate<? super R> predicate) {
    requireNonNull(predicate);
    return addCheckPhrase(w -> (Predicate<R>) predicate);
  }
  
  /**
   * A synonym of {@link this#checkWithPredicate(Predicate)}.
   *
   * @param predicate A predicate to check the target value.
   * @return this object.
   */
  default V predicate(Predicate<? super R> predicate) {
    return checkWithPredicate(predicate);
  }
  
  /**
   * Convert this object into a printable predicate to check the target value.
   *
   * @return A predicate to check the target value.
   */
  default Predicate<T> done() {
    return statementPredicate();
  }
  
  /**
   * // @formatter:off
   * When you use an assertion method that accepts multiple statements (`Statement`), it requires all the elements in the array (`varargs`) should have the same generic parameter type.
   * However, you sometimes want to check multiple types at once.
   * By calling this method for every statement building method calling chain, you can address the compilation error.
   *
   * [source, java]
   * ```
   * class Example {
   *   public static void main(String... args) {
   *     assert all(
   *        objectValue(arg[0]).isNotNull().$(),
   *        objectValue(new Example()).isNotNull().$(),
   *        ...
   *     );
   *   }
   * }
   * ```
   *
   * // @formatter.off
   *
   * @return A statement for `java.lang.Object` type.
   */
  @SuppressWarnings("unchecked")
  default Statement<Object> $() {
    return (Statement<Object>) this;
  }

  abstract class Base<
      V extends Checker<V, T, R>,
      T,
      R> extends
      Matcher.Base<
          V,
          T,
          R
          > implements
      Checker<
          V,
          T,
          R> {
    public Base(Supplier<T> baseValue, Function<T, R> transformFunction) {
      super(baseValue, transformFunction);
    }

    @SuppressWarnings("unchecked")
    @Override
    public V addCheckPhrase(Function<Checker<?, R, R>, Predicate<R>> clause) {
      return this.addPredicate((Matcher<?, R, R> v) -> clause.apply((Checker<?, R, R>) v));
    }

    @Override
    public T statementValue() {
      return this.baseValue();
    }

    @Override
    public Predicate<T> statementPredicate() {
      return toPredicate();
    }
  }
}
