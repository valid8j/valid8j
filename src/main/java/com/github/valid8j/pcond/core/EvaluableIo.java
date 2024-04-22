package com.github.valid8j.pcond.core;

import static com.github.valid8j.pcond.core.EvaluationContext.formNameOf;
import static java.util.Objects.requireNonNull;

public class EvaluableIo<I, E extends Evaluable<I>, O> {
  private final ValueHolder<I> input;
  private final E                    evaluable;
  private final EvaluationEntry.Type evaluableType;
  private final String               formName;
  private ValueHolder<O> output;

  public EvaluableIo(ValueHolder<I> input, EvaluationEntry.Type evaluableType, E evaluable) {
    this(input, evaluableType, formNameOf(evaluableType, evaluable), evaluable);
  }

  public EvaluableIo(ValueHolder<I> input, EvaluationEntry.Type evaluableType, String formName, E evaluable) {
    this.input = requireNonNull(input);
    this.evaluableType = requireNonNull(evaluableType);
    this.formName = formName;
    this.evaluable = requireNonNull(evaluable);
    this.output = ValueHolder.create();
  }

  public void output(ValueHolder<O> output) {
    this.output = requireNonNull(output).clone();
  }

  public ValueHolder<I> input() {
    return this.input;
  }

  public EvaluationEntry.Type evaluableType() {
    return this.evaluableType;
  }

  public String formName() {
    return this.formName;
  }

  public E evaluable() {
    return this.evaluable;
  }

  public ValueHolder<O> output() {
    return this.output;
  }

  @Override
  public String toString() {
    return "evaluable:<" + evaluableType + ":" + evaluable + "> in:" + input + " out:" + output;
  }
}
