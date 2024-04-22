package com.github.valid8j.pcond.core.fluent;

import com.github.valid8j.pcond.fluent.Statement;
import com.github.valid8j.pcond.forms.Functions;
import com.github.valid8j.pcond.internals.InternalChecks;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

public interface Transformer<
    TX extends Transformer<TX, V, T, R>,  // SELF
    V extends Checker<V, T, R>,
    T,
    R> extends
    Matcher<TX, T, R>,
        Statement<T> {

  TX addTransformAndCheckClause(Function<Transformer<?, ?, R, R>, Predicate<R>> clause);

  /**
   * Returns a checker object for this object.
   * @return A checker object for this object.
   */
  V then();

  @SuppressWarnings("unchecked")
  default TX satisfies(Function<TX, Statement<T>> clause) {
    requireNonNull(clause);
    return this.addTransformAndCheckClause(tx -> (Predicate<R>) clause.apply((TX) tx).statementPredicate());
  }

  /**
   * A synonym of {@link Transformer#then()} method.
   * @return A checker object for this object.
   */
  default V satisfies() {
    return then();
  }

  /**
   * A synonym of {@link Transformer#then()} method.
   * @return A checker object for this object.
   */
  default V toBe() {
    return then();
  }

  <TY extends Transformer<TY, W, T, RR>,
      W extends Checker<W, T, RR>,
      RR>
  TY transformValueWith(Function<? super R, RR> func, BiFunction<Supplier<T>, Function<T, RR>, TY> transformerFactory);

  abstract class Base<
      TX extends Transformer<TX, V, T, R>,  // SELF
      V extends Checker<V, T, R>,
      T,
      R> extends
      Matcher.Base<
          TX,
          T,
          R> implements
      Transformer<
          TX,
          V,
          T,
          R> {

    protected Base(Supplier<T> baseValue, Function<T, R> transformFunction) {
      super(baseValue, transformFunction);
    }

    public V then() {
      InternalChecks.requireState(this, Matcher.Base::hasNoChild, v -> format("Predicate is already added. %s", v.childPredicates()));
      return toChecker(this.transformFunction());
    }

    public <
        TY extends Transformer<TY, W, T, RR>,
        W extends Checker<W, T, RR>,
        RR>
    TY transformValueWith(Function<? super R, RR> func, BiFunction<Supplier<T>, Function<T, RR>, TY> transformerFactory) {
      Function<T, R> tf = transformFunction();
      @SuppressWarnings("unchecked") Function<T, RR> transformFunction = Objects.equals(tf, Functions.identity()) ?
          (Function<T, RR>) func :
          tf.andThen(func);
      return transformerFactory.apply(this::baseValue, transformFunction);
    }

    @SuppressWarnings("unchecked")
    @Override
    public TX addTransformAndCheckClause(Function<Transformer<?, ?, R, R>, Predicate<R>> clause) {
      return this.addPredicate(tx -> clause.apply((Transformer<?, ?, R, R>) tx));
    }

    @Override
    public T statementValue() {
      return baseValue();
    }

    @Override
    public Predicate<T> statementPredicate() {
      return toPredicate();
    }

    protected abstract V toChecker(Function<T, R> transformFunction);
  }
}
