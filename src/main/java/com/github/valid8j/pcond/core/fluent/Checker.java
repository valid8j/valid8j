package com.github.valid8j.pcond.core.fluent;

import com.github.valid8j.pcond.fluent.Statement;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

public interface Checker<
    V extends Checker<V, T, R>,
    T,
    R> extends
    Matcher<V, T, R>,
        Statement<T> {
  V addCheckPhrase(Function<Checker<?, R, R>, Predicate<R>> clause);

  @SuppressWarnings("unchecked")
  default V checkWithPredicate(Predicate<? super R> predicate) {
    requireNonNull(predicate);
    return addCheckPhrase(w -> (Predicate<R>) predicate);
  }

  default V predicate(Predicate<? super R> predicate) {
    return checkWithPredicate(predicate);
  }

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
