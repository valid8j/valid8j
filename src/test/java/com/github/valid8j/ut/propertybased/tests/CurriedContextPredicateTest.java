package com.github.valid8j.ut.propertybased.tests;

import com.github.valid8j.pcond.experimentals.currying.CurriedFunctions;
import com.github.valid8j.ut.propertybased.utils.PropertyBasedTestBase;
import com.github.valid8j.ut.propertybased.utils.TestCase;
import com.github.valid8j.ut.propertybased.utils.TestCaseParameter;
import com.github.valid8j.ut.propertybased.utils.TestCaseUtils;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import static com.github.valid8j.pcond.experimentals.currying.CurriedFunctions.toCurriedContextStream;
import static com.github.valid8j.pcond.forms.Functions.streamOf;
import static com.github.valid8j.ut.propertybased.utils.TestCheck.equalsPredicate;
import static com.github.valid8j.pcond.forms.Predicates.*;

@RunWith(Parameterized.class)
public class CurriedContextPredicateTest extends PropertyBasedTestBase {

  public CurriedContextPredicateTest(String testName, TestCase<?, ?> testCase) {
    super(testName, testCase);
  }

  @Parameters(name = "{index}: {0}")
  public static Iterable<Object[]> parameters() {
    return TestCaseUtils.parameters(CurriedContextPredicateTest.class);
  }

  @TestCaseParameter
  public static TestCase<Object, Throwable> givenVariableBundlePredicate_whenExpectedValue_thenValueReturned() {
    Object v;
    return new TestCase.Builder.ForReturnedValue<>(
        v = "hello",
        transform(streamOf()                                        // (1)
            .andThen(toCurriedContextStream()))                     // (2)
            .check(anyMatch(CurriedFunctions.toCurriedContextPredicate(isNotNull()))),
        Object.class)
        .addExpectationPredicate(equalsPredicate(v))
        .build();
  }
}
