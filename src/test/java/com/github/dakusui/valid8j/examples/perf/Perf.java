package com.github.dakusui.valid8j.examples.perf;

import com.github.dakusui.shared.ApplicationException;
import org.junit.*;
import org.junit.rules.Stopwatch;
import org.junit.runner.Description;
import org.junit.runners.MethodSorters;

import java.util.Objects;
import java.util.function.Predicate;

import static com.github.dakusui.shared.TestUtils.validate;
import static com.github.dakusui.valid8j.classic.Assertions.that;
import static com.github.dakusui.valid8j.pcond.forms.Functions.length;
import static com.github.dakusui.valid8j.pcond.forms.Predicates.*;

/**
 * 1,000,000 times
 * -ea
 * <pre>
 * assertValueWithLambda                      37[msec] (succeeded)
 * assertValueWithPrintablePredicate        3663[msec] (succeeded)
 * checkValidateValueWithLambda               21[msec] (succeeded)
 * validateValueWithLambda                    48[msec] (succeeded)
 * validateValueWithPrintablePredicate      3597[msec] (succeeded)
 * </pre>
 * -ea; useEvaluator=false
 * <pre>
 * assertValueWithLambda                      30[msec] (succeeded)
 * assertValueWithPrintablePredicate         311[msec] (succeeded)
 * checkValidateValueWithLambda               40[msec] (succeeded)
 * validateValueWithLambda                    36[msec] (succeeded)
 * validateValueWithPrintablePredicate       284[msec] (succeeded)
 * </pre>
 * -da
 * <pre>
 * assertValueWithLambda                       6[msec] (succeeded)
 * assertValueWithPrintablePredicate           4[msec] (succeeded)
 * checkValidateValueWithLambda               21[msec] (succeeded)
 * validateValueWithLambda                    45[msec] (succeeded)
 * validateValueWithPrintablePredicate      3588[msec] (succeeded)
 * </pre>
 */
@SuppressWarnings("NewClassNamingConvention")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Perf {
  @Rule
  public Stopwatch stopwatch = new Stopwatch() {
    @Override
    protected void succeeded(long nanos, Description description) {
      formatResult(nanos, description, "succeeded");
    }

    @Override
    protected void failed(long nanos, Throwable e, Description description) {
      formatResult(nanos, description, "failed");
    }

    @Override
    protected void skipped(long nanos, AssumptionViolatedException e, Description description) {
      formatResult(nanos, description, "skipped");
    }


    @Override
    protected void finished(long nanos, Description description) {
    }

    private void formatResult(long nanos, Description description, String label) {
      System.out.printf("%-40s %10s (%s)%n", description.getMethodName(), formatNanos(nanos), label);
    }

    private String formatNanos(long nanos) {
      return String.format("%s[msec]", nanos / 1_000_000);
    }
  };


  @BeforeClass
  public static void warmUp() {
    String value = "John Doe";
    for (int i = 0; i < 100_000; i++) {
      validateValueWithPrintablePredicate(value);
      validateValueWithLambda(value);
      checkValueWithLambda(value);
      assertValueWithPrintablePredicate(value);
      assertValueWithLambda(value);
    }
  }


  @Test
  public void validateValueWithPrintablePredicate() {
    for (int i = 0; i < numLoop(); i++)
      validateValueWithPrintablePredicate("John Doe");
  }

  @Test
  public void validateValueWithLambda() {
    for (int i = 0; i < numLoop(); i++)
      validateValueWithLambda("John Doe");
  }

  @Test
  public void checkValidateValueWithLambda() {
    for (int i = 0; i < numLoop(); i++)
      checkValueWithLambda("John Doe");
  }

  @Test
  public void assertValueWithPrintablePredicate() {
    for (int i = 0; i < numLoop(); i++)
      assertValueWithPrintablePredicate("John Doe");
  }

  @Test
  public void assertValueWithLambda() {
    for (int i = 0; i < numLoop(); i++)
      assertValueWithLambda("John Doe");
  }

  private int numLoop() {
    return 1_000_000;
  }


  private static void validateValueWithPrintablePredicate(String value) {
    validate(value, and(isNotNull(), transform(length()).check(gt(0)), containsString(" ")), ApplicationException::new);
  }

  private static void validateValueWithLambda(String value) {
    validate(value, ((Predicate<String>) Objects::nonNull).and(v -> v.length() > 0).and(v -> v.contains(" ")), ApplicationException::new);
  }

  private static void checkValueWithLambda(String value) {
    if (!((Predicate<String>) Objects::nonNull).and(v -> v.length() > 0).and(v -> v.contains(" ")).test(value))
      throw new RuntimeException();
  }

  private static void assertValueWithPrintablePredicate(String value) {
    assert that(value, and(isNotNull(), transform(length()).check(gt(0)), containsString(" ")));
  }

  private static void assertValueWithLambda(String value) {
    assert that(value, ((Predicate<String>) Objects::nonNull).and(v -> v.length() > 0).and(v -> v.contains(" ")));
  }
}
