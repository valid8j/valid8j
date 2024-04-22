package com.github.valid8j.ut.internal;

import com.github.valid8j.classic.IllegalValueException;
import com.github.valid8j.utils.testbase.TestBase;
import com.github.valid8j.classic.TestAssertions;
import com.github.valid8j.pcond.forms.Functions;
import com.github.valid8j.pcond.forms.Predicates;
import com.github.valid8j.pcond.forms.Printables;
import org.hamcrest.CoreMatchers;
import org.junit.ComparisonFailure;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.util.Objects;
import java.util.function.Function;

import static com.github.valid8j.classic.Validates.validate;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

@RunWith(Enclosed.class)
public class PrintablesFunctionTest {
  public static class Simple {
    @Test
    public void test() {
      Function<?, ?> f = Functions.identity();
      Function<?, ?> ff = Functions.identity();
      Function<?, ?> g = Functions.stringify();
      Object n = null;
      Object o = new Object();
      assertThat(
          f,
          allOf(
              is(f),
              is(ff),
              not(is(g)),
              not(is(n)),
              not(is(o))));
    }

    @Test(expected = IllegalValueException.class)
    public void testHandleNull() {
      Function<String, String> f = Functions.identity();
      String o = null;
      validate(o, Predicates.transform(f).check(Predicates.isNotNull()));
    }

  }

  public static class Parameterized {
    @Test
    public void test() {
      Function<?, ?> f = Functions.elementAt(0);
      Function<?, ?> ff = Functions.elementAt(0);
      Function<?, ?> g = Functions.cast(String.class);
      Object n = null;
      Object o = new Object();
      assertThat(
          f,
          allOf(
              is(f),
              is(ff),
              not(is(g)),
              not(is(n)),
              not(is(o))));
    }
  }

  public static class Composed extends TestBase {
    @Test
    public void testCompose() {
      Function<?, ?> f1 = Functions.identity().compose(Functions.identity());
      Function<?, ?> f2 = Functions.identity().compose(Functions.identity());
      Function<?, ?> g = Functions.identity().compose(Functions.stringify());
      Function<?, ?> h = Functions.stringify().compose(Functions.length());
      Object o = new Object();

      assertThat(
          f1,
          CoreMatchers.allOf(
              is(f1),
              is(f2),
              not(is(g)),
              not(is(h)),
              not(is(o))
          ));
    }

    @Test
    public void testAndThen() {
      Function<?, ?> f1 = Functions.identity().andThen(Functions.identity());
      Function<?, ?> f2 = Functions.identity().andThen(Functions.identity());
      Function<?, ?> g = Functions.identity().andThen(Functions.stringify());
      Function<?, ?> h = Functions.stringify().andThen(Functions.length());
      Function<?, ?> i = Functions.identity().compose(Functions.identity());
      Object o = new Object();

      assertThat(
          f1,
          CoreMatchers.allOf(
              is(f1),
              is(f2),
              not(is(g)),
              not(is(h)),
              is(i),
              not(is(o))
          ));
    }

    @Test
    public void testAndThen$toString() {
      Function<Object, Object> f1 = Functions.identity().andThen(Function.identity());

      System.out.println(f1);
      System.out.println(f1.apply("hello"));
      assertThat(
          f1.toString(),
          startsWith("identity->java.util.function.Function"));
      assertEquals(f1.apply("hello"), "hello");
    }


    @Test(expected = ComparisonFailure.class)
    public void testFailureFromAndThenStructureInTransformer() {
      TestAssertions.assertThat("hello",
          Predicates.transform(
                  createToUpperCase(1)
                      .andThen(createToLowerCase(2))
                      .andThen(createToUpperCase(3))
                      .andThen(createToLowerCase(4))
                      .andThen(createToUpperCase(5)))
              .check(Predicates.equalTo("Nothing"))
      );
    }

    @Test(expected = ComparisonFailure.class)
    public void testFailureFromComposeStructureInTransformer() {
      TestAssertions.assertThat("hello",
          Predicates.transform(
                  createToUpperCase(1)
                      .compose(createToLowerCase(2))
                      .compose(createToUpperCase(3))
                      .compose(createToLowerCase(4))
                      .compose(createToUpperCase(5)))
              .check(Predicates.equalTo("Nothing"))
      );
    }

    private static Function<String, String> createToLowerCase(int i) {
      return Printables.function("toLowerCase", o -> Objects.toString(o).toLowerCase() + i);
    }

    private static Function<Object, String> createToUpperCase(int i) {
      return Printables.function("toUpperCase", o -> Objects.toString(o).toUpperCase() + i) ;
    }

    @Test
    public void testCompose$toString() {
      Function<Object, Object> f1 = Functions.identity().compose(Function.identity());

      System.out.println(f1);
      System.out.println(f1.apply("hello"));
      assertThat(
          f1.toString(),
          allOf(
              startsWith("java.util.function.Function"),
              endsWith("->identity")));
      assertEquals(f1.apply("hello"), "hello");
    }

    @Test
    public void testComposeParameterized() {
      Function<?, ?> f1 = Functions.identity().compose(Functions.elementAt(0));
      Function<?, ?> f2 = Functions.identity().compose(Functions.elementAt(0));
      Function<?, ?> g = Functions.identity().compose(Functions.cast(String.class));
      Function<?, ?> h = Functions.cast(String.class).compose(Functions.length());
      Object n = null;
      Object o = new Object();

      assertThat(
          f1,
          CoreMatchers.allOf(
              is(f1),
              is(f2),
              not(is(g)),
              not(is(h)),
              not(is(n)),
              not(is(o))
          ));
    }
  }
}