package com.github.valid8j.entrypoints;

import com.github.valid8j.classic.Assertions;
import com.github.valid8j.pcond.forms.Predicates;
import com.github.valid8j.pcond.validator.Validator;
import com.github.valid8j.utils.exceptions.ExpectedException;
import com.github.valid8j.utils.testbase.TestBase;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.util.Properties;

import static org.junit.Assert.assertEquals;

@RunWith(Enclosed.class)
public class AssertionsTest {

  private static void assertTrue(boolean value) {
    // If we use JUnit's assertTrue, it will confuse pitest because it throws AssertionError, which is also thrown by methods in Assertion class.
    if (value)
      return;
    throw new RuntimeException();
  }

  @Test
  public void givenValidValue_whenPreconditionExercised_thenTrueReturned() {
    assertTrue(Assertions.precondition(0, Predicates.isEqualTo(0)));
  }

  @Test(expected = ExpectedException.class)
  public void givenInvalidValue_whenPreconditionExercised_thenAborted() {
    try {
      assertTrue(Assertions.precondition(0, Predicates.isEqualTo(1)));
    } catch (AssertionError e) {
      throw new ExpectedException(e);
    }
  }

  @Test
  public void givenValidValue_whenPostconditionExercised_thenTrueReturned() {
    assertTrue(Assertions.postcondition(0, Predicates.isEqualTo(0)));
  }

  @Test(expected = ExpectedException.class)
  public void givenInvalidValue_whenPostconditionExercised_thenAborted() {
    try {
      assertTrue(Assertions.postcondition(0, Predicates.isEqualTo(1)));
    } catch (AssertionError e) {
      throw new ExpectedException(e);
    }
  }

  @Test
  public void givenValidValue_whenInvariantConditionExercised_thenTrueReturned() {
    assertTrue(Assertions.that(0, Predicates.isEqualTo(0)));
  }

  @Test(expected = ExpectedException.class)
  public void givenInvalidValue_whenInvariantConditionExercised_thenAborted() {
    try {
      assertTrue(Assertions.that(0, Predicates.isEqualTo(1)));
    } catch (AssertionError e) {
      throw new ExpectedException(e);
    }
  }

  public static class Passing {
    @Test
    public void testAssertThatValue$thenPass() {
      String var = "10";
      assert Assertions.that(var, Predicates.ge("10").and(Predicates.lt("20")));
    }

    /*
    @Test
    public void fluent$testAssertThat$thenPassing() {
      String var = "10";
      assert ValidationFluents.that(Fluents.stringStatement(var).thenWith(b -> b.statement(Predicates.ge("10").and(Predicates.lt("20")))));
    }

    @Test
    public void fluent$testAssertAll$thenPassing() {
      String var = "10";
      assert ValidationFluents.all(Fluents.stringStatement(var).thenWith(b -> b.statement(Predicates.ge("10").and(Predicates.lt("20")))));
    }

     */


    @Test
    public void testAssertPrecondition$thenPassing() {
      String var = "10";
      assert Assertions.precondition(var, Predicates.ge("10").and(Predicates.lt("20")));
    }

    /*
    @Test
    public void fluent$testAssertPrecondition$thenPassing() {
      String var = "10";
      assert ValidationFluents.precondition(Fluents.stringStatement(var).thenWith(b -> b.statement(Predicates.ge("10").and(Predicates.lt("20")))));
    }

    @Test
    public void fluent$testAssertPreconditions$thenPassing() {
      String var = "10";
      assert ValidationFluents.preconditions(Fluents.stringStatement(var).thenWith(b -> b.statement(Predicates.ge("10").and(Predicates.lt("20")))));
    }

     */

    @Test
    public void testAssertPostcondition$thenPassing() {
      String var = "10";
      assert Assertions.postcondition(var, Predicates.ge("10").and(Predicates.lt("20")));
    }

    /*
    @Test
    public void fluent$testAssertPostcondition$thenPassing() {
      String var = "10";
      assert ValidationFluents.postcondition(Fluents.stringStatement(var).thenWith(b -> b.statement(Predicates.ge("10").and(Predicates.lt("20")))));
    }

    @Test
    public void fluent$testAssertPostconditions$thenPassing() {
      String var = "10";
      assert ValidationFluents.postconditions(Fluents.stringStatement(var).thenWith(b -> b.statement(Predicates.ge("10").and(Predicates.lt("20")))));
    }

     */
  }

  public static class Failing extends TestBase.ForAssertionEnabledVM {
    @Test(expected = AssertionError.class)
    public void testAssertThat$thenFailing() {
      String var = "20";
      assert Assertions.that(var, Predicates.ge("10").and(Predicates.lt("20")));
    }

    @Test(expected = AssertionError.class)
    public void testAssertPrecondition$thenFailing() {
      String var = "20";
      assert Assertions.precondition(var, Predicates.ge("10").and(Predicates.lt("20")));
    }

    @Test(expected = AssertionError.class)
    public void testAssertPostcondition$thenFailing() {
      String var = "20";
      assert Assertions.postcondition(var, Predicates.ge("10").and(Predicates.lt("20")));
    }
  }

  public static class MessageTest {
    @Test
    public void composeMessage$thenComposed() {
      assertEquals("Value:\"hello\" violated: isNull", new Validator.Impl(Validator.configurationFromProperties(new Properties())).configuration().messageComposer().composeMessageForAssertion("hello", Predicates.isNull()));
    }
  }

}
