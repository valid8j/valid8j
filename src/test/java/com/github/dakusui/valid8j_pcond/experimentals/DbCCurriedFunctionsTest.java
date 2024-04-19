package com.github.dakusui.valid8j_pcond.experimentals;

import com.github.dakusui.valid8j.utils.experimentals.ExperimentalsUtils;
import com.github.dakusui.valid8j.utils.exceptions.IllegalValueException;
import com.github.dakusui.valid8j.utils.experimentals.TargetMethodHolder;
import com.github.dakusui.valid8j.pcond.experimentals.currying.CurriedFunction;
import com.github.dakusui.valid8j.pcond.experimentals.currying.CurriedFunctions;
import com.github.dakusui.valid8j.pcond.experimentals.currying.context.CurriedContext;
import com.github.dakusui.valid8j.pcond.forms.Functions;
import com.github.dakusui.valid8j.utils.testbase.TestBase;
import com.github.dakusui.valid8j_pcond.ut.IntentionalException;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import java.util.function.Function;
import java.util.function.Predicate;

import static com.github.dakusui.valid8j.utils.experimentals.ExperimentalsUtils.areEqual;
import static com.github.dakusui.valid8j.utils.experimentals.ExperimentalsUtils.stringEndsWith;
import static com.github.dakusui.valid8j.utils.Validates.validate;
import static com.github.dakusui.valid8j.utils.TestUtils.lineAt;
import static com.github.dakusui.valid8j.pcond.experimentals.currying.CurriedFunctions.*;
import static com.github.dakusui.valid8j.pcond.forms.Functions.stream;
import static com.github.dakusui.valid8j.pcond.forms.Functions.streamOf;
import static com.github.dakusui.valid8j.pcond.forms.Predicates.*;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;

public class DbCCurriedFunctionsTest extends TestBase {
  /**
   * Building a nested loop with the {@code pcond} library.
   *
   * You can build a check using a multi-parameter static method which returns a boolean value.
   * In this example, {@link TargetMethodHolder#stringEndsWith(String, String)} is the method.
   * It is turned into a curried function in {@link ExperimentalsUtils#stringEndsWith()} and then passed to {@link CurriedFunctions#toCurriedContextPredicate(CurriedFunction, int...)}.
   * The method {@code Experimentals#test(CurriedFunction, int...)} converts a curried function whose final returned value is a boolean into a predicate of a {@link CurriedContext}.
   * A {@code Context} may have one or more values at once and those values are indexed.
   */
  @Test
  public void hello() {
    validate(
        asList("hello", "world"),
        transform(stream().andThen(nest(asList("1", "2", "o"))))
            .check(anyMatch(toCurriedContextPredicate(stringEndsWith()))));
  }

  @Test
  public void hello_a() {
    validate(
        asList("hello", "world"),
        transform(stream().andThen(nest(asList("1", "2", "o")))).check(anyMatch(toCurriedContextPredicate(stringEndsWith(), 0, 1))));
  }

  @Test
  public void hello_b() {
    validate(
        asList("hello", "world"),
        transform(stream().andThen(nest(asList("1", "2", "o")))).check(noneMatch(toCurriedContextPredicate(stringEndsWith(), 1, 0))));
  }

  @Test
  public void hello_b2() {
    validate(
        asList("hello", "world"),
        transform(Functions.stream(String.class)).check(anyMatch(matchesRegex("he.*")))
    );
  }

  @Test
  public void hello_b3() {
    validate(
        asList("hello", "world"),
        transform(Functions.stream(String.class)).check(anyMatch(isNotNull())));
  }

  @Test
  public void givenStream_whenRequireConditionResultingInNPE_thenInternalExceptionWithCorrectMessageAndNpeAsNestedException() {
    validate(
        asList("Hi", "hello", "world"),
        transform((Functions.stream(String.class))).check(anyMatch(containsString("hello"))),
        IllegalValueException::new);
  }

  @Test(expected = IllegalValueException.class)
  public void hello_b_e5() {
    validate(
        asList(null, "Hi", "hello", "world", null),
        transform(stream().andThen(nest(asList("1", "2", "o")))).check(noneMatch(
            toCurriedContextPredicate(transform((Function<String, Integer>) s -> {
              throw new IntentionalException();
            }).check(gt(3))))));
  }

  @Test
  public void hello_c() {
    validate(
        asList("hello", "world"),
        transform(stream().andThen(toCurriedContextStream()).andThen(nest(asList("1", "2", "o")))).check(anyMatch(toCurriedContextPredicate(stringEndsWith(), 0, 1))));
  }

