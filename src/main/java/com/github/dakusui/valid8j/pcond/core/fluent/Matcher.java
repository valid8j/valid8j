package com.github.dakusui.valid8j.pcond.core.fluent;

import com.github.dakusui.valid8j.pcond.forms.Functions;
import com.github.dakusui.valid8j.pcond.forms.Predicates;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.github.dakusui.valid8j.pcond.internals.InternalChecks.requireState;
import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

public interface Matcher<
    M extends Matcher<M, T, R>,
    T,
    R> {
  M allOf();

  M anyOf();

  Predicate<T> toPredicate();

  Function<T, R> transformFunction();

  abstract class Base<
      M extends Matcher<M, T, R>,
      T,
      R> implements Matcher<M, T, R> {
    private final Function<T, R> transformFunction;
    private final Supplier<T>    baseValue;

    private final List<Function<Matcher<?, R, R>, Predicate<R>>> childPredicates = new LinkedList<>();
    private       JunctionType                           junctionType;

    private Predicate<T> builtPredicate;

    protected Base(Supplier<T> baseValue, Function<T, R> transformFunction) {
      this.transformFunction = requireNonNull(transformFunction);
      this.baseValue = requireNonNull(baseValue);
      this.allOf();
    }

    @Override
    public M allOf() {
      return junctionType(JunctionType.CONJUNCTION);
    }

    @Override
    public M anyOf() {
      return junctionType(JunctionType.DISJUNCTION);
    }

    @Override
    public Predicate<T> toPredicate() {
      if (this.builtPredicate == null)
        this.builtPredicate = buildPredicate();
      return this.builtPredicate;
    }

    protected M addPredicate(Function<Matcher<?, R, R>, Predicate<R>> clause) {
      this.childPredicates.add(requireNonNull(clause));
      return me();
    }

    public R value() {
      return this.transformFunction.apply(this.baseValue());
    }

    /* protected */
    public Function<T, R> transformFunction() {
      return this.transformFunction;
    }

    protected boolean hasNoChild() {
      return this.childPredicates().isEmpty();
    }

    protected List<Function<Matcher<?, R, R>, Predicate<R>>> childPredicates() {
      return unmodifiableList(this.childPredicates);
    }

    @SuppressWarnings("unchecked")
    protected M me() {
      return (M) this;
    }

    /**
     * Override this method so that it returns extending class.
     *
     * @return A rebased transformer.
     */
    protected abstract Matcher<?, R, R> rebase();

    /* protected */
    protected T baseValue() {
      return this.baseValue.get();
    }

    private M junctionType(JunctionType junctionType) {
      requireState(this, v -> childPredicates.isEmpty(), v -> "Child predicate(s) are already added.: <" + this + ">");
      this.junctionType = requireNonNull(junctionType);
      return me();
    }

    @SuppressWarnings("unchecked")
    private Predicate<T> buildPredicate() {
      Predicate<R> ret;
      requireState(this, v -> !v.childPredicates.isEmpty(), (v) -> "No child has been added yet.: <" + v + ">");
      if (this.childPredicates.size() == 1)
        ret = childPredicates.get(0).apply(rebase());
      else {
        ret = this.junctionType.connect(
            new ArrayList<>(this.childPredicates)
                .stream()
                .map(each -> each.apply(rebase()))
                .collect(toList()));
      }
      if (Objects.equals(transformFunction, Functions.identity()))
        return (Predicate<T>) ret;
      return Predicates.transform(transformFunction).check("THEN", ret);
    }
  }

  enum JunctionType {
    CONJUNCTION {
      @SuppressWarnings("unchecked")
      @Override
      public <T> Predicate<T> connect(List<Predicate<T>> predicates) {
        return Predicates.allOf(predicates.toArray(new Predicate[0]));
      }
    },
    DISJUNCTION {
      @SuppressWarnings("unchecked")
      @Override
      public <T> Predicate<T> connect(List<Predicate<T>> predicates) {
        return Predicates.anyOf(predicates.toArray(new Predicate[0]));
      }
    };

    public abstract <T> Predicate<T> connect(List<Predicate<T>> predicates);
  }
}
