package com.github.dakusui.shared.utils;

import org.junit.runner.Description;

import java.lang.annotation.Retention;
import java.util.List;
import java.util.Optional;

import static com.github.dakusui.shared.utils.TestMethodExpectation.Result.*;
import static com.github.dakusui.shared.utils.TestMethodExpectation.TestMethodResult.State.NOT_STARTED;
import static com.github.dakusui.shared.utils.TestMethodExpectation.TestMethodResult.State.STARTED;
import static java.lang.String.format;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

@Retention(RUNTIME)
public @interface TestMethodExpectation {
  enum Result {
    PASSING,
    FAILURE,
    ASSUMPTION_FAILURE,
    IGNORED;
  }

  class TestMethodResult {
    enum State {
      NOT_STARTED,
      STARTED
    }

    State  state  = NOT_STARTED;
    Result result = PASSING;

    void testStarted() {
      requireState(this.state, NOT_STARTED);
      this.state = STARTED;
    }

    Optional<Exception> testFinished(Description description) {
      requireState(this.state, STARTED);
      try {
        TestMethodExpectation annotation = description.getAnnotation(TestMethodExpectation.class);
        List<Result> expectations;
        if (annotation != null)
          expectations = asList(annotation.value());
        else
          expectations = singletonList(PASSING);
        if (!expectations.contains(this.result))
          return Optional.of(new Exception(format("Expected test result(s) are %s, but actually it was '%s', which is not contained in the expectation.", expectations, this.result)));
        return Optional.empty();
      } finally {
        this.state = NOT_STARTED;
        this.result = PASSING;
      }
    }

    void testFailure() {
      requireState(this.state, STARTED);
      this.result = FAILURE;
    }

    void testAssumptionFailure() {
      requireState(this.state, STARTED);
      this.result = ASSUMPTION_FAILURE;
    }

    void testIgnored() {
      requireState(this.state, STARTED);
      this.result = IGNORED;
    }

    private static void requireState(State currentState, State expectedState) {
      if (currentState != expectedState)
        throw new IllegalStateException(format("Expected state was '%s' but it actually was '%s'", expectedState, currentState));
    }
  }

  Result[] value() default { PASSING };
}
