package com.github.dakusui.thincrest.examples;

import com.github.dakusui.thincrest.utils.metatest.TestClassExpectation;
import com.github.dakusui.thincrest.utils.metatest.TestClassExpectation.EnsureJUnitResult;
import com.github.dakusui.thincrest.utils.metatest.TestClassExpectation.ResultPredicateFactory.*;
import com.github.dakusui.thincrest.utils.metatest.TestMethodExpectation;
import org.junit.Test;

import static com.github.dakusui.thincrest.TestFluents.*;
import static com.github.dakusui.thincrest.utils.metatest.TestMethodExpectation.Result.*;
import static com.github.dakusui.valid8j_pcond.fluent.Statement.objectValue;

@TestClassExpectation(value = {
    @EnsureJUnitResult(type = WasNotSuccessful.class, args = {}),
    @EnsureJUnitResult(type = RunCountIsEqualTo.class, args = "6"),
    @EnsureJUnitResult(type = IgnoreCountIsEqualTo.class, args = "0"),
    @EnsureJUnitResult(type = AssumptionFailureCountIsEqualTo.class, args = "2"),
    @EnsureJUnitResult(type = SizeOfFailuresIsEqualTo.class, args = "2")
})
public class ThincrestExample {
  @TestMethodExpectation(FAILURE)
  @Test
  public void assertAllSalutes() {
    assertAll(
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
    assertStatement(
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
    assertStatement(
        objectValue(new Salute())
            .invoke("inEnglish")
            .asString()
            .length()
            .then()
            .greaterThan(0));
  }

  @TestMethodExpectation(ASSUMPTION_FAILURE)
  @Test
  public void assumeAllSalutes() {
    assumeAll(
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
  public void assumeSaluteInJapanese() {
    assumeAll(
        objectValue(new Salute())
            .invoke("inJapanese")
            .asString()
            .length()
            .then()
            .greaterThan(0));
  }
  @TestMethodExpectation(ASSUMPTION_FAILURE)
  @Test
  public void assumeSaluteInEnglish() {
    assumeAll(
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
