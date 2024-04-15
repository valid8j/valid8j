package com.github.dakusui.shared.utils;

import org.junit.runner.Result;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.function.Predicate;

import static com.github.dakusui.shared.utils.TestClassExpectation.ResultPredicateFactory.IgnoreCountIsEqualTo;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(TYPE)
public @interface TestClassExpectation {
  EnsureJUnitResult[] value() default {};

  @interface EnsureJUnitResult {
    Class<? extends ResultPredicateFactory> type();

    String[] args();
  }

  interface ResultPredicateFactory {
    static Predicate<Result> createPredicate(EnsureJUnitResult ann) {
      try {
        return ann.type().newInstance().create(ann.args());
      } catch (InstantiationException | IllegalAccessException e) {
        throw new RuntimeException(e);
      }
    }

    Predicate<Result> create(String... args);

    class IgnoreCountIsEqualTo implements ResultPredicateFactory {
      @Override
      public Predicate<Result> create(String... args) {
        final int expected = Integer.parseInt(args[0]);
        return result -> result.getIgnoreCount() == expected;
      }
    }

    class AssumptionFailureCountIsEqualTo implements ResultPredicateFactory {
      @Override
      public Predicate<Result> create(String... args) {
        final int expected = Integer.parseInt(args[0]);
        return result -> result.getAssumptionFailureCount() == expected;
      }
    }

    class WasSuccessful implements ResultPredicateFactory {
      @Override
      public Predicate<Result> create(String... args) {
        return Result::wasSuccessful;
      }
    }

    class WasNotSuccessful implements ResultPredicateFactory {
      @Override
      public Predicate<Result> create(String... args) {
        return new WasSuccessful().create(args).negate();
      }
    }

    class RunCountIsEqualTo implements ResultPredicateFactory {
      @Override
      public Predicate<Result> create(String... args) {
        final int expected = Integer.parseInt(args[0]);
        return result -> result.getRunCount() == expected;
      }
    }

    class SizeOfFailuresIsEqualTo implements ResultPredicateFactory {
      @Override
      public Predicate<Result> create(String... args) {
        final int expected = Integer.parseInt(args[0]);
        return result -> result.getFailures().size() == expected;
      }
    }
  }

  @TestClassExpectation(@EnsureJUnitResult(type = IgnoreCountIsEqualTo.class, args = "1"))
  class Example {
    public static void main(String... args) {
      Result result = new Result();
      result.getFailures();
      result.wasSuccessful();
      result.getIgnoreCount();
      result.getRunCount();
      result.getRunTime();
    }
  }
}
