package com.github.dakusui.ut.thincrest.ut;

import com.github.dakusui.shared.IllegalValueException;
import com.github.dakusui.shared.utils.ut.TestBase;
import com.github.dakusui.thincrest.TestAssertions;
import com.github.dakusui.valid8j.pcond.experimentals.cursor.Cursors;
import com.github.dakusui.valid8j.pcond.fluent.Statement;
import com.github.dakusui.valid8j.pcond.forms.Functions;
import com.github.dakusui.valid8j.pcond.forms.Printables;
import org.junit.ComparisonFailure;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Objects;
import java.util.function.Predicate;

import static com.github.dakusui.shared.TestUtils.validate;
import static com.github.dakusui.shared.TestUtils.validateStatement;
import static com.github.dakusui.thincrest.TestAssertions.assertThat;
import static com.github.dakusui.thincrest.TestFluents.assertAll;
import static com.github.dakusui.valid8j.pcond.core.refl.MethodQuery.instanceMethod;
import static com.github.dakusui.valid8j.pcond.forms.Functions.parameter;
import static com.github.dakusui.valid8j.pcond.forms.Predicates.*;

public class ReportDetailTest extends TestBase {
    public static <T> Statement<T> statement(T value, Predicate<T> predicate) {
      return Statement.objectValue(value).then().checkWithPredicate(predicate);
    }

    @Test
  public void givenLongString_whenCheckEqualnessWithSlightlyDifferentString_thenFailWithDetailsArePrinted$assertThat() {
    String actual = "helloHELLOhelloHELLOhelloXYZHELLOhelloHELLOhelloHELLO";
    String expected = "helloHELLOhelloHELLOhelloHELLOhelloHELLOhelloHELLO";
    try {
      TestAssertions.assertThat(actual, isEqualTo(expected));
      throw new Error(); // Make it fail if PC reaches here.
    } catch (ComparisonFailure e) {
      System.out.println("EXPECTED:<" + e.getExpected() + ">");
      System.out.println("ACTUAL:<" + e.getActual() + ">");
      assertThat(
          e,
          allOf(
              transform(Functions.<Throwable, String>call(instanceMethod(parameter(), "getActual"))).check(containsString(actual)),
              transform(Functions.<Throwable, String>call(instanceMethod(parameter(), "getExpected"))).check(containsString(expected))));
    }
  }

  @Ignore
  @Test
  public void givenLongString_whenCheckEqualnessWithSlightlyDifferentString_thenFailWithDetailsArePrinted$assertThat_forSandbox() {
    String actual = "helloHELLOhelloHELLOhelloXYZHELLOhelloHELLOhelloHELLO";
    String expected = "helloHELLOhelloHELLOhelloHELLOhelloHELLOhelloHELLO";

    TestAssertions.assertThat(actual, isEqualTo(expected));
  }

  @Ignore
  @Test
  public void givenLongString_whenCheckEqualnessWithSlightlyDifferentString_thenFailWithDetailsArePrinted$printed_forSaxbox() {
    String actual = "helloHELLOhelloHELLOhelloXYZHELLOhelloHELLOhelloHELLO";
    String expected = "helloHELLOhelloHELLOhelloHELLOhelloHELLOhelloHELLO";
    validate(actual, isEqualTo(expected));
  }

  @Test(expected = IllegalValueException.class)
  public void givenLongString_whenCheckEqualnessWithSlightlyDifferentString_thenFailWithDetailsArePrinted$assertThat_usingFluentStyle() {
    String actual = "helloHELLOhelloHELLOhelloXYZHELLOhelloHELLOhelloHELLO";
    String expected = "helloHELLOhelloHELLOhelloHELLOhelloHELLOhelloHELLO";
    try {
      validateStatement(statement(actual, isEqualTo(expected)));
      throw new Error(); // Make it fail if PC reaches here.
    } catch (IllegalValueException e) {
      System.err.println(e.getMessage());
      assertThat(
          e,
          allOf(
              transform(Functions.<Throwable, String>call(instanceMethod(parameter(), "getMessage"))).check(containsString(actual)),
              transform(Functions.<Throwable, String>call(instanceMethod(parameter(), "getMessage"))).check(containsString(expected))));
      throw e;
    }
  }

