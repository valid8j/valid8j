package com.github.valid8j.ut.propertybased.tests;

import com.github.valid8j.pcond.forms.Predicates;
import com.github.valid8j.ut.propertybased.utils.*;
import org.junit.ComparisonFailure;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.stream.Stream;

import static com.github.valid8j.pcond.forms.Predicates.containsString;
import static com.github.valid8j.pcond.forms.Predicates.isNotNull;

@RunWith(Parameterized.class)
public class StreamAllMatchPredicateTest extends PropertyBasedTestBase {

  public StreamAllMatchPredicateTest(String testName, TestCase<?, ?> testCase) {
    super(testName, testCase);
  }

  @Parameterized.Parameters(name = "{index}: {0}")
  public static Iterable<Object[]> parameters() {
    return TestCaseUtils.parameters(StreamAllMatchPredicateTest.class);
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  @TestCaseParameter
  public static TestCase<Stream<String>, Throwable> givenStreamPredicate_whenExpectedValue_thenValueReturned() {
    Stream<String> v;
    return new TestCase.Builder.ForReturnedValue<>(
        v = Stream.of("hello", "world"),
        Predicates.allMatch(isNotNull()),
        (Class<Stream<String>>) (Class) Stream.class)
        .addExpectationPredicate(TestCheck.equalsPredicate(v))
        .build();
  }

  @TestCaseParameter
  public static TestCase<Stream<String>, ComparisonFailure> givenStreamPredicate_whenUnexpectedValue_thenComparisonFailure() {
    Stream<String> v;
    return new TestCase.Builder.ForThrownException<>(
        v = Stream.of("hello", "world", "HELLO", "WORLD"),
        Predicates.allMatch(containsString("o")),
        ComparisonFailure.class)
        .addCheck(TestCheck.numberOfActualSummariesIsEqualTo(4))
        .addCheck(TestCheck.numbersOfExpectAndActualSummariesAreEqual())
        .build();
  }

  @TestCaseParameter
  public static TestCase<Stream<String>, ComparisonFailure> givenStreamPredicate_whenUnexpectedNullValue_thenComparisonFailure() {
    Stream<String> v;
    return new TestCase.Builder.ForThrownException<>(
        v = Stream.of("hello", "world", null, "HELLO", "WORLD"),
        Predicates.allMatch(isNotNull()),
        ComparisonFailure.class)
        .addCheck(TestCheck.numberOfActualSummariesIsEqualTo(4))
        .addCheck(TestCheck.numbersOfExpectAndActualSummariesAreEqual())
        .build();
  }
}