  @Test
  public void hello_d_1() {
    validate(
        "hello",
        transform(streamOf().andThen(nest(asList("Hello", "HELLO", "hello")))).check(anyMatch(toCurriedContextPredicate(areEqual()))));
  }

  @Test(expected = IllegalValueException.class)
  public void givenStreamOfSingleString$hello$_whenRequireNullIsFound_thenPreconditionViolationWithCorrectMessageIsThrown() {
    try {
      validate(
          "hello",
          transform(streamOf()                                        // (1)
              .andThen(toCurriedContextStream()))                            // (2)
              .check(anyMatch(toCurriedContextPredicate(isNull()))));        // (3)
    } catch (IllegalValueException e) {
      e.printStackTrace();
      int i = 0;
      // (1)
      assertThat(
          lineAt(e.getMessage(), ++i),
          CoreMatchers.allOf(
              CoreMatchers.containsString("hello"),
              CoreMatchers.containsString("transform")));
      // (1)
      assertThat(
          lineAt(e.getMessage(), ++i),
          CoreMatchers.containsString("streamOf"));
      // (2)
      assertThat(
          lineAt(e.getMessage(), ++i),
          CoreMatchers.containsString("toCurriedContextStream"));
      // expected (3)
      assertThat(
          lineAt(e.getMessage(), ++i),
          CoreMatchers.allOf(
              CoreMatchers.containsString("anyMatch"),
              CoreMatchers.containsString("curry"),
              CoreMatchers.containsString("isNull"),
              CoreMatchers.containsString("0"),
              CoreMatchers.containsString("true")));
      // expected (3)
      assertThat(
          lineAt(e.getMessage(), ++i),
          CoreMatchers.allOf(
              CoreMatchers.containsString("anyMatch"),
              CoreMatchers.containsString("curry"),
              CoreMatchers.containsString("isNull"),
              CoreMatchers.containsString("0"),
              CoreMatchers.containsString("false")));
      throw e;
    }
  }

  @Test
  public void givenStreamOfSingleString$hello$_whenRequireNonNullIsFound_thenPassing() {
    validate(
        "hello",
        transform(toCurriedContext()).check(toCurriedContextPredicate(isNotNull())));
  }

  @Test(expected = IllegalValueException.class)
  public void givenString$hello$_whenTransformToContextAndCheckContextValueIsNull_thenPreconditionViolationWithCorrectMessageThrown() {
    try {
      validate(
          "hello",
          transform(toCurriedContext())                              // (1)
              .check(toCurriedContextPredicate(isNull())));          // (2) -1,2
    } catch (IllegalValueException e) {
      e.printStackTrace(System.out);
      int i = 0;
      // (1)
      assertThat(
          lineAt(e.getMessage(), ++i),
          CoreMatchers.allOf(
              CoreMatchers.containsString("hello"),
              CoreMatchers.containsString("toCurriedContext"),
              CoreMatchers.containsString("variables:[hello]")
          ));
      // expected (2) -1
      assertThat(
          lineAt(e.getMessage(), ++i),
          CoreMatchers.allOf(
              CoreMatchers.containsString("variables:[hello]"),
              CoreMatchers.containsString("check")
          ));
      // expected (2) -2
      assertThat(
          lineAt(e.getMessage(), i),
          CoreMatchers.allOf(
              CoreMatchers.containsString("curry"),
              CoreMatchers.containsString("isNull"),
              CoreMatchers.containsString("0"),
              CoreMatchers.containsString("->"),
              CoreMatchers.containsString("true")
          ));
      // actual (2) -1
      assertThat(
          lineAt(e.getMessage(), ++i),
          CoreMatchers.allOf(
              CoreMatchers.containsString("variables:[hello]"),
              CoreMatchers.containsString("check")
          ));
      // actual (2) -2
      assertThat(
          lineAt(e.getMessage(), i),
          CoreMatchers.allOf(
              CoreMatchers.containsString("curry"),
              CoreMatchers.containsString("isNull"),
              CoreMatchers.containsString("0"),
              CoreMatchers.containsString("->"),
              CoreMatchers.containsString("false")
          ));
      throw e;
    }
  }