  @Test
  public void givenLongString_whenCheckEqualnessWithSlightlyDifferentString_thenFailWithDetailsArePrinted() {
    String actual = "helloHELLOhelloHELLOhelloXYZHELLOhelloHELLOhelloHELLO";
    String expected = "helloHELLOhelloHELLOhelloHELLOhelloHELLOhelloHELLO";
    try {
      TestAssertions.assertThat(actual, isEqualTo(expected));
      throw new Error(); // Make it fail if PC reaches here.
    } catch (ComparisonFailure e) {
      e.printStackTrace();
      assertAll(
          statement(
              e.getExpected(),
              allOf(
                  containsString(expected),
                  containsString("isEqualTo"))),
          statement(
              e.getActual(),
              containsString(actual)));
    }
  }


  @Test
  public void givenLongString_whenCheckEqualnessUsingCustomPredicateWithSlightlyDifferentString_thenFailWithDetailsArePrinted() {
    String actual = "helloHELLOhelloHELLOhelloXYZHELLOhelloHELLOhelloHELLO";
    String expected = "helloHELLOhelloHELLOhelloHELLOhelloHELLOhelloHELLO";
    try {
      assertThat(actual, Printables.predicate("customEquals", v -> Objects.equals(v, expected)));
      throw new Error(); // Make it fail if PC reaches here.
    } catch (ComparisonFailure e) {
      assertThat(
          e.getMessage(),
          allOf(
              containsString(actual),
              containsString("customEquals")));
    }
  }

  @Test
  public void givenLongString_whenCheckEqualnessUsingCustomPredicateWithSlightlyDifferentString_thenExpectationSpecificFragmentsContainedOnlyByExplanationForExpectationAndActualValuesAreAlsoSo() {
    String actualValue = "HelloWorld, everyone, -----------------------------------------,  hi, there!";
    String expected = "EXPECTED:" + actualValue + ":EXPECTED";
    try {
      assertThat(actualValue, allOf(
          isNotNull(),
          isNull(),
          containsString("XYZ"),
          equalTo(actualValue),
          equalTo(expected)
      ));
      throw new Error(); // Make it fail if program counter reaches here.
    } catch (ComparisonFailure e) {
      e.printStackTrace();
      assertAll(
          statement(
              e.getExpected(),
              allOf(
                  containsString("isNull"),
                  containsString("containsString[XYZ]"),
                  containsString(expected),
                  not(matchesRegex("\n" + actualValue + "\n"))
              )),
          statement(
              e.getActual(),
              allOf(
                  not(containsString(expected)),
                  containsString(actualValue))));
    }
  }

  @Test(expected = IllegalValueException.class)
  public void givenString_whenFails_then() {
    String expectedValue = "EXPECTED VALUE, Expected value, xyz,xyz,xyz,xyz,xyz,xyz,xyz,xyz,xyz,xyz,xyz, expected value.";
    String actualValue = "ACTUAL VALUE, actual value, xyz,xyz,xyz,xyz,xyz,xyz,xyz,xyz,xyz,xyz,xyz, actual value.";

    try {
      validate(actualValue, isEqualTo(expectedValue));
    } catch (IllegalValueException e) {
      System.err.println("================================================");
      e.printStackTrace();
      System.err.println("================================================");
      throw e;
    }
  }

  @Test(expected = ComparisonFailure.class)
  public void givenString_whenFails_then_2() {
    String expectedValue = "EXPECTED VALUE, Expected value, xyz,xyz,xyz,xyz,xyz,xyz,xyz,xyz,xyz,xyz,xyz, expected value.";
    String actualValue = "ACTUAL VALUE, actual value, xyz,xyz,xyz,xyz,xyz,xyz,xyz,xyz,xyz,xyz,xyz, actual value.";

    try {
      assertThat(actualValue,
          allOf(
              isEqualTo(expectedValue),
              Cursors.findSubstrings("VALUE", "Value")));
    } catch (IllegalValueException e) {
      System.err.println("================================================");
      e.printStackTrace();
      System.err.println("================================================");
      throw e;
    }
  }

}
