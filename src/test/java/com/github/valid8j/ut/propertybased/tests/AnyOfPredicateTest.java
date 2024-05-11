package com.github.valid8j.ut.propertybased.tests;

import com.github.valid8j.pcond.forms.Predicates;
import com.github.valid8j.ut.propertybased.utils.*;
import com.github.valid8j.utils.TestUtils;
import org.junit.ComparisonFailure;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import static com.github.valid8j.pcond.forms.Predicates.alwaysTrue;
import static com.github.valid8j.pcond.forms.Predicates.anyOf;
import static com.github.valid8j.ut.propertybased.utils.TestCheck.*;

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
  public static TestCase<String, ComparisonFailure> whenOnePredicateReturningFalseUnderAnyOf_thenComparisonFailure() {
    return new TestCase.Builder.ForThrownException<>(
        "Hello",
        Predicates.anyOf(TestUtils.alwaysFalse()), ComparisonFailure.class)
        .addCheck(expectationSummarySizeIsEqualTo(1 + /* anyOf */ +1 /*alwaysFalse*/))
        .addCheck(numberOfExpectSummariesWithDetailsIsEqualTo(1))
        .build();
  }
  
  @TestCaseParameter
  public static TestCase<String, ComparisonFailure> whenOnePredicateThrowingExceptionUnderAnyOf_thenComparisonFailure() {
    return new TestCase.Builder.ForThrownException<>(
        "Hello",
        Predicates.anyOf(alwaysTrue(), TestUtils.throwExceptionWithMessage("INTENTIONAL EXCEPTION")), ComparisonFailure.class)
        .addCheck(expectationSummarySizeIsEqualTo(/* anyOf */1 /* always true*/ + 1 /*intentional exception*/ + 1))
        .addCheck(numberOfExpectSummariesWithDetailsIsEqualTo(1))
        .addCheck(actualDetailAtContainsToken(0, "INTENTIONAL EXCEPTION"))
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
