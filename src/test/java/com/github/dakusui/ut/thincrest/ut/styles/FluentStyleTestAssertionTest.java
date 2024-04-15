package com.github.dakusui.ut.thincrest.ut.styles;

import com.github.dakusui.shared.utils.ut.TestBase;
import com.github.dakusui.thincrest.TestAssertions;
import com.github.dakusui.thincrest.TestFluents;
import com.github.dakusui.valid8j.pcond.fluent.ListHolder;
import com.github.dakusui.valid8j.pcond.fluent.Statement;
import com.github.dakusui.valid8j.pcond.forms.Functions;
import com.github.dakusui.valid8j.pcond.forms.Printables;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.AssumptionViolatedException;
import org.junit.ComparisonFailure;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.stream.Stream;

import static com.github.dakusui.valid8j.pcond.fluent.Statement.booleanValue;
import static com.github.dakusui.valid8j.pcond.fluent.Statement.stringValue;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

/**
 * Temporarily commented out for improving new fluent model.
 */
@RunWith(Enclosed.class)
public class FluentStyleTestAssertionTest {

  public static class ForTestAssertionsTest extends TestBase {
    @Test(expected = ComparisonFailure.class)
    public void expectingDifferentException_testFailing() {
      try {
        String givenValue = "helloWorld";
        TestFluents.assertStatement(stringValue(givenValue)
            .expectException(ArrayIndexOutOfBoundsException.class, Printables.function("substring100", (String s) -> s.substring(100)))
            .getMessage()
            .then()
            .equalTo("HELLOWORLD"));
      } catch (ComparisonFailure e) {
        e.printStackTrace();
        MatcherAssert.assertThat(e.getMessage(), CoreMatchers.allOf(
            CoreMatchers.containsString("StringIndexOutOfBounds"),
            CoreMatchers.containsString("isInstanceOf")));
        throw e;
      }
    }

    @SuppressWarnings("StringOperationCanBeSimplified")
    @Test(expected = ComparisonFailure.class)
    public void expectingExceptionButNotThrown_testFailing() {
      try {
        String givenValue = "helloWorld";
        TestFluents.assertStatement(stringValue(givenValue)
            .expectException(StringIndexOutOfBoundsException.class, Printables.function("substring0", (String s) -> s.substring(0)))
            .getMessage()
            .then()
            .equalTo("HELLOWORLD"));
      } catch (ComparisonFailure e) {
        e.printStackTrace();
        MatcherAssert.assertThat(e.getMessage(), CoreMatchers.allOf(
            CoreMatchers.containsString("exceptionThrown"),
            CoreMatchers.containsString("exceptionClass:StringIndexOutOfBoundsException")));
        throw e;
      }
    }

    @Test
    public void expectedExceptionThrown_testPassing() {
      String givenValue = "helloWorld";
      TestFluents.assertStatement(stringValue(givenValue)
          .expectException(StringIndexOutOfBoundsException.class, Printables.function("substring100", (String s) -> s.substring(100)))
          .getMessage()
          .then()
          .equalTo("String index out of range: -90"));
    }

    @Test(expected = ComparisonFailure.class)
    public void string_assertThatTest_failed() {
      String givenValue = "helloWorld";
      try {
        TestFluents.assertStatement(Statement.stringValue(givenValue)
            .toLowerCase()
            .then()
            .equalTo("HELLOWORLD"));
      } catch (ComparisonFailure e) {
        MatcherAssert.assertThat(e.getMessage(), CoreMatchers.containsString("Value:\"helloWorld\" violated: toLowerCase THEN:stringIsEqualTo[HELLOWORLD]"));
        throw e;
      }
    }

    @Test
    public void string_assertThatTest_passed() {
      String givenValue = "helloWorld";
      TestFluents.assertStatement(stringValue(givenValue)
          .toLowerCase()
          .then()
          .equalTo("helloworld"));
    }

    @Test
    public void string_assertThat_useValue_passed() {
      String givenValue = "helloWorld";
      TestFluents.assertStatement(stringValue(givenValue)
          .toLowerCase()
          .then()
          .equalTo("helloworld"));
    }

    @Test
    public void int_assertThatTest_passed() {
      int givenValue = 1234;
      TestFluents.assertStatement(Statement.integerValue(givenValue)
          .then()
          .equalTo(1234));
    }

    @Test
    public void boolean_assertThatTest_passed() {
      boolean givenValue = true;
      TestFluents.assertStatement(booleanValue(givenValue)
          .then()
          .equalTo(true));
    }

    @Test
    public void object_assertThatTest_passed() {
      Object givenValue = new Object() {
        @Override
        public String toString() {
          return "OBJECT";
        }
      };
      TestFluents.assertStatement(Statement.objectValue(givenValue)
          .toString(Object::toString)
          .then()
          .equalTo("OBJECT"));
    }

