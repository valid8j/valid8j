package com.github.valid8j.pcond.core;

import static java.util.Objects.requireNonNull;

public class ValueHolder<V> implements Cloneable {

  private final State state;
  private final CreatorFormType creatorFormType;
  V         value;
  Throwable exception;

  private ValueHolder(State state, V value, Throwable exception, CreatorFormType creatorFormType) {
    this.state = state;
    this.value = value;
    this.exception = exception;
    this.creatorFormType = creatorFormType;
  }

  @SuppressWarnings("unchecked")
  public ValueHolder<V> clone() {
    try {
      return (ValueHolder<V>) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new RuntimeException(e);
    }
  }

  public State state() {
    return this.state;
  }

  public V returnedValue() {
    return this.state.value(this);
  }

  public Throwable thrownException() {
    return this.state.exception(this);
  }

  public Object value() {
    if (isValueReturned())
      return this.returnedValue();
    if (isEvaluationSkipped())
      return Evaluator.Impl.EVALUATION_SKIPPED;
    if (isExceptionThrown())
      return this.thrownException();
    throw new IllegalStateException();
  }

  public static <V> ValueHolder<V> forValue(V value) {
    return new ValueHolder<>(State.VALUE_RETURNED, value, null, CreatorFormType.UNKNOWN);
  }

  @Override
  public String toString() {
    if (isValueReturned())
      return String.format("state:%s, value:%s, creator:%s", state, value, creatorFormType);
    if (isEvaluationSkipped())
      return String.format("state:%s, exception:%s, creator:%s", state, exception, creatorFormType);
    return String.format("state:%s, creator:%s", state, creatorFormType);
  }

  public boolean isValueReturned() {
    return this.state() == State.VALUE_RETURNED;
  }

  public boolean isExceptionThrown() {
    return this.state() == State.EXCEPTION_THROWN;
  }

  public boolean isEvaluationSkipped() {
    return this.state() == State.EVALUATION_SKIPPED;
  }

  public CreatorFormType creatorFormType() {
    return this.creatorFormType;
  }

  public ValueHolder<V> valueReturned(V value) {
    //    requireState(this.state, v -> v.equals(State.NOT_YET_EVALUATED), v -> messageNotYetEvaluatedStateIsRequired(v, this));
    return new ValueHolder<>(State.VALUE_RETURNED, value, null, this.creatorFormType);
  }

  public ValueHolder<V> exceptionThrown(Throwable throwable) {
    //    requireState(this.state, v -> v.equals(State.NOT_YET_EVALUATED), v -> messageNotYetEvaluatedStateIsRequired(v, this));
    return new ValueHolder<>(State.EXCEPTION_THROWN, null, requireNonNull(throwable), this.creatorFormType);
  }

  public ValueHolder<V> evaluationSkipped() {
    //    requireState(this.state, v -> v.equals(State.NOT_YET_EVALUATED), v -> messageNotYetEvaluatedStateIsRequired(v, this));
    return new ValueHolder<>(State.EVALUATION_SKIPPED, null, null, this.creatorFormType);
  }

  public ValueHolder<V> creatorFormType(CreatorFormType creatorFormType) {
    return new ValueHolder<>(this.state, this.value, this.exception, creatorFormType);
  }

  static <E> ValueHolder<E> create() {
    return create(CreatorFormType.UNKNOWN);
  }

  public static <E> ValueHolder<E> create(CreatorFormType creatorFormType) {
    return new ValueHolder<>(State.NOT_YET_EVALUATED, null, null, creatorFormType);
  }

  public enum State {
    NOT_YET_EVALUATED {
    },
    VALUE_RETURNED {
      <V> V value(ValueHolder<V> valueHolder) {
        return valueHolder.value;
      }
    },
    EXCEPTION_THROWN {
      <V> Throwable exception(ValueHolder<V> vContextVariable) {
        return vContextVariable.exception;
      }
    },
    EVALUATION_SKIPPED {
    };

    <V> V value(ValueHolder<V> valueHolder) {
      throw new IllegalStateException("current state=" + valueHolder.state);
    }

    <V> Throwable exception(ValueHolder<V> vContextVariable) {
      throw new IllegalStateException();
    }
  }

  enum CreatorFormType {
    FUNC_HEAD,
    FUNC_TAIL,
    TRANSFORM,
    UNKNOWN
  }
}
