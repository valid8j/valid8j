package com.github.valid8j.ut.propertybased.utils;

import org.junit.ComparisonFailure;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.github.valid8j.pcond.internals.InternalUtils.formatObject;
import static com.github.valid8j.ut.propertybased.utils.ReportCheckUtils.makePrintableFunction;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

public interface TestCase<V, T extends Throwable> {
  Predicate<V> targetPredicate();

  V targetValue();

  Optional<Expectation<T>> expectationForThrownException();

  Optional<Expectation<V>> expectationForReturnedValue();

  interface Expectation<E> {
    Class<E> expectedClass();

    List<TestCheck<E, ?>> checks();
  }

  abstract class Builder<B extends Builder<B, V, T>, V, T extends Throwable> {
    private final V value;
    Predicate<V>   predicate;
    Expectation<V> expectationForReturnedValue   = null;
    Expectation<T> expectationForThrownException = null;

    public Builder(V value) {
      this.value = value;
    }

    public Builder(V value, Predicate<V> predicate) {
      this(value);
      this.predicate(predicate);
    }

    @SuppressWarnings("unchecked")
    public B predicate(Predicate<V> predicate) {
      this.predicate = requireNonNull(predicate);
      return (B) this;
    }

    public TestCase<V, T> build() {
      return new TestCase<V, T>() {
        @Override
        public Predicate<V> targetPredicate() {
          return predicate;
        }

        @Override
        public V targetValue() {
          return value;
        }

        @Override
        public Optional<Expectation<T>> expectationForThrownException() {
          return Optional.ofNullable(expectationForThrownException);
        }

        @Override
        public Optional<Expectation<V>> expectationForReturnedValue() {
          return Optional.ofNullable(expectationForReturnedValue);
        }

        @Override
        public String toString() {
          String s = expectationForThrownException()
              .map(v -> "exceptionThrown:" + v )
              .orElseGet(() -> expectationForReturnedValue()
                  .map(v -> "valueReturned:" + v)
                  .orElseThrow(UnsupportedOperationException::new));
          return format("Given:<%s>:When:<%s>:Then:<%s>", formatObject(targetValue()), targetPredicate(), s);
        }
      };
    }

    public static class ForReturnedValue<V> extends Builder<ForReturnedValue<V>, V, Throwable> {
      private       Class<V>              expectedClass;
      private final List<TestCheck<V, ?>> expectations = new LinkedList<>();

      public ForReturnedValue(V value, Predicate<V> predicate, Class<V> expectedClass) {
        this(value, predicate);
        this.expectedClass(expectedClass);
      }

      public ForReturnedValue(V value, Predicate<V> predicate) {
        this(value);
        predicate(predicate);
      }

      public ForReturnedValue(V value) {
        super(value);
      }

      public ForReturnedValue<V> expectedClass(Class<V> expectedClass) {
        this.expectedClass = requireNonNull(expectedClass);
        return this;
      }

      public ForReturnedValue<V> addExpectationPredicate(Predicate<V> predicate) {
        return this.addExpectationPredicate(makePrintableFunction("identity", Function.identity()), predicate);
      }

      public <W> ForReturnedValue<V> addExpectationPredicate(Function<V, W> function, Predicate<W> predicate) {
        this.expectations.add(new TestCheck<>(function, predicate));
        return this;
      }

      @Override
      public TestCase<V, Throwable> build() {
        this.expectationForReturnedValue = new Expectation<V>() {

          @Override
          public Class<V> expectedClass() {
            return expectedClass;
          }

          @Override
          public List<TestCheck<V, ?>> checks() {
            return expectations;
          }

          @Override
          public String toString() {
            return format("%s",  checks());
          }
        };
        return super.build();
      }
    }

    public static class ForThrownException<V, T extends Throwable> extends Builder<ForThrownException<V, T>, V, T> {

      private final List<TestCheck<T, ?>> expectations = new LinkedList<>();
      private       Class<T>              expectedExceptionClass;

      public ForThrownException(V value) {
        super(value);
      }

      public ForThrownException(V value, Predicate<V> predicate) {
        this(value);
        this.predicate(predicate);
      }

      public ForThrownException(V value, Predicate<V> predicate, Class<T> expectedExceptionClass) {
        this(value, predicate);
        this.expectedExceptionClass(expectedExceptionClass);
      }

      @SuppressWarnings({ "unchecked", "rawtypes" })
      public ForThrownException<V, T> expectedExceptionClass(Class<T> expectedExceptionClass) {
        this.expectedExceptionClass = requireNonNull(expectedExceptionClass);
        if (ComparisonFailure.class.isAssignableFrom(expectedExceptionClass))
          return ((ForThrownException) this).configure(TestCheck.genericConfiguratorForComparisonFailure());
        return this;
      }

      public ForThrownException<V, T> configure(Consumer<ForThrownException<V, T>> consumer) {
        consumer.accept(this);
        return this;
      }

      public ForThrownException<V, T> addCheck(Predicate<T> predicate) {
        return addCheck(TestCheck.createFromSimplePredicate(predicate));
      }

      public ForThrownException<V, T> addCheck(TestCheck<T, ?> fromSimplePredicate) {
        this.expectations.add(fromSimplePredicate);
        return this;
      }

      @Override
      public TestCase<V, T> build() {
        this.expectationForThrownException = new Expectation<T>() {
          @Override
          public Class<T> expectedClass() {
            return expectedExceptionClass;
          }

          @Override
          public List<TestCheck<T, ?>> checks() {
            return expectations;
          }

          @Override
          public String toString() {
            return format("%s:%s", expectedClass().getSimpleName(), checks());
          }
        };
        return super.build();
      }
    }
  }
}