  @Test(expected = IllegalValueException.class)
  public void given$hello$_$world$_whenRequireNestedStreamImpossibleConditions_thenPreconditionViolationExceptionWithCorrectMessage() {
    try {
      validate(
          asList("hello", "world"),
          transform(stream()                                                        // (1)
              .andThen(nest(asList("1", "2", "o"))))                                // (2)
              .check(allMatch(toCurriedContextPredicate(stringEndsWith()))));              // (3)
    } catch (IllegalValueException e) {
      e.printStackTrace(System.out);
      int i = 0;
      // (1)
      assertThat(
          lineAt(e.getMessage(), ++i),
          CoreMatchers.allOf(
              CoreMatchers.containsString("\"hello\",\"world\""),
              CoreMatchers.containsString("transform")));
      // (1)
      assertThat(
          lineAt(e.getMessage(), ++i),
          CoreMatchers.containsString("stream"));
      // (2)
      assertThat(
          lineAt(e.getMessage(), ++i),
          CoreMatchers.allOf(
              CoreMatchers.containsString("nest"),
              CoreMatchers.containsString("\"1\",\"2\",\"o\"")));
      // expected (3)
      assertThat(
          lineAt(e.getMessage(), ++i),
          CoreMatchers.allOf(
              CoreMatchers.containsString("allMatch"),
              CoreMatchers.containsString("curry"),
              CoreMatchers.containsString("String"),
              CoreMatchers.containsString("true")));
      // actual (3)
      assertThat(
          lineAt(e.getMessage(), ++i),
          CoreMatchers.allOf(
              CoreMatchers.containsString("allMatch"),
              CoreMatchers.containsString("curry"),
              CoreMatchers.containsString("String"),
              CoreMatchers.containsString("false")));
      throw e;
    }
  }

  @Test
  public void hello2() {
    validate(
        asList("hello", "world"),
        transform(stream().andThen(nest(asList("1", "2")))).check(alwaysTrue()));
  }

  @Test
  public void hello3() {
    validate(
        asList("hello", "world"),
        transform(stream().andThen(nest(asList("1", "2")))).check(anyMatch(alwaysTrue())));
  }

  @Test
  public void hello3_a() {
    validate(
        asList("hello", "world"),
        transform(stream().andThen(nest(asList("1", "2"))).andThen(nest(asList("A", "B")))).check(alwaysTrue()));
  }

  @Test
  public void hello3_b() {
    validate(
        asList("hello", "world"),
        transform(stream().andThen(nest(asList("1", "2"))).andThen(nest(asList("A", "B")))).check(anyMatch(alwaysTrue())));
  }

  @Test
  public void hello4() {
    validate(
        asList("hello", "world"),
        transform(stream().andThen(nest(asList("1", "2"))).andThen(nest(asList("A", "B")))).check(anyMatch(new Predicate<CurriedContext>() {
          @Override
          public boolean test(CurriedContext curriedContext) {
            return curriedContext.valueAt(1).equals("1");
          }

          @Override
          public String toString() {
            return "context#valueAt(1) equals '1'";
          }
        })));
  }

  @Test
  public void hello4_a() {
    validate(
        asList("hello", "world"),
        transform(stream().andThen(nest(asList("1", "2")))).check(anyMatch(new Predicate<CurriedContext>() {
          @Override
          public boolean test(CurriedContext curriedContext) {
            return curriedContext.valueAt(1).equals("1");
          }

          @Override
          public String toString() {
            return "context#valueAt(1) equals '1'";
          }
        })));
  }

  @Test(expected = IllegalValueException.class)
  public void hello5() {
    validate(
        asList("hello", "world"),
        transform(stream().andThen(nest(asList("1", "2")))).check(allMatch(new Predicate<CurriedContext>() {
          @Override
          public boolean test(CurriedContext curriedContext) {
            return curriedContext.valueAt(1).equals("1");
          }

          @Override
          public String toString() {
            return "context#valueAt(1) equals '1'";
          }
        })));
  }

  @Test
  public void hello6() {
    validate(
        asList("hello", "world"),
        transform(stream().andThen(nest(asList("1", "2")))).check(anyMatch(new Predicate<CurriedContext>() {
          @Override
          public boolean test(CurriedContext curriedContext) {
            return curriedContext.valueAt(1).equals("1");
          }

          @Override
          public String toString() {
            return "context#valueAt(1) equals '1'";
          }
        })));
  }

  @Test
  public void nestedLoop_success() {
    validate(
        asList("hello", "world"),
        transform(stream().andThen(nest(asList("msg-1", "msg-2")))).check(anyMatch(toCurriedContextPredicate(equalTo("msg-2"), 1))));
  }

  @Test(expected = IllegalValueException.class)
  public void nestedLoop_fail() {
    validate(
        asList("hello", "world"),
        transform(stream().andThen(nest(asList("msg-1", "msg-2")))).check(anyMatch(toCurriedContextPredicate(equalTo("msg-3"), 1))));
  }
}
