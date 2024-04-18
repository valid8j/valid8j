package com.github.dakusui.valid8j.entrypoints.n;

import com.github.dakusui.valid8j.classic.Ensures;
import com.github.dakusui.valid8j.pcond.forms.Predicates;
import com.github.dakusui.valid8j.pcond.validator.Validator;
import com.github.dakusui.valid8j.pcond.validator.exceptions.PostconditionViolationException;
import com.github.dakusui.valid8j.utils.testbase.TestBase;
import org.junit.Test;

import static com.github.dakusui.valid8j.utils.TestUtils.firstLineOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class EnsuresTest extends TestBase.ForAssertionEnabledVM {
  @Test(expected = NullPointerException.class)
  public void testEnsureNonNull() {
    try {
      Object ret = Ensures.ensureNonNull(null);
      System.out.println("<" + ret + ">");
    } catch (NullPointerException e) {
      assertEquals("value:<null> violated postcondition:value isNotNull", firstLineOf(e.getMessage()));
      throw e;
    }
  }

  @Test
  public void test() {
    System.out.println(Validator.instance());
  }

  @Test
  public void givenNonNull$whenEnsureNonNull$thenPasses() {
    Object ret = Ensures.ensureNonNull("hello");
    System.out.println("<" + ret + ">");
    assertNotNull(ret);
  }

  @Test(expected = IllegalStateException.class)
  public void testEnsureState() {
    try {
      Object ret = Ensures.ensureState(null, Predicates.isNotNull());
      System.out.println("<" + ret + ">");
    } catch (NullPointerException e) {
      assertEquals("value:null violated postcondition:value isNotNull", e.getMessage());
      throw e;
    }
  }

  @Test
  public void givenValidState$whenEnsureState$thenPasses() {
    Object ret = Ensures.ensureState("hello", Predicates.isNotNull());
    System.out.println("<" + ret + ">");
    assertNotNull(ret);
  }

  @Test(expected = PostconditionViolationException.class)
  public void testEnsure() {
    try {
      Object ret = Ensures.ensure(null, Predicates.isNotNull());
      System.out.println("<" + ret + ">");
    } catch (Error e) {
      assertEquals("Hello:null:isNotNull", firstLineOf(e.getMessage()));
      throw e;
    }
  }

  @Test
  public void givenValidValue$whenEnsure$thenPasses() {
    Object ret = Ensures.ensure(
        "hello",
        Predicates.isNotNull());
    System.out.println("<" + ret + ">");
    assertNotNull(ret);
  }

}
