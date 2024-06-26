package com.github.valid8j.ut.propertybased.tests;

import com.github.valid8j.pcond.forms.Predicates;
import com.github.valid8j.ut.propertybased.utils.*;
import org.junit.ComparisonFailure;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class NegatedPredicateTest extends PropertyBasedTestBase {

  public NegatedPredicateTest(String testName, TestCase<?, ?> testCase) {
    super(testName, testCase);
  }

  @Parameterized.Parameters(name = "{index}: {0}")
  public static Iterable<Object[]> parameters() {
    return TestCaseUtils.parameters(NegatedPredicateTest.class);
  }

  @TestCaseParameter
  public static TestCase<String, Throwable> givenNegatedSimplePredicateReturningFalse_whenExpectedValue_thenValueReturned() {
    return new TestCase.Builder.ForReturnedValue<>("HELLO", Predicates.not(Predicates.isEqualTo("hello")), String.class)
        .addExpectationPredicate(TestCheck.equalsPredicate("HELLO"))
        .build();
  }

  @TestCaseParameter
  public static TestCase<String, ComparisonFailure> givenNegatedSimplePredicateReturningTrue_whenUnexpectedValue_thenComparisonFailureThrown() {
    return new TestCase.Builder.ForThrownException<>(
        "Hello",
        Predicates.not(Predicates.isEqualTo("Hello")),
        ComparisonFailure.class)
        .addCheck(TestCheck.numberOfActualSummariesIsEqualTo(1))
        .addCheck(TestCheck.numberOfExpectSummariesWithDetailsIsEqualTo(1))
        .addCheck(TestCheck.numbersOfExpectAndActualSummariesAreEqual())
        .addCheck(TestCheck.numbersOfExpectAndActualSummariesWithDetailsAreEqual())
        .build();
  }
}
