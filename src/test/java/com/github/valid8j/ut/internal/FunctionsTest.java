package com.github.valid8j.ut.internal;

import com.github.valid8j.utils.testbase.TestBase;
import com.github.valid8j.pcond.core.printable.PrintableFunctionFactory;
import com.github.valid8j.pcond.experimentals.currying.multi.MultiFunction;
import com.github.valid8j.pcond.experimentals.currying.multi.MultiFunctionUtils;
import com.github.valid8j.pcond.forms.Functions;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static com.github.valid8j.pcond.forms.Functions.stream;
import static com.github.valid8j.pcond.forms.Functions.stringify;
import static com.github.valid8j.pcond.internals.InternalUtils.getMethod;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static junit.framework.TestCase.assertEquals;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

@RunWith(Enclosed.class)
public class FunctionsTest {
  public static class ElementAtTest extends TestBase {
    @Test
    public void whenApplied$thenLooksGood() {
      assertEquals(
          200,
          Functions.elementAt(1).apply(asList(100, 200, 300))
      );
    }

    @Test
    public void whenToString$thenLooksGood() {
      assertEquals(
          "at[123]",
          Functions.elementAt(123).toString());
    }

    @Test
    public void whenEqualityIsChecked$thenSameIsSameAndDifferentIsDifferent() {
      Function<List<Object>, Object> target = Functions.elementAt(100);
      assertThat(
          target,
          allOf(
              is(Functions.elementAt(100)),
              is(target),
              not(is(new Object())),
              not(is(Functions.elementAt(101)))));
    }

    @Test
    public void whenHashCode$thenSameIsSameAndDifferentIsDifferent() {
      int target = Functions.elementAt(100).hashCode();
      assertThat(
          target,
          allOf(
              is(Functions.elementAt(100).hashCode()),
              not(is(Functions.elementAt(101).hashCode()))));
    }
  }

  public static class SizeTest extends TestBase {
    @Test
    public void whenApplied$thenLooksGood() {
      assertEquals(
          (Integer) 3,
          Functions.size().apply(asList(100, 200, 300))
      );
    }

    @Test
    public void whenToString$thenLooksGood() {
      assertEquals(
          "size",
          Functions.size().toString()
      );
    }
  }

  public static class StreamTest extends TestBase {
    @Test
    public void whenApplied$thenLooksGood() {
      assertEquals(
          asList(100, 200, 300),
          stream(Integer.class).apply(asList(100, 200, 300)).collect(toList())
      );
    }

    @Test
    public void whenToString$thenLooksGood() {
      assertEquals(
          "stream",
          stream().toString()
      );
    }
  }

  public static class StringifyTest extends TestBase {
    @Test
    public void whenApplied$thenLooksGood() {
      assertEquals(
          "[]",
          stringify().apply(Collections.emptyList())
      );
    }

    @Test
    public void whenToString$thenLooksGood() {
      assertEquals(
          "stringify",
          stringify().toString()
      );
    }
  }

  public static class MultiFunctionTest extends TestBase {
    @Test(expected = IllegalArgumentException.class)
    public void lookUpWithInvalidArgument_duplicatedOrder() {
      try {
        greeting(0, 0);
      } catch (IllegalArgumentException e) {
        e.printStackTrace();
        assertThat(
            e.getMessage(),
            allOf(
                containsString("Duplicated elements are found"),
                containsString("[0, 0]")));
        throw e;
      }
    }

    @Test(expected = IllegalArgumentException.class)
    public void lookUpWithInvalidArgument_insufficientNumberOfParamOrder() {
      try {
        greeting(0);
      } catch (IllegalArgumentException e) {
        e.printStackTrace();
        assertThat(
            e.getMessage(),
            allOf(
                containsString("Inconsistent number of parameters are"),
                containsString("Expected:2, Actual: 1")));
        throw e;
      }
    }

    @Test
    public void runMultiParameterFunction$thenExpectedValueReturned() {
      MultiFunction<String> func = greeting(0, 1);
      String ret = func.apply(asList("Hello", "John"));
      System.out.println(ret);
      assertEquals("Hello, John", ret);
    }

