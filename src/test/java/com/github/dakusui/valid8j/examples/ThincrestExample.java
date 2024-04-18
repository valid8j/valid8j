package com.github.dakusui.valid8j.examples;

import com.github.dakusui.valid8j.fluent.Expectations;
import com.github.dakusui.valid8j.pcond.core.fluent.CustomTransformer;
import com.github.dakusui.valid8j.pcond.core.fluent.builtins.StringTransformer;
import com.github.dakusui.valid8j.pcond.forms.Functions;
import com.github.dakusui.valid8j.pcond.forms.Predicates;
import com.github.dakusui.valid8j.pcond.forms.Printables;
import com.github.dakusui.valid8j.utils.metatest.TestClassExpectation;
import com.github.dakusui.valid8j.utils.metatest.TestClassExpectation.EnsureJUnitResult;
import com.github.dakusui.valid8j.utils.metatest.TestClassExpectation.ResultPredicateFactory.*;
import com.github.dakusui.valid8j.utils.metatest.TestMethodExpectation;
import org.junit.Test;

import java.util.Objects;

import static com.github.dakusui.thincrest.TestFluents.*;
import static com.github.dakusui.valid8j.classic.TestAssertions.assertThat;
import static com.github.dakusui.valid8j.fluent.Expectations.that;
import static com.github.dakusui.valid8j.pcond.fluent.Statement.objectValue;
import static com.github.dakusui.valid8j.pcond.forms.Predicates.*;
import static com.github.dakusui.valid8j.pcond.forms.Printables.function;
import static com.github.dakusui.valid8j.utils.metatest.TestMethodExpectation.Result.*;
import static java.util.Arrays.asList;

@TestClassExpectation(value = {
    @EnsureJUnitResult(type = WasNotSuccessful.class, args = {}),
    @EnsureJUnitResult(type = RunCountIsEqualTo.class, args = "10"),
    @EnsureJUnitResult(type = IgnoreCountIsEqualTo.class, args = "0"),
    @EnsureJUnitResult(type = AssumptionFailureCountIsEqualTo.class, args = "3"),
    @EnsureJUnitResult(type = SizeOfFailuresIsEqualTo.class, args = "5")
})
public class ThincrestExample {
  @TestMethodExpectation(FAILURE)
  @Test
  public void testString() {
    assertThat(
        "Howdy, World",
        transform(function("toUpperCase", o -> Objects.toString(o).toUpperCase()))
            .check(containsString("hello"))
    );
  }
  
  @TestMethodExpectation(FAILURE)
  @Test
  public void testStream() {
    assertThat(
        asList("Hello", "world"),
        transform(Functions.<String>stream()
            .andThen(stream -> stream.map(String::toLowerCase)))
            .check(Predicates.anyMatch(Predicates.endsWith("X"))));
  }
  
  @TestMethodExpectation(FAILURE)
  @Test
  public void assertAllSalutes() {
    assertAll(
        that(new Salute())
            .invoke("inJapanese")
            .asString()
            .length()
            .then()
            .greaterThan(0),
        that(new Salute())
            .invoke("inEnglish")
            .asString()
            .length()
            .then()
            .greaterThan(0));
  }
  
  @TestMethodExpectation(FAILURE)
  @Test
  public void assertAllSalutes_2() {
    assertAll(
        that(new Salute())
            .invoke("inJapanese")
            .asString()
            .length()
            .then()
            .greaterThan(0),
        that(new Salute())
            .invoke("inEnglish")
            .asString()
            .transform(v -> allOf(
                v.length()
                    .then()
                    .greaterThan(0).toPredicate(),
                v.then().containing("Hello").toPredicate()
                )));
  }
  
  @TestMethodExpectation(PASSING)
  @Test
  public void assertSaluteInJapanese() {
    assertStatement(
        that(new Salute())
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
        that(new Salute())
            .invoke("inEnglish")
            .asString()
            .length()
            .then()
            .greaterThan(0));
  }
  
  @TestMethodExpectation(ASSUMPTION_FAILURE)
  @Test
  public void assumeSalute() {
    assumeStatement(
        that(new Salute())
            .invoke("inJapanese")
            .asString()
            .length()
            .then()
            .greaterThan(100));
  }
  
  @TestMethodExpectation(ASSUMPTION_FAILURE)
  @Test
  public void assumeAllSalutes() {
    assumeAll(
        that(new Salute())
            .invoke("inJapanese")
            .asString()
            .length()
            .then()
            .greaterThan(0),
        that(new Salute())
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
        that(new Salute(), SaluteTransformer::new)
            .inJapanese()
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
  
  static class SaluteTransformer extends CustomTransformer<SaluteTransformer, Salute> {
    /**
     * Creates an instance of this class.
     *
     * @param baseValue The target value of this transformer.
     */
    public SaluteTransformer(Salute baseValue) {
      super(baseValue);
    }
    
    public StringTransformer<Salute> inJapanese() {
      return this.toString(Printables.function("inJapanese", Salute::inJapanese));
    }
    
    public StringTransformer<Salute> inEnglish() {
      return this.toString(Printables.function("inEnglish", Salute::inEnglish));
    }
  }
}
