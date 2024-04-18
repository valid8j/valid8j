package com.github.dakusui.valid8j_pcond.propertybased.tests;

import com.github.dakusui.valid8j.pcond.forms.Functions;
import com.github.dakusui.valid8j.pcond.forms.Predicates;
import com.github.dakusui.valid8j_pcond.propertybased.utils.PropertyBasedTestBase;
import com.github.dakusui.valid8j_pcond.propertybased.utils.TestCase;
import com.github.dakusui.valid8j_pcond.propertybased.utils.TestCaseParameter;
import com.github.dakusui.valid8j_pcond.propertybased.utils.TestCaseUtils;
import org.junit.ComparisonFailure;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.github.dakusui.valid8j.utils.TestUtils.toLowerCase;
import static com.github.dakusui.valid8j.utils.TestUtils.toUpperCase;
import static com.github.dakusui.valid8j.pcond.forms.Predicates.allOf;
import static com.github.dakusui.valid8j.pcond.forms.Predicates.transform;
import static com.github.dakusui.valid8j_pcond.propertybased.utils.TestCheck.*;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

@RunWith(Parameterized.class)
public class TransformAndCheckPredicateTest extends PropertyBasedTestBase {

  public TransformAndCheckPredicateTest(String testName, TestCase<?, ?> testCase) {
    super(testName, testCase);
  }

  @Parameters(name = "{index}: {0}")
  public static Iterable<Object[]> parameters() {
    return TestCaseUtils.parameters(TransformAndCheckPredicateTest.class);
  }

  @TestCaseParameter
  public static TestCase<String, ComparisonFailure> givenChainedTransformingPredicate_whenNonExpectedValue_thenComparisonFailure() {
    return new TestCase.Builder.ForThrownException<String, ComparisonFailure>("HELLO")
        .predicate(transform(toLowerCase().andThen(Functions.length())).check(Predicates.isEqualTo(6)))
        .expectedExceptionClass(ComparisonFailure.class)
        .addCheck(numbersOfExpectAndActualSummariesAreEqual())
        .addCheck(numberOfActualSummariesIsEqualTo(4))
        .addCheck(numbersOfExpectAndActualSummariesWithDetailsAreEqual())
        .addCheck(numberOfExpectSummariesWithDetailsIsEqualTo(1))
        .build();
  }

  @TestCaseParameter
  public static TestCase<String, ComparisonFailure> givenDoubleChainedTransformingPredicate_whenNonExpectedValue_thenComparisonFailure() {
    return new TestCase.Builder.ForThrownException<String, ComparisonFailure>("HELLO")
        .predicate(transform(toUpperCase().andThen(toLowerCase()).andThen(Functions.length())).check(Predicates.isEqualTo(6)))
        .expectedExceptionClass(ComparisonFailure.class)
        .addCheck(numbersOfExpectAndActualSummariesAreEqual())
        .addCheck(numberOfActualSummariesIsEqualTo(5))
        .addCheck(numbersOfExpectAndActualSummariesWithDetailsAreEqual())
        .addCheck(numberOfExpectSummariesWithDetailsIsEqualTo(1))
        .build();
  }

  @SuppressWarnings("unchecked")
  @TestCaseParameter
  public static TestCase<String, Throwable> givenTransformingPredicate_whenExpectedValue_thenValueReturned() {
    return new TestCase.Builder.ForReturnedValue<>("hello", (Predicate<String>) transform(Functions.length()).check(Predicates.isEqualTo(5)), String.class)
        .addExpectationPredicate(equalsPredicate("hello"))
        .build();
  }

  @SuppressWarnings("unchecked")
  @TestCaseParameter
  public static TestCase<String, ComparisonFailure> givenTransformingPredicate_whenNonExpectedValue_thenComparisonFailure() {
    return new TestCase.Builder.ForThrownException<String, ComparisonFailure>("hello")
        .predicate((Predicate<String>) transform(Functions.length()).check(Predicates.isEqualTo(6)))
        .expectedExceptionClass(ComparisonFailure.class)
        .addCheck(numbersOfExpectAndActualSummariesAreEqual())
        .addCheck(numberOfActualSummariesIsEqualTo(2))
        .addCheck(numbersOfExpectAndActualSummariesWithDetailsAreEqual())
        .addCheck(numberOfExpectSummariesWithDetailsIsEqualTo(1))
        .build();
  }

  @TestCaseParameter
  public static TestCase<String, ComparisonFailure> givenTwoChainedTransformingPredicates_whenNonExpectedValue_thenComparisonFailure() {
    Function<String, String> _toUpperCase = toUpperCase();
    Function<? super String, Integer> _length_1 = Functions.length();
    Predicate<Integer> _isEqualTo_1 = Predicates.isEqualTo(6);
    Function<String, String> _toLowerCase = toLowerCase();
    Function<? super String, Integer> _length_2 = Functions.length();
    Predicate<Integer> _isEqualTo_2 = Predicates.isEqualTo(5);
    Predicate<String> _allOf = allOf(
        transform(_toUpperCase.andThen(_length_1)).check(_isEqualTo_1),
        transform(_toLowerCase.andThen(_length_2)).check(_isEqualTo_2));
    @SuppressWarnings("UnnecessaryLocalVariable") Predicate<String> _root = _allOf;
    List<Predicate<Integer>> _failureCausingPredicates = singletonList(_isEqualTo_2);
    List<String> _formNamesInUse = formNamesList(
        _allOf,
        /*transform*/
        /**/_toUpperCase,
        /**/_length_1,
        /*check*/
        /**/_isEqualTo_1,
        /*transform*/
        /**/_toLowerCase,
        /**/_length_2,
        /*check*/
        /**/_isEqualTo_2);
    String inputValue = "HELLO";
    return new TestCase.Builder.ForThrownException<String, ComparisonFailure>(inputValue)
        .predicate(_root)
        /* UNIVERSAL */
        .expectedExceptionClass(ComparisonFailure.class)
        .configure(genericConfiguratorForComparisonFailure())
        /* DEPENDING ON FORM NAMES */
        .addCheck(numberOfActualSummariesIsGreaterThanOrEqualTo(_formNamesInUse.size()))
        .addCheck(numberOfExpectSummariesWithDetailsIsEqualTo(_failureCausingPredicates.size()))
        .addCheck(formNamesContainAllOf(_formNamesInUse, comparisonFailureToExpected()))
        .addCheck(formNamesContainAllOf(_formNamesInUse, comparisonFailureToActual()))
        /* DEPENDING ON SPECIFIC TEST CASE */
        .addCheck(numberOfActualSummariesIsEqualTo(9))
        .addCheck(inputValuesContainAllOf(asList(inputValue, Long.toString("HELLO".length()), "HELLO".toLowerCase()), comparisonFailureToExpected()))
        .addCheck(inputValuesContainAllOf(asList(inputValue, Long.toString("HELLO".length()), "HELLO".toLowerCase()), comparisonFailureToActual()))
        .build();
  }

  private static List<String> formNamesList(Object... forms) {
    return Arrays.stream(forms)
        .map(Object::toString)
        .map(f -> f.replaceAll("[^A-Za-z0-9_:].+", ""))
        .collect(Collectors.toList());
  }
}
