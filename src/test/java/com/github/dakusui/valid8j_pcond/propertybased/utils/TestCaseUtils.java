package com.github.dakusui.valid8j_pcond.propertybased.utils;

import com.github.dakusui.valid8j.pcond.core.DebuggingUtils;
import com.github.dakusui.valid8j.pcond.fluent.Statement;
import org.junit.ComparisonFailure;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

import static com.github.dakusui.thincrest.TestAssertions.assertThat;
import static com.github.dakusui.valid8j.pcond.internals.InternalUtils.formatObject;
import static com.github.dakusui.valid8j.pcond.internals.InternalUtils.wrapIfNecessary;
import static com.github.dakusui.valid8j_pcond.propertybased.utils.ReportCheckUtils.makePrintablePredicate;
import static java.lang.String.format;
import static java.lang.reflect.Modifier.isStatic;
import static java.util.Comparator.comparing;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public enum TestCaseUtils {
  ;

  private static Object invokeStaticMethod(Method m) {
    try {
      return m.invoke(null);
    } catch (Exception e) {
      throw wrapIfNecessary(e);
    }
  }

  public static List<Object[]> parameters(@SuppressWarnings("SameParameterValue") Class<?> testClass) {
    return Arrays.stream(requireNonNull(testClass).getDeclaredMethods())
        .filter(m -> m.isAnnotationPresent(TestCaseParameter.class))
        .filter(m -> isStatic(m.getModifiers()))
        .sorted(comparing(Method::getName))
        .map(m -> new Object[] { m.getName(), invokeStaticMethod(m) })
        .collect(toList());
  }

  public static <T, E extends Throwable> void exerciseTestCase(TestCase<T, E> testCase) throws Throwable {
    try {
      T value;
      assertThat(value = testCase.targetValue(), testCase.targetPredicate());
      examineReturnedValue(testCase, value);
    } catch (Throwable t) {
      if (DebuggingUtils.passThroughComparisonFailure() && t instanceof ComparisonFailure) {
        throw t;
      }
      examineThrownException(testCase, t);
    }
  }

  public static <T> void exerciseStatementExpectingComparisonFailure(Statement<T> statement) {
    exercisePredicateExpectingComparisonFailure(statement.statementValue(), statement.statementPredicate());
  }

  public static <T> void exerciseStatementExpectingPass(Statement<T> statement) {
    exercisePredicateExpectingPass(statement.statementValue(), statement.statementPredicate());
  }

  public static <T> void exercisePredicateExpectingComparisonFailure(T value, Predicate<T> targetPredicate) {
    try {
      TestCaseUtils.exerciseTestCase(testCaseExpectingComparisonFailure(value, targetPredicate).build());
    } catch (Throwable e) {
      throw new RuntimeException(e);
    }
  }
  public static <T> void exercisePredicateExpectingPass(T value, Predicate<T> targetPredicate) {
    try {
      TestCaseUtils.exerciseTestCase(testCaseExpectingPass(value, targetPredicate).build());
    } catch (Throwable e) {
      throw new RuntimeException(e);
    }
  }
  public static <T> TestCase.Builder.ForThrownException<T, ComparisonFailure> testCaseExpectingComparisonFailure(T value, Predicate<? super T> targetPredicate) {
    return testCaseExpectingException(ComparisonFailure.class, value, targetPredicate);
  }

  @SuppressWarnings("unchecked")
  public static <E extends Throwable, T> TestCase.Builder.ForThrownException<T, E> testCaseExpectingException(Class<E> expectedExceptionClass, T value, Predicate<? super T> targetPredicate) {
    return new TestCase.Builder.ForThrownException<T, E>(value)
        .predicate((Predicate<T>) targetPredicate)
        .expectedExceptionClass(expectedExceptionClass);
  }

  @SuppressWarnings("unchecked")
  public static <T> TestCase.Builder.ForReturnedValue<T> testCaseExpectingPass(T value, Predicate<? super T> targetPredicate) {
    return new TestCase.Builder.ForReturnedValue<T>(value).predicate((Predicate<T>) targetPredicate)
        .addExpectationPredicate(makePrintablePredicate("identicalWith(value:" + value + ")" , v -> v == value));
  }

  @SuppressWarnings("unchecked")
  private static <T, E extends Throwable> void examineThrownException(TestCase<T, E> testCase, Throwable t) throws Throwable {
    if (testCase.expectationForThrownException().isPresent()) {
      TestCase.Expectation<E> exceptionExpectation = testCase.expectationForThrownException().get();
      if (exceptionExpectation.expectedClass().isAssignableFrom(t.getClass())) {
        class CheckResult {
          final TestCheck<E, ?> testDef;
          final Object          transformOutput;
          final boolean         passed;


          CheckResult(TestCheck<E, ?> testDef, Object transformOutput, boolean passed) {
            this.testDef = testDef;
            this.transformOutput = transformOutput;
            this.passed = passed;
          }
        }
        List<CheckResult> testResults = new LinkedList<>();
        for (TestCheck<E, ?> each : exceptionExpectation.checks()) {
          Object v;
          boolean passed;
          passed = ((Predicate<Object>) each.check).test(v = each.transform.apply((E) t));
          testResults.add(new CheckResult(each, v, passed));
        }
        String message = format("Thrown exception: <" + formatObject(t) + "> did not satisfy some of following conditions:%n" +
            testResults.stream()
                .map((CheckResult each) ->
                    format("%-2s %s(%s(%s)->(%s))", each.passed ? "" : "NG", each.testDef.check, each.testDef.transform, formatObject(t, 16), formatObject(each.transformOutput)))
                .collect(joining("%n- ", "----%n- ", "%n----"))) + format("%n%nTHROWN EXCEPTION DETAIL:%n") + formatException(t);
        if (testResults.stream().anyMatch(r -> !r.passed))
          throw new AssertionError(String.format("%n- GIVEN:%s%n- WHEN:%s%n- THEN:%s%n%n%s", testCase.targetValue(),testCase.targetPredicate(), testCase.expectationForThrownException().orElse(testCase.expectationForThrownException().orElseThrow(() -> new RuntimeException("InvalidTestCase?"))), message));
        else
          System.err.println(message);
      } else
        throw new AssertionError("Expected exception is '" + exceptionExpectation.expectedClass() + "' but thrown exception was: " + t, t);
    } else {
      throw t;
    }
  }

  private static Object formatException(Throwable t) {
    if (!(t instanceof ComparisonFailure))
      return t;
    StringBuilder b = new StringBuilder().append(format("%n"));
    b.append("MESSAGE:").append(format("%n"));
    b.append("- ").append(t.getMessage().replaceAll("\\n.+", ""));
    b.append("EXPECTATION:").append(format("%n"));
    for (String s : ((ComparisonFailure) t).getExpected().split("\n")) {
      b.append("  ").append(s).append(format("%n"));
    }
    b.append(format("%n"));
    b.append("ACTUAL:").append(format("%n"));
    for (String s : ((ComparisonFailure) t).getActual().split("\n")) {
      b.append("  ").append(s).append(format("%n"));
    }
    b.append(format("%n"));
    b.append("STACKTRACE:").append(format("%n"));
    for (StackTraceElement s : t.getStackTrace()) {
      b.append("  ").append(s).append(format("%n"));
    }
    return b.toString();
  }

  @SuppressWarnings("unchecked")
  private static <T, E extends Throwable> void examineReturnedValue(TestCase<T, E> testCase, T value) {
    if (testCase.expectationForThrownException().isPresent())
      throw new AssertionError("An exception that satisfies: <" + testCase.expectationForThrownException().get().expectedClass() + "> was expected to be thrown, but not");
    else if (testCase.expectationForReturnedValue().isPresent()) {
      List<TestCheck<T, ?>> errors = new LinkedList<>();
      for (TestCheck<T, ?> each : testCase.expectationForReturnedValue().get().checks()) {
        if (!((Predicate<Object>) each.check).test(each.transform.apply(value)))
          errors.add(each);
      }
      if (!errors.isEmpty())
        throw new AssertionError("Returned value: <" + value + "> did not satisfy following conditions:" + format("%n") +
            errors.stream()
                .map(each -> format("%s", each))
                .collect(joining("%n", "- ", "")));
    } else
      assert false;
  }
}
