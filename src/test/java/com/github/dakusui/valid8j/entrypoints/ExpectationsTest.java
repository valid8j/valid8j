package com.github.dakusui.valid8j.entrypoints;

import org.junit.Test;

import static com.github.dakusui.valid8j.fluent.Expectations.assertStatement;
import static com.github.dakusui.valid8j.fluent.Expectations.that;

public class ExpectationsTest {
  @Test
  public void test() {
      int a = 99;
      assertStatement((that(a).satisfies().greaterThanOrEqualTo(0).lessThan(100)));
  }
}
