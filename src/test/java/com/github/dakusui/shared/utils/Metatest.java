package com.github.dakusui.shared.utils;

import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

import java.util.*;
import java.util.function.Consumer;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

public class Metatest {
  private final Class<?> testClass;
  List<Throwable> errors = new LinkedList<>();
  private boolean run;

  public Metatest(Class<?> testClass) {
    this.run = false;
    this.testClass = requireNonNull(testClass);
  }

  public void runTestClass() {
    final HashMap<Description, TestMethodExpectation.TestMethodResult> testMethodResultMap = new LinkedHashMap<>();
    try {
      Result testResult = new JUnitCore() {{
        addListener(new RunListener() {
          @Override
          public void testStarted(Description description) {
            updateTestMethodResult(description, TestMethodExpectation.TestMethodResult::testStarted);
          }

          @Override
          public void testFinished(Description description) {
            testMethodResultMap.get(description).testFinished(description).ifPresent(errors::add);
          }

          @Override
          public void testFailure(Failure failure) {
            updateTestMethodResult(failure.getDescription(), TestMethodExpectation.TestMethodResult::testFailure);
          }

          @Override
          public void testAssumptionFailure(Failure failure) {
            updateTestMethodResult(failure.getDescription(), TestMethodExpectation.TestMethodResult::testAssumptionFailure);
          }

          @Override
          public void testIgnored(Description description) {
            updateTestMethodResult(description, TestMethodExpectation.TestMethodResult::testIgnored);
          }

          private void updateTestMethodResult(Description description, Consumer<TestMethodExpectation.TestMethodResult> consumer) {
            testMethodResultMap.computeIfAbsent(description, (k) -> new TestMethodExpectation.TestMethodResult());
            consumer.accept(testMethodResultMap.get(description));
          }
        });
      }}.run(this.testClass);
      TestClassExpectation testClassExpectation = testClass.getAnnotation(TestClassExpectation.class);
      if (testClassExpectation != null) {
        Arrays.stream(testClassExpectation.value())
            .forEach((TestClassExpectation.EnsureJUnitResult each) -> {
              if (!TestClassExpectation.ResultPredicateFactory.createPredicate(each).test(testResult)) {
                errors.add(new Exception(format("Failed to verify expectation:%s[%s]: result='%s'", each.type().getSimpleName(), Arrays.toString(each.args()), testResultToString(testResult))));
                testResult.getFailures().forEach(System.out::println);
              }
            });
      }
    } finally {
      this.run = true;
    }
  }

  private String testResultToString(Result result) {
    return format("%s:{wasSuccessful:%s,runCount:%s,failures.size:%s,ignoreCount:%s}", Result.class.getSimpleName(), result.wasSuccessful(), result.getRunCount(), result.getFailures().size(), result.getIgnoreCount());
  }

  public List<Throwable> verifyTestResult() {
    if (!this.run)
      throw new IllegalStateException();
    return errors;
  }

  public static void verifyTestClass(Class<?> testClass) {
    Metatest metatest = new Metatest(testClass);
    metatest.runTestClass();
    List<Throwable> errors = metatest.verifyTestResult();
    if (!errors.isEmpty()) {
      for (Throwable each : errors)
        each.printStackTrace();
      throw new RuntimeException(String.format("%s mismatch(es) are detected.", errors.size()));
    } else
      System.err.println("All verifications are green!");
  }
}
