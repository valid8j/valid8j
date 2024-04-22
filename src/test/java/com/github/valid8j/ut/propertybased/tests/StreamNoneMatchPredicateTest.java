package com.github.valid8j.ut.propertybased.tests;

import com.github.valid8j.pcond.experimentals.currying.CurriedFunctions;
import com.github.valid8j.pcond.forms.Functions;
import com.github.valid8j.pcond.forms.Printables;
import com.github.valid8j.ut.propertybased.utils.PropertyBasedTestBase;
import com.github.valid8j.ut.propertybased.utils.TestCase;
import com.github.valid8j.ut.propertybased.utils.TestCaseParameter;
import com.github.valid8j.ut.propertybased.utils.TestCaseUtils;
import com.github.valid8j.ut.internal.exceptionhandling.IntentionalError;
import org.junit.ComparisonFailure;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.function.Function;
import java.util.stream.Stream;

import static com.github.valid8j.utils.experimentals.ExperimentalsUtils.stringEndsWith;
import static com.github.valid8j.pcond.experimentals.currying.CurriedFunctions.nest;
import static com.github.valid8j.pcond.forms.Functions.length;
import static com.github.valid8j.ut.propertybased.utils.TestCheck.*;
import static com.github.valid8j.pcond.forms.Predicates.*;
import static java.util.Arrays.asList;

@RunWith(Parameterized.class)
public class StreamNoneMatchPredicateTest extends PropertyBasedTestBase {

  public StreamNoneMatchPredicateTest(String testName, TestCase<?, ?> testCase) {
    super(testName, testCase);
  }

  @Parameters(name = "{index}: {0}")
  public static Iterable<Object[]> parameters() {
    return TestCaseUtils.parameters(StreamNoneMatchPredicateTest.class);
  }


  // givenStreamContainingNull_whenRequireConditionResultingInNPE_thenInternalExceptionWithCorrectMessageAndNpeAsNestedException
  @SuppressWarnings("PointlessArithmeticExpression")
  @TestCaseParameter
  public static TestCase<Stream<?>, ComparisonFailure> givenStreamPredicate$RequireConditionResultingInNPE$_whenUnexpectedValue_thenComparisonFailure2() {
    return new TestCase.Builder.ForThrownException<Stream<?>, ComparisonFailure>(
        Stream.of(null, "Hi", "hello", "world", null),
        transform(nest(asList("1", "2", "o")))
            .check(noneMatch(
                CurriedFunctions.toCurriedContextPredicate(transform(Functions.length()).check(gt(3))))))
        .expectedExceptionClass(ComparisonFailure.class)
        /* Test Case Specific Check*/
        .addCheck(numberOfActualSummariesIsEqualTo(
            1 /* nest */ +
                1 /* check:nonMatch */ +
                (1 /*curry*/ + 1 /* length */ + 1 /*check:isEqualTo*/) /* for each element */ * 1/* fourth element cuts the stream */))
        .addCheck(numberOfExpectSummariesWithDetailsIsEqualTo(1))
        .addCheck(expectDetailAtContainsToken(0, "length"))
        .addCheck(actualDetailAtContainsToken(0, NullPointerException.class.getCanonicalName()))
        .build();
  }

  @TestCaseParameter
  public static TestCase<Stream<?>, ComparisonFailure> givenStreamPredicate$hello_b_e$_whenUnexpectedValue_thenComparisonFailure2() {
    return new TestCase.Builder.ForThrownException<Stream<?>, ComparisonFailure>(
        Stream.of("Hi", "hello", "world", null),
        transform(nest(asList("1", "2", "o")))
            .check(noneMatch(
                CurriedFunctions.toCurriedContextPredicate(transform(length()).check(gt(3))))))
        //      |                         |         |                         |
        //      |                         |         |                         |
        //     (1)                       (2)       (3)                       (4),
        .expectedExceptionClass(ComparisonFailure.class)
        /* Test Case Specific Check*/
        .addCheck(numberOfActualSummariesIsEqualTo(
            1 /* nest */ +
                1 /* check:nonMatch */ +
                ((1 /*contextPredicate*/ + 1 /*length*/ + 1 /*check:gt(3)*/)/* for each element */
                    * 4/* fourth element cuts the stream */)))
        .addCheck(numberOfExpectSummariesWithDetailsIsEqualTo(1))
        .addCheck(expectDetailAtContainsToken(0, ">[3]"))
        .build();
  }

  @SuppressWarnings("PointlessArithmeticExpression")
  @TestCaseParameter
  public static TestCase<Stream<?>, ComparisonFailure> givenStreamPredicate$hello_b_e_2$_whenUnexpectedValue_thenComparisonFailure2() {
    return new TestCase.Builder.ForThrownException<Stream<?>, ComparisonFailure>(
        Stream.of("Hi", "hello", "world"),
        transform(nest(asList("1", "2", "o")))
            .check(noneMatch(CurriedFunctions.toCurriedContextPredicate(stringEndsWith(), 0, 1))))
        .expectedExceptionClass(ComparisonFailure.class)
        /* Test Case Specific Check*/
        .addCheck(numberOfActualSummariesIsEqualTo(
            1 /* nest */ +
                1 /* check:nonMatch */ +
                (1 /*contextPredicate*/ /* for each element */ * 6/* fourth element cuts the stream */)))
        .addCheck(numberOfExpectSummariesWithDetailsIsEqualTo(1))
        .addCheck(expectDetailAtContainsToken(0, "curry(stringEndsWith(String)(String)"))
        .addCheck(actualDetailAtContainsToken(0, "variables:[hello, o]"))
        .build();
  }


  @SuppressWarnings("PointlessArithmeticExpression")
  @TestCaseParameter
  public static TestCase<Stream<?>, ComparisonFailure> givenStreamPredicate$hello_b_e_4$_whenUnexpectedValue_thenComparisonFailure2() {
    return new TestCase.Builder.ForThrownException<Stream<?>, ComparisonFailure>(Stream.of(null, "Hi", "hello", "world", null),
        transform(nest(asList("1", "2", "o"))).check(noneMatch(
            CurriedFunctions.toCurriedContextPredicate(
                transform(
                    Printables.function("throwIntentionalError", (Function<String, Integer>) s -> {
                      throw new IntentionalError();
                    }))
                    .check(gt(3))))))
        .expectedExceptionClass(ComparisonFailure.class)
        /* Test Case Specific Check*/
        .addCheck(numberOfActualSummariesIsEqualTo(
            1 /* nest */ +
                1 /* check:nonMatch */ +
                ((1 /*contextPredicate*/ + 1 /*length*/ + 1 /*check:gt(3)*/)/* for each element */
                    * 1/* fourth element cuts the stream */)))
        .addCheck(numberOfExpectSummariesWithDetailsIsEqualTo(1))
        .addCheck(expectDetailAtContainsToken(0, "throwIntentionalError"))
        .addCheck(actualDetailAtContainsToken(0, "null"))
        .build();
  }
}
