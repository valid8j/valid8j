package com.github.dakusui.valid8j.examples;

import com.github.dakusui.valid8j.ValidationFluents;
import com.github.dakusui.valid8j.utils.metatest.TestClassExpectation;
import com.github.dakusui.valid8j.utils.metatest.TestClassExpectation.EnsureJUnitResult;
import com.github.dakusui.valid8j.utils.metatest.TestClassExpectation.ResultPredicateFactory.*;
import com.github.dakusui.valid8j.utils.metatest.TestMethodExpectation;
import org.junit.Test;

import static com.github.dakusui.valid8j.utils.metatest.TestMethodExpectation.Result.FAILURE;
import static com.github.dakusui.valid8j.utils.metatest.TestMethodExpectation.Result.PASSING;
import static com.github.dakusui.valid8j_pcond.fluent.Statement.objectValue;

@TestClassExpectation(value = {
    @EnsureJUnitResult(type = WasNotSuccessful.class, args = {}),
    @EnsureJUnitResult(type = RunCountIsEqualTo.class, args = "3"),
    @EnsureJUnitResult(type = IgnoreCountIsEqualTo.class, args = "0"),
    @EnsureJUnitResult(type = AssumptionFailureCountIsEqualTo.class, args = "0"),
    @EnsureJUnitResult(type = SizeOfFailuresIsEqualTo.class, args = "2")
})
public class Valid8JExample {
  @TestMethodExpectation(FAILURE)
  @Test
  public void assertAllSalutes() {
    assert ValidationFluents.all(
        objectValue(new Salute())
            .invoke("inJapanese")
            .asString()
            .length()
            .then()
            .greaterThan(0),
        objectValue(new Salute())
            .invoke("inEnglish")
            .asString()
            .length()
            .then()
            .greaterThan(0));
  }

  @TestMethodExpectation(PASSING)
  @Test
  public void assertSaluteInJapanese() {
    ValidationFluents.that(
        objectValue(new Salute())
            .invoke("inJapanese")
            .asString()
            .length()
            .then()
            .greaterThan(0));
  }

  @TestMethodExpectation(FAILURE)
  @Test
  public void assertSaluteInEnglish() {
    ValidationFluents.that(
        objectValue(new Salute())
            .invoke("inEnglish")
            .asString()
            .length()
            .then()
            .greaterThan(0));
  }

  static class Salute {
    public String inJapanese() {
      return "こんにちは";
    }

    public String inEnglish() {
      return "";
    }
  }
}
