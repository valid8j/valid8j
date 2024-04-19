package com.github.dakusui.valid8j_pcond.ut;

import com.github.dakusui.valid8j.utils.testbase.TestBase;
import com.github.dakusui.valid8j.pcond.core.refl.MethodSelector;
import com.github.dakusui.valid8j.pcond.core.refl.ReflUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

public class MethodSelectorTest extends TestBase {
  @Test(expected = IllegalArgumentException.class)
  public void givenPreferNarrowerSelector$whenIncompatibleMethodsArePassed$thenIllegalArgument() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    try {
      Method[] methods = new Method[] {
          Methods.class.getMethod("method", String.class),
          Methods.class.getMethod("method", String.class, String.class)
      };

      List<Method> results = new MethodSelector.PreferNarrower()
          .select(asList(methods), new Object[] { "hello" });
      System.out.println(results);

      System.out.println(results.get(0).invoke(new Methods(), "hello"));
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
      assertThat(e.getMessage(), CoreMatchers.containsString("Parameter counts are different"));
      throw e;
    }
  }

  @Test
  public void givenPreferNarrowerSelector$whenIdenticalMethodsArePassed$thenBothAreReturned() throws
      NoSuchMethodException {
    Method[] methods = new Method[] {
        Methods.class.getMethod("method", String.class),
        Methods.class.getMethod("method", String.class)
    };

    List<Method> results = new MethodSelector.PreferNarrower()
        .select(asList(methods), new Object[] { "hello" });

    System.out.println(results);
    assertThat(results.size(), is(2));
    assertThat(results.get(0), CoreMatchers.is(methods[0]));
    assertThat(results.get(1), CoreMatchers.is(methods[1]));
  }

  @Test
  public void givenPreferNarrowerSelector$whenSelectWithNullArgument$thenReturned() throws
      NoSuchMethodException {
    Method[] methods = new Method[] {
        Methods.class.getMethod("method", String.class),
    };

    List<Method> results = new MethodSelector.Default()
        .select(asList(methods), new Object[] { null });

    assertThat(results.size(), is(1));
    assertThat(results.get(0), CoreMatchers.is(methods[0]));
  }

  @Test
  public void checkDescriptionOfDefaultMethodSelector() {
    assertThat(
        new MethodSelector.Default().describe(),
        CoreMatchers.containsString("default")
    );
  }

  @Test
  public void checkDescriptionOChainedMethodSelector() {
    assertThat(
        new MethodSelector.PreferExact().andThen(new MethodSelector.PreferNarrower()).describe(),
        CoreMatchers.containsString("preferExact&&preferNarrower")
    );
  }

  public static class Methods {
    public String method(String a) {
      return String.format("method(String: %s)", a);
    }

    public String method(String s, String t) {
      return String.format("method(String:%s,String:%s)", s, t);
    }
  }

  private static boolean privateMethod() {
    return true;
  }

  private Method findPrivateMethod() throws NoSuchMethodException {
    return this.getClass().getDeclaredMethod("privateMethod");
  }

  @Test
  public void testPrivateMethod() throws NoSuchMethodException {
    Method privateMethod = findPrivateMethod();
    System.out.println(privateMethod);

    boolean ret = ReflUtils.invokeMethod(privateMethod, null, new Object[0]);
    assertTrue(ret);
  }
}
