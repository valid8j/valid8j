package com.github.dakusui.ut.thincrest.ut;

import com.github.dakusui.shared.utils.ut.TestBase;
import org.junit.ComparisonFailure;
import org.junit.Test;

import java.util.stream.Stream;

import static com.github.dakusui.thincrest.TestAssertions.assertThat;
import static com.github.dakusui.valid8j.pcond.core.printable.ExplainablePredicate.explainableStringIsEqualTo;
import static com.github.dakusui.valid8j.pcond.forms.Predicates.*;

/**
 * Temporarily commented out for improving new fluent model.
 */
public class FluentUtilsTest extends TestBase {
  /*
  @Test
  public void whenPassingValidation_thenPasses$1() {
    assertThat(
        new Parent(),
        when().<Parent>as((Parent) value())
            .exercise(Parent::parentMethod1)
            .then()
            .<Parent>as(value())
            .verify(isEqualTo("returnValueFromParentMethod")).toPredicate());
  }

   */


  @Test(expected = ComparisonFailure.class)
  public void test4() {
    assertThat(
        "hello",
        not(equalsIgnoreCase("HELLO"))
    );
  }

  @Test(expected = ComparisonFailure.class)
  public void expectationFlipping() {
    assertThat(
        Stream.of("hello"),
        noneMatch(equalsIgnoreCase("HELLO"))
    );
  }

  /*
  @Test(expected = ComparisonFailure.class)
  public void example() {
    assertThat(
        asList("Hello", "world"),
        when().asListOf((String) value())
            .elementAt(0)
            .then().asString()
            .findSubstrings("hello", "world")
            .contains("hello"));
  }

   */

  @Test(expected = ComparisonFailure.class)
  public void example2() {
    assertThat(
        "stringHelloworlD!",
        explainableStringIsEqualTo("Hello")
    );
  }

  /*
  @Test(expected = ComparisonFailure.class)
  public void example3() {
    try {
      assertThat(
          new Parent(),
          allOf(
              whenValueOfClass(Parent.class).asObject()
                  .exercise(function("lambda:Parent::parentMethod1", Parent::parentMethod1))
                  .then()
                  .asString()
                  .isEqualTo("returnValueFromParentMethod"),
              valueOfClass(Parent.class).asObject()
                  .exercise(function("Parent::parentMethod2", Parent::parentMethod2))
                  .exercise(function("lambda:Child::childMethod", Child::childMethod))
                  .then()
                  .asString()
                  // 'not(...)' is added to make the matcher fail.
                  .addPredicate(not(isEqualTo("returnedStringFromChildMethod")))));
    } catch (ComparisonFailure e) {
      e.printStackTrace();
      throw e;
    }
  }

   */

  static class Parent {
    public String parentMethod1() {
      return "returnValueFromParentMethod";
    }

    public Child parentMethod2() {
      return new Child();
    }
  }

  static class Child {
    public String childMethod() {
      return "returnedStringFromChildMethod";
    }
  }
}
