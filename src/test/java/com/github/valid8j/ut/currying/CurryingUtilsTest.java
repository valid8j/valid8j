package com.github.valid8j.ut.currying;

import com.github.valid8j.pcond.experimentals.currying.CurriedFunction;
import com.github.valid8j.pcond.internals.InternalUtils;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

public class CurryingUtilsTest {
  @Test
  public void testVoid() {
    assertEquals(Void.class, InternalUtils.wrapperClassOf(void.class));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNonPrimitive() {
    InternalUtils.wrapperClassOf(String.class);
  }

  @Test(expected = IllegalStateException.class)
  public void returnedValueIsInvalid() {
    try {
      new CurriedFunction<Object, Object>() {

        @Override
        public Object applyFunction(Object value) {
          return 123;
        }

        @Override
        public Class<?> parameterType() {
          return Object.class;
        }

        @Override
        public Class<?> returnType() {
          return String.class;
        }
      }.apply("hello");
    } catch (IllegalStateException e) {
      assertThat(
          e.getMessage(),
          allOf(
              containsString("123"),
              containsString("java.lang.Integer"),
              containsString("java.lang.String")));
      throw e;
    }
  }
}