    @Test
    public void toStringMultiParameterFunction$thenExpectedValueReturned() {
      MultiFunction<String> func = greeting(0, 1);
      assertEquals("com.github.valid8j.ut.internal.FunctionsTest$MultiFunctionTest$TargetMethodHolder.greeting(String,String)", func.toString());
    }

    @Test
    public void testMultiParameterFunction$withReversedOrder$thenExpectedValueReturned() {
      MultiFunction<String> func = greeting(1, 0);
      String ret = func.apply(asList("John", "Hello"));
      System.out.println(ret);
      assertEquals("Hello, John", ret);
    }

    @Test
    public void toStringMultiParameterFunction$withReversedOrder$thenExpectedValueReturned() {
      MultiFunction<String> func = greeting(1, 0);
      assertEquals("com.github.valid8j.ut.internal.FunctionsTest$MultiFunctionTest$TargetMethodHolder.greeting(String,String)(1,0)", func.toString());
    }

    @Test
    public void testHashCodeWithIdenticalObjects() {
      MultiFunction<String> func1 = greeting(0, 1);
      MultiFunction<String> func2 = greeting(0, 1);
      assertEquals(func1.hashCode(), func2.hashCode());
    }

    @Test
    public void testEqualsWithIdenticalObjects() {
      MultiFunction<String> func1 = greeting(0, 1);
      MultiFunction<String> func2 = greeting(0, 1);
      assertEquals(func1, func2);
      assertEquals(func1.hashCode(), func2.hashCode());
    }

    @Test
    public void testEqualsWithIdenticalObjectsCreatedSeparately() {
      MultiFunction<String> func1 = greeting(0, 1);
      MultiFunction<String> func2 = greeting2(0, 1);
      assertEquals(func1, func2);
      assertEquals(func1.hashCode(), func2.hashCode());
    }

    @Test
    public void testEqualsWithDifferentObjects() {
      MultiFunction<String> func1 = greeting(0, 1);
      MultiFunction<String> func2 = greeting(1, 0);
      assertNotEquals(func1, func2);
      assertNotEquals(func1.hashCode(), func2.hashCode());
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenWrongNumberArgumentsThenFails() {
      MultiFunction<String> func1 = greeting(0, 1);
      try {
        System.out.println(func1.apply(asList("Hello", "World", "!")));
      } catch (IllegalArgumentException e) {
        assertThat(e.getMessage(), allOf(startsWith("Wrong number of"), containsString("required: 2"), containsString("actual: 3")));
        throw e;
      }
    }

    @Test
    public void testEqualsWithDifferentTypeObjects() {
      MultiFunction<String> func1 = greeting(0, 1);
      assertNotEquals(func1, new Object());
    }

    @Test
    public void runVoidReturningMultiParameterFunction$thenNullReturned() {
      MultiFunction<String> func = voidMethod();
      String ret = func.apply(asList("Hello", "John"));
      System.out.println(ret);
      assertNull(ret);
    }

    private static MultiFunction<String> greeting(int... order) {
      return MultiFunctionUtils.multifunction(order, TargetMethodHolder.class, "greeting", String.class, String.class);
    }

    private static MultiFunction<String> greeting2(int... order) {
      Method m = getMethod(TargetMethodHolder.class, "greeting", String.class, String.class);
      List<Integer> paramOrder = Arrays.stream(order).boxed().collect(toList());
      return PrintableFunctionFactory.multifunction(m, paramOrder);
    }

    private static MultiFunction<String> voidMethod() {
      return Functions.multifunction(TargetMethodHolder.class, "voidMethod", String.class, String.class);
    }

    public static class TargetMethodHolder {
      @SuppressWarnings("unused") // Called through reflection.
      public static String greeting(String hello, String name) {
        return String.format("%s, %s", hello, name);
      }

      @SuppressWarnings("unused") // Called through reflection.
      public static void voidMethod(String hello, String name) {
        System.out.printf("%s, %s%n", hello, name);
      }
    }
  }
}