    @Test
    public void list_assertThatTest_passed() {
      List<String> givenValue = asList("hello", "world");
      TestFluents.assertStatement(Statement.listValue(givenValue)
          .then()
          .equalTo(asList("hello", "world")));
    }

    @Test
    public void stream_assertThatTest_passed() {
      Stream<String> givenValue = Stream.of("hello", "world");
      TestFluents.assertStatement(Statement.streamValue(givenValue)
          .toList(v -> v.collect(toList()))
          .then()
          .equalTo(asList("hello", "world")));
    }

    @Test(expected = ComparisonFailure.class)
    public void multiAssertAll_failed() {
      try {
        TestFluents.assertAll(
            stringValue("hello").toUpperCase().then().equalTo("HELLO"),
            stringValue("world").toLowerCase().then().containing("WORLD"));
      } catch (ComparisonFailure e) {
        e.printStackTrace();
        System.err.println("actual:" + e.getActual());
        MatcherAssert.assertThat(e.getActual().replaceAll(" +", ""), CoreMatchers.containsString("stringIsEqualTo[HELLO]->true"));
        MatcherAssert.assertThat(e.getActual().replaceAll(" +", ""), CoreMatchers.containsString("containsString[WORLD]->false"));
        System.err.println("expected:" + e.getExpected());
        MatcherAssert.assertThat(e.getExpected().replaceAll(" +", ""), CoreMatchers.containsString("stringIsEqualTo[HELLO]->true"));
        MatcherAssert.assertThat(e.getExpected().replaceAll(" +", ""), CoreMatchers.containsString("containsString[WORLD]->true"));
        throw e;
      }
    }

    @Test
    public void assertAll_passed() {
      TestFluents.assertAll(
          stringValue("hello").toUpperCase().then().equalTo("HELLO"),
          stringValue("world").toLowerCase().then().containing("world"));
    }

    @Test
    public void assertAllDifferentTypes_passed() {
      boolean b = TestFluents_assertAll(
          stringValue("hello").toUpperCase().then().equalTo("HELLO").$(),
          booleanValue(false).then().isFalse().$());
      MatcherAssert.assertThat(b, CoreMatchers.is(true));
    }

    /*
     * <T> boolean ValidationFluents.all(Statement<T>... statements) cannot use "?" instead of T.
     * Because there will be no way to suppress a compiler warning.
     */
    @SafeVarargs
    private static <T> boolean TestFluents_assertAll(Statement<T>... statements) {
      List<?> values = java.util.Arrays.stream(statements).map(Statement::statementValue).collect(toList());
      TestAssertions.assertThat(ListHolder.fromList(values), Statement.createPredicateForAllOf(statements));
      return true;
    }


    @Test(expected = AssumptionViolatedException.class)
    public void assumeThatTest_failed() {
      String givenValue = "helloWorld";
      try {
        TestFluents.assumeStatement(stringValue(givenValue)
            .toLowerCase()
            .then()
            .equalTo("HELLOWORLD"));
      } catch (AssumptionViolatedException e) {
        e.printStackTrace();
        MatcherAssert.assertThat(e.getMessage(), CoreMatchers.containsString("Value:\"helloWorld\" violated: toLowerCase THEN"));
        throw e;
      }
    }

    @Test
    public void assumeThatTest_passed() {
      String givenValue = "helloWorld";
      TestFluents.assumeStatement(stringValue(givenValue)
          .toLowerCase()
          .then()
          .equalTo("helloworld"));
    }

    @Test(expected = AssumptionViolatedException.class)
    public void multiAssumeAll_failed() {
      try {
        TestFluents.assumeAll(
            stringValue("hello").toUpperCase().then().equalTo("HELLO"),
            stringValue("world").toLowerCase().then().containing("WORLD"));
      } catch (AssumptionViolatedException e) {
        e.printStackTrace();
        MatcherAssert.assertThat(e.getMessage().replaceAll(" +", ""), CoreMatchers.containsString("stringIsEqualTo[HELLO]->true"));
        MatcherAssert.assertThat(e.getMessage().replaceAll(" +", ""), CoreMatchers.containsString("containsString[WORLD]->false"));
        throw e;
      }
    }

    @Test
    public void multiAssumeAll_passed() {
      TestFluents.assumeAll(
          stringValue("hello").toUpperCase().then().equalTo("HELLO"),
          stringValue("world").toLowerCase().then().containing("world"));
    }

    @Test
    public void test_valueMethod() {
      MatcherAssert.assertThat(Functions.value(), CoreMatchers.equalTo(null));
    }
  }
}
