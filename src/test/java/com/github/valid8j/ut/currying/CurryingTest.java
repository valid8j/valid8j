package com.github.valid8j.ut.currying;

import com.github.valid8j.utils.testbase.TestBase;
import com.github.valid8j.pcond.experimentals.currying.CurriedFunction;
import com.github.valid8j.pcond.internals.InternalException;
import com.github.valid8j.ut.testdata.IntentionalException;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import java.util.NoSuchElementException;
import java.util.Objects;

import static com.github.valid8j.pcond.forms.Functions.curry;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

public class CurryingTest extends TestBase {
  @Test
  public void givenCurriedFunction$whenApplyExpectedTimes$thenExpectedResultReturned() {
    CurriedFunction<Object, Object> curried = Utils.example();
    curried = curried.applyNext(1);
    String actual = curried.applyLast(2);
    assertEquals("1+2=3", actual);
  }

  @Test
  public void givenCurriedFunction$whenToStringOnOngoing$thenExpectedResultReturned() {
    CurriedFunction<Object, Object> curried = Utils.example();
    curried = curried.applyNext(1);
    String actual = curried.toString();
    assertEquals("example(int:1)(int)", actual);
  }

  @Test(expected = NoSuchElementException.class)
  public void givenCurriedFunction$whenApplyNextMoreThanExpected$thenNoSuchElementIsThrown() {
    CurriedFunction<Object, Object> curried = Utils.example();
    curried = curried.applyNext(1);
    curried.applyNext(2);
  }

  @Test(expected = IllegalStateException.class)
  public void givenCurriedFunction$whenApplyLastBeforeLast$thenIllegalStateIsThrown() {
    CurriedFunction<Object, Object> curried = Utils.example();
    Object actual = curried.applyLast(1);
    System.out.println(actual);
  }

  @Test(expected = IllegalArgumentException.class)
  public void givenCurriedFunction$whenApplyWithInvalidArg$thenThrown() {
    CurriedFunction<Object, Object> curried = Utils.example();
    try {
      curried = curried.applyNext("InvalidArgString").applyLast("Detail:InvalidArgString");
      System.out.println(curried);
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
      assertThat(
          e.getMessage(),
          allOf(
              CoreMatchers.containsString("Given argument"),
              CoreMatchers.containsString("InvalidArgString"),
              CoreMatchers.containsString(String.class.getName()),
              CoreMatchers.containsString("int")
          ));
      throw e;
    }
  }

  @Test(expected = IntentionalException.class)
  public void givenExceptionThrowingFunction$whenApplythenThrown() throws Throwable {
    CurriedFunction<Object, Object> curried = Utils.exceptionThrowingMethod();
    try {
      String actual = curried.applyNext("Hello").applyLast("World");
      System.out.println(actual);
    } catch (InternalException e) {
      assertThat(
          e.getMessage(),
          allOf(
              CoreMatchers.containsString(TestMethodHolder.class.getName()),
              CoreMatchers.containsString("exceptionThrowingMethod(String,String)")
          ));
      throw e.getCause();
    }
  }

  @Test
  public void test3() {
    CurriedFunction<Object, Object> curried = Utils.example();
    System.out.println(Objects.toString(curried.applyNext((short) 2).applyLast(3)));
  }

  @Test
  public void givenStringToCurriedFuncWithIntParam$whenIsValidArg$thenFalse() {
    CurriedFunction<Object, Object> curried = Utils.example();
    assertFalse(curried.isValidArg("Hello"));
  }

  @Test
  public void given_intToCurriedFuncWithIntParam$whenIsValidArg$thenTrue() {
    CurriedFunction<Object, Object> curried = Utils.example();
    assertTrue(curried.isValidArg(1));
  }

  @Test
  public void given_shortToCurriedFuncWithIntParam$whenIsValidArg$thenTrue() {
    CurriedFunction<Object, Object> curried = Utils.example();
    assertTrue(curried.isValidArg((short) 1));
  }

  @Test
  public void given_byteToCurriedFuncWithIntParam$whenIsValidArg$thenTrue() {
    CurriedFunction<Object, Object> curried = Utils.example();
    assertTrue(curried.isValidArg((byte) 1));
  }


  @Test
  public void given_longToCurriedFuncWithIntParam$whenIsValidArg$thenFalse() {
    CurriedFunction<Object, Object> curried = Utils.example();
    assertFalse(curried.isValidArg(1L));
  }

  @SuppressWarnings("UnnecessaryBoxing")
  @Test
  public void given_IntegerToCurriedFuncWithIntParam$whenIsValidArg$thenTrue() {
    CurriedFunction<Object, Object> curried = Utils.example();
    assertTrue(curried.isValidArg(new Integer(0)));
  }

  @Test
  public void given_nullToCurriedFuncWithIntParam$whenIsValidArg$thenFalse() {
    CurriedFunction<Object, Object> curried = Utils.example();
    assertFalse(curried.isValidArg(null));
  }

  @Test
  public void given_nullToCurriedFuncWithStringParam$whenIsValidArg$thenTrue() {
    CurriedFunction<Object, Object> curried = Utils.exceptionThrowingMethod();
    assertTrue(curried.isValidArg(null));
  }

  @Test(expected = InternalException.class)
  public void test4_b() {
    try {
      curry(TestMethodHolder.class, "undefined", int.class, int.class);
    } catch (InternalException e) {
      e.printStackTrace();
      assertThat(
          e.getMessage(), allOf(
              CoreMatchers.containsString("undefined(int,int)"),
              CoreMatchers.containsString("was not found"),
              CoreMatchers.containsString(CurryingTest.class.getName())
          ));
      throw e;
    }
  }

  @Test(expected = InternalException.class)
  public void undefinedMethod() {
    try {
      curry(TestMethodHolder.class, "undefined", int.class, int.class);
    } catch (InternalException e) {
      e.printStackTrace();
      assertThat(
          e.getMessage(), allOf(
              CoreMatchers.containsString("undefined(int,int)"),
              CoreMatchers.containsString("was not found"),
              CoreMatchers.containsString(CurryingTest.class.getName())
          ));
      throw e;
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void nonStaticMethod() {
    try {
      curry(TestMethodHolder.class, "nonStatic", int.class, int.class);
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
      assertThat(
          e.getMessage(), allOf(
              CoreMatchers.containsString("nonStatic(int,int)"),
              CoreMatchers.containsString("is not static"),
              CoreMatchers.containsString(CurryingTest.class.getName())
          ));
      throw e;
    }
  }

  public static class Utils {
    public static CurriedFunction<Object, Object> example() {
      return curry(TestMethodHolder.class, "example", int.class, int.class);
    }

    public static CurriedFunction<Object, Object> exceptionThrowingMethod() {
      return curry(TestMethodHolder.class, "exceptionThrowingMethod", String.class, String.class);
    }
  }

  public static class TestMethodHolder {
    @SuppressWarnings("unused") // Called through reflection
    public static String example(int i, int j) {
      return String.format("%s+%s=%s", i, j, i + j);
    }

    @SuppressWarnings("unused") // Called through reflection
    public boolean nonStatic(int i, int j) {
      return false;
    }

    @SuppressWarnings("unused") // Called through reflection
    public static boolean exceptionThrowingMethod(String message, String detail) {
      throw new IntentionalException(message + ":" + detail);
    }
  }
}
