package com.github.valid8j.ut.internal;

import com.github.valid8j.pcond.forms.Printables;
import com.github.valid8j.utils.testbase.TestBase;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.util.function.Function;
import java.util.function.Predicate;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(Enclosed.class)
public class PrintablesTest {
  public static class FunctionTest extends TestBase {
    Function<String, String> appendA = Printables.function("append[A]", s -> s + "A");
    Function<String, String> appendB = Printables.function("append[B]", s -> s + "B");

    @Test
    public void givenFunctionReturnedByCompose$whenApply$thenWorksRight() {
      Function<String, String> composed = appendA.compose(appendB);

      assertEquals("HELLO:BA", composed.apply("HELLO:"));
    }

    @Test
    public void givenFunctionReturnedByCompose$whenToString$thenLooksGood() {
      Function<String, String> composed = appendA.compose(appendB);

      assertEquals("append[B]->append[A]", composed.toString());
    }

    @Test
    public void givenFunctionReturnedByAndThen$whenApply$thenWorksRight() {
      Function<String, String> andThen = appendA.andThen(appendB);

      assertEquals("HELLO:AB", andThen.apply("HELLO:"));
    }

    @Test
    public void givenFunctionReturnedByAndThen$whenToString$thenLooksGood() {
      Function<String, String> andThen = appendA.andThen(appendB);

      assertEquals("append[A]->append[B]", andThen.toString());
    }
  }

  public static class PredicateTest extends TestBase {
    Predicate<String> isA = Printables.predicate("is[A]", "A"::equals);
    Predicate<String> isB = Printables.predicate("is[B]", "B"::equals);

    @Test
    public void givenPredicateReturnedByAnd$whenTest$thenWorksRight() {
      assertFalse(isA.and(isB).test("A"));
      assertFalse(isA.and(isB).test("B"));
      assertTrue(isA.and(isB.negate()).test("A"));
    }

    @Test
    public void givenPredicateReturnedByAnd$whenToString$thenLooksGood() {
      assertEquals("(is[A]&&is[B])", isA.and(isB).toString());
    }

    @Test
    public void givenPredicateReturnedByOr$whenTest$thenWorksRight() {
      assertTrue(isA.or(isB).test("B"));
      assertTrue(isA.or(isB).test("A"));
      assertFalse(isA.or(isB).test("C"));
    }

    @Test
    public void givenPredicateReturnedByOr$whenToString$thenWorksRight() {
      assertEquals("(is[A]||is[B])", isA.or(isB).toString());
    }

    @Test
    public void givenPredicateReturnedByNegate$whenTest$thenWorksRight() {
      assertFalse(isA.negate().test("A"));
      assertTrue(isA.negate().test("B"));
    }

    @Test
    public void givenPredicateReturnedByNegate$whenToString$thenWorksRight() {
      assertEquals("!is[A]", isA.negate().toString());
    }
  }
}
