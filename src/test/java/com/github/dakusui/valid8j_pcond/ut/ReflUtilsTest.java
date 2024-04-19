package com.github.dakusui.valid8j_pcond.ut;

import com.github.dakusui.valid8j.utils.testbase.TestBase;
import com.github.dakusui.valid8j.pcond.core.refl.ReflUtils;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class ReflUtilsTest extends TestBase {
  @Test
  public void testCombiner() {
    BinaryOperator<List<Method>> combiner = ReflUtils.createCombinerForMethodList();

    List<Method> methods1 = Arrays.stream(TestClass1.class.getMethods()).filter(m -> m.getName().startsWith("test")).collect(Collectors.toList());
    List<Method> methods2 = Arrays.stream(TestClass2.class.getMethods()).filter(m -> m.getName().startsWith("test")).collect(Collectors.toList());

    List<Method> out = combiner.apply(methods1, methods2);

    System.out.println("methods1:" + methods1);
    System.out.println("methods2:" + methods2);
    System.out.println("out:" + out);

    assertEquals(
        new HashSet<Method>() {{
          addAll(methods1);
          addAll(methods2);
        }},
        new HashSet<>(out));

  }


  public static class TestClass1 {
    public void testHello() {
    }

    public void testWorld1() {
    }
  }

  public static class TestClass2 {
    public void testHello() {
    }

    public void testWorld2() {
    }
  }
}
