package com.github.valid8j.examples.misc;

import com.github.valid8j.utils.metatest.TestClassExpectation;
import com.github.valid8j.utils.metatest.TestClassExpectation.EnsureJUnitResult;
import com.github.valid8j.utils.metatest.TestClassExpectation.ResultPredicateFactory.*;
import com.github.valid8j.utils.metatest.TestMethodExpectation;
import org.junit.Test;

import static com.github.valid8j.fluent.Expectations.*;
import static com.github.valid8j.utils.metatest.TestMethodExpectation.Result.FAILURE;

@TestClassExpectation(value = {
    @EnsureJUnitResult(type = WasNotSuccessful.class, args = {}),
    @EnsureJUnitResult(type = RunCountIsEqualTo.class, args = "1"),
    @EnsureJUnitResult(type = IgnoreCountIsEqualTo.class, args = "0"),
    @EnsureJUnitResult(type = AssumptionFailureCountIsEqualTo.class, args = "0"),
    @EnsureJUnitResult(type = SizeOfFailuresIsEqualTo.class, args = "1")
})
public class IntroductionExample {
  public String examplePublicPublicMethod(String name, int basePrice) {
    // Use `Expectations.requireXyz` method to check values in production.
    requireArguments(
        that(name).satisfies()
            .notNull(),
        that(basePrice).satisfies()
            .greaterThanOrEqualTo(0)
            .lessThan(10_000));
    return examplePrivateMethod(name, basePrice);
  }

  private String examplePrivateMethod(String name, int basePrice) {
    // Use `assert` statement with`Expectations.` {`precondition`,`invariant`,`postcondition`} methods
    // and their plural for Design by Contract programming.
    assert preconditions(
        // `value(var)` and `that(var)` are synonyms. Use the one you like.
        // `toBe(var)` and `satisfies(var)` are synonyms. Use the one you like.
        value(name).toBe()
            .notNull(),
        value(basePrice).toBe()
            .greaterThanOrEqualTo(0)
            .lessThan(10_000));
    int price = (int) (basePrice * 1.08);
    return String.format("%s:%s", name, price);
  }

  @TestMethodExpectation(FAILURE)
  @Test
  public void exampleMethod() {
    String message = examplePublicPublicMethod("Kirin Ichiban", 100);
    // Use `Expectations.assertAll` for test assertions.
    assertAll(
        that(message)
            .substringAfter(":")
            .parseInt()
            .satisfies()
            .equalTo(110),
        that(message)
            .satisfies()
            .startingWith("Kirin Ichiban"));
  }
}
