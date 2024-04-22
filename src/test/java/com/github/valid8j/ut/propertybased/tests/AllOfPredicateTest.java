package com.github.valid8j.ut.propertybased.tests;

import com.github.valid8j.pcond.forms.Predicates;
import com.github.valid8j.ut.propertybased.utils.PropertyBasedTestBase;
import com.github.valid8j.ut.propertybased.utils.TestCase;
import com.github.valid8j.ut.propertybased.utils.TestCaseParameter;
import com.github.valid8j.ut.propertybased.utils.TestCaseUtils;
import org.junit.ComparisonFailure;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import static com.github.valid8j.utils.TestUtils.alwaysFalse;
import static com.github.valid8j.utils.TestUtils.expectationSummarySizeIsEqualTo;
import static com.github.valid8j.pcond.forms.Predicates.alwaysTrue;

@RunWith(Parameterized.class)
public class AllOfPredicateTest extends PropertyBasedTestBase {

  public AllOfPredicateTest(String testName, TestCase<?, ?> testCase) {
    super(testName, testCase);
  }

  @Parameters(name = "{index}: {0}")
  public static Iterable<Object[]> parameters() {
    return TestCaseUtils.parameters(AllOfPredicateTest.class);
  }

  @TestCaseParameter
  public static TestCase<String, Throwable> whenPredicatesAllReturningTrueUnderAllOf_thenPasses() {
    return new TestCase.Builder.ForReturnedValue<>(
        "Hello",
        Predicates.allOf(alwaysTrue(), alwaysTrue()),
        String.class)
        .build();
  }

  @TestCaseParameter
  public static TestCase<String, ComparisonFailure> whenPredicatesFirstReturningFalseRestReturningTrueUnderAllOf_whenComparisonFailure() {
    return new TestCase.Builder.ForThrownException<>(
        "Hello",
        Predicates.allOf(alwaysFalse(), alwaysTrue(), alwaysTrue()),
        ComparisonFailure.class)
        .addCheck(expectationSummarySizeIsEqualTo(1 /*all*/ + 3 /*alwaysFalse, alwaysTrue, alwaysTrue*/))
        .build();
  }

  @TestCaseParameter
  public static TestCase<String, ComparisonFailure> whenOnePredicateReturningFalseUnderAllOf_whenComparisonFailure() {
    return new TestCase.Builder.ForThrownException<>(
        "Hello",
        Predicates.allOf(alwaysFalse()),
        ComparisonFailure.class)
        .addCheck(expectationSummarySizeIsEqualTo(1 /*all*/ + 1 /*alwaysFalse*/))
        .build();
  }
}
