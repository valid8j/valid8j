package com.github.dakusui.valid8j.examples.misc;

import com.github.dakusui.valid8j.pcond.fluent.Statement;
import org.junit.Test;

import java.util.function.Predicate;

import static com.github.dakusui.valid8j.fluent.Expectations.assertStatement;
import static com.github.dakusui.valid8j.pcond.forms.Predicates.*;

@SuppressWarnings("NewClassNamingConvention")
public class Sandbox {
  @Test
  public void test() {
    System.getProperties().forEach((k, v) -> System.out.println(k + "=<" + v + ">"));
  }

  @Test
  public void hello() {
    Predicate<Integer> p = i -> 0 <= i && i < 100;
    System.out.println(p);
  }

  @Test
  public void hello2() {
    Predicate<Integer> p = and(ge(0), lt(100));
    System.out.println(p);
  }

  @Test
  public void hello3() {
    assertStatement(Statement.stringValue("hello").substring(2).then().equalTo("world"));
  }
}
