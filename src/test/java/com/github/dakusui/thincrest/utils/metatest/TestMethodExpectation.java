package com.github.dakusui.thincrest.utils.metatest;

import org.junit.runner.Description;

import java.lang.annotation.Retention;
import java.util.List;
import java.util.Optional;

import static com.github.dakusui.thincrest.utils.metatest.TestMethodExpectation.Result.*;
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

    State  state  = State.NOT_STARTED;
    Result result = PASSING;

    void testStarted() {
      requireState(this.state, State.NOT_STARTED);
      this.state = State.STARTED;
    }

    Optional<Exception> testFinished(Description description) {
      requireState(this.state, State.STARTED);
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
        this.state = State.NOT_STARTED;
        this.result = PASSING;
      }
    }

    void testFailure() {
      requireState(this.state, State.STARTED);
      this.result = FAILURE;
    }

    void testAssumptionFailure() {
      requireState(this.state, State.STARTED);
      this.result = ASSUMPTION_FAILURE;
    }

    void testIgnored() {
      requireState(this.state, State.STARTED);
      this.result = IGNORED;
    }

    private static void requireState(State currentState, State expectedState) {
      if (currentState != expectedState)
        throw new IllegalStateException(format("Expected state was '%s' but it actually was '%s'", expectedState, currentState));
    }
  }

  Result[] value() default { PASSING };
}
