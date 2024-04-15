package com.github.dakusui.valid8j_pcond.propertybased.tests;

import com.github.dakusui.valid8j.pcond.forms.Predicates;
import com.github.dakusui.valid8j_pcond.propertybased.utils.*;
import org.junit.ComparisonFailure;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class SimplePredicateTest extends PropertyBasedTestBase {

  public SimplePredicateTest(String testName, TestCase<?, ?> testCase) {
    super(testName, testCase);
  }

  @Parameters(name = "{index}: {0}")
  public static Iterable<Object[]> parameters() {
    return TestCaseUtils.parameters(SimplePredicateTest.class);
  }

  @TestCaseParameter
  public static TestCase<String, Throwable> givenSimplePredicate_whenExpectedValue_thenValueReturned() {
    return new TestCase.Builder.ForReturnedValue<>("HELLO", Predicates.isEqualTo("HELLO"))
        .expectedClass(String.class)
        .addExpectationPredicate(TestCheck.equalsPredicate("HELLO"))
        .build();
  }

  @TestCaseParameter
  public static TestCase<String, ComparisonFailure> givenSimplePredicate_whenUnexpectedValue_thenComparisonFailureThrown() {
    return new TestCase.Builder.ForThrownException<>("Hello", Predicates.isEqualTo("HELLO"), ComparisonFailure.class)
        .addCheck(TestCheck.numberOfActualSummariesIsEqualTo(1))
        .addCheck(TestCheck.numbersOfExpectAndActualSummariesAreEqual())
        .build();
  }
}
