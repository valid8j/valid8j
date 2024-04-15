package com.github.dakusui.valid8j_pcond.propertybased.tests;

import com.github.dakusui.shared.utils.TestUtils;
import com.github.dakusui.valid8j.pcond.forms.Predicates;
import com.github.dakusui.valid8j_pcond.propertybased.utils.*;
import org.junit.ComparisonFailure;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import static com.github.dakusui.valid8j.pcond.forms.Predicates.alwaysTrue;
import static com.github.dakusui.valid8j.pcond.forms.Predicates.anyOf;

@RunWith(Parameterized.class)
public class AnyOfPredicateTest extends PropertyBasedTestBase {

  public AnyOfPredicateTest(String testName, TestCase<?, ?> testCase) {
    super(testName, testCase);
  }

  @Parameters(name = "{index}: {0}")
  public static Iterable<Object[]> parameters() {
    return TestCaseUtils.parameters(AnyOfPredicateTest.class);
  }

  @TestCaseParameter
  public static TestCase<String, Throwable> whenPredicatesFirstReturningFalseRestReturningTrueUnderAnyOf_thenPass() {
    return new TestCase.Builder.ForReturnedValue<>(
        "Hello",
        anyOf(TestUtils.alwaysFalse(), alwaysTrue(), alwaysTrue()),
        String.class)
        .build();
  }

  @TestCaseParameter
  public static TestCase<String, ComparisonFailure> whenOnePredicateReturningFalseUnderAnyOf_whenComparisonFailure() {
    return new TestCase.Builder.ForThrownException<>(
        "Hello",
        Predicates.anyOf(TestUtils.alwaysFalse()), ComparisonFailure.class)
        .addCheck(TestUtils.expectationSummarySizeIsEqualTo(1 + /* anyOf */ +1 /*alwaysFalse*/))
        .addCheck(TestCheck.numberOfExpectSummariesWithDetailsIsEqualTo(1))
        .build();
  }

  @TestCaseParameter
  public static TestCase<String, Throwable> whenPredicatesAllReturningTrueUnderAnyOf_thenPasses() {
    return new TestCase.Builder.ForReturnedValue<>(
        "Hello",
        anyOf(alwaysTrue(), alwaysTrue()),
        String.class)
        .build();
  }
}
