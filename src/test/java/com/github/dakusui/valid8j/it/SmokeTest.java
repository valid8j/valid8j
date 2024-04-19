package com.github.dakusui.valid8j.it;

import com.github.dakusui.valid8j.examples.fluent.Fluent4Example;
import com.github.dakusui.valid8j.utils.reporting.ReportParser;
import com.github.dakusui.valid8j.utils.TestUtils;
import com.github.dakusui.valid8j.pcond.core.fluent.builtins.IntegerChecker;
import com.github.dakusui.valid8j.pcond.core.fluent.builtins.StringTransformer;
import com.github.dakusui.valid8j.utils.testbase.TestBase;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.AssumptionViolatedException;
import org.junit.ComparisonFailure;
import org.junit.Ignore;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.github.dakusui.valid8j.fluent.Expectations.assertAll;
import static com.github.dakusui.valid8j.fluent.Expectations.assumeAll;
import static com.github.dakusui.valid8j.pcond.fluent.Statement.stringValue;
import static com.github.dakusui.valid8j.pcond.forms.Predicates.isNull;
import static java.util.Arrays.asList;

public class SmokeTest extends TestBase {
  @Test(expected = ExpectedException.class)
  public void givenBook_whenCheckTitleAndAbstract_thenTheyAreNotNullAndAppropriateLength_2() throws Throwable {
    smoke();
  }

  /*
   * ----
   * WHEN:transform:length
   * THEN:>[1]
   * WHEN:allOf
   * transform:title
   * THEN:allOf
   *   isNotNull
   *   transform:parseInt
   *   THEN:allOf
   *     >=[10]
   *     <[40]
   * transform:abstractText
   * THEN:allOf
   *   not:isNull
   *   transform:length
   *   THEN:allOf
   *     >=[200]
   *     <[400]
   * ----
   * ----
   * "hello"
   * 5
   * Book:[title:<De Bello G...i appellantur.>]
   * "De Bello Gallico"
   * NumberFormatException:"For input s...ico""
   * Book:[title:<De Bello G...i appellantur.>]
   * "Gallia est omnis divis...li appellantur."
   * 145
   * ----
   */
  @SuppressWarnings("CallToPrintStackTrace")
  @Ignore
  @Test
  public void smoke() {
    String title = "De Bello Gallico";
    String abstractText = "Gallia est omnis divisa in partes tres, quarum unam incolunt Belgae, aliam Aquitani, tertiam qui ipsorum lingua Celtae, nostra Galli appellantur.";
    List<String> expectedInputList = asList(
        "'hello'",
        "5",
        "Book:[title:<De Bello G...i appellantur.>]",
        "'De Bello Gallico'",
        "NumberFormatException:'For input s...ico''",
        "Book:[title:<De Bello G...i appellantur.>]",
        "'Gallia est omnis divis...li appellantur.'",
        "145");
    List<String> extractedOpNameList = asList(
        "WHEN:transform",
        "length",
        "THEN:>[1]",
        "WHEN:allOf",
        "transform:title",
        "THEN:allOf",
        "isNotNull",
        "transform:parseInt",
        "THEN:allOf",
        ">=[10]",
        "<[40]",
        "transform:abstractText",
        "THEN:allOf",
        "not:isNull",
        "transform:length",
        "THEN:allOf",
        ">=[200]",
        "<[400]"
    );
    Fluent4Example.OnGoing.Book book = new Fluent4Example.OnGoing.Book(title, abstractText);
    try {
      assertSmoke(book);
    } catch (ComparisonFailure e) {
      e.printStackTrace();
      ReportParser reportParserForActualValue = new ReportParser(e.getActual());
      ReportParser reportParserForExpectation = new ReportParser(e.getExpected());
      assertAllRunnables(
          () -> assertAllRunnables(
              () -> {
                System.out.println("summary=" + reportParserForActualValue.summary());
                reportParserForActualValue.details().forEach(
                    each -> {
                      System.out.println(each.subject());
                      System.out.println(each.body());
                    }
                );
              },
              () -> MatcherAssert.assertThat(
                  countDetailIndicesInSummary(reportParserForExpectation),
                  CoreMatchers.equalTo(2L))
          ),
          () -> assertAllRunnables(
              () -> {
                MatcherAssert.assertThat(detailSubjectFor(reportParserForActualValue, 0), CoreMatchers.containsString("[0]"));
                MatcherAssert.assertThat(detailBodyFor(reportParserForActualValue, 0), CoreMatchers.allOf(
                    CoreMatchers.containsString("Input: 'De Bello Gallico'"),
                    CoreMatchers.containsString("Input Type: java.lang.String"),
                    CoreMatchers.containsString("Thrown Exception: 'java.lang.NumberFormatException'")
                ));
              },
              () -> {
                MatcherAssert.assertThat(detailSubjectFor(reportParserForActualValue, 1), CoreMatchers.containsString("[1]"));
                MatcherAssert.assertThat(detailBodyFor(reportParserForActualValue, 1), CoreMatchers.containsString("145"));
              },
              () -> {
                MatcherAssert.assertThat(
                    countDetailIndicesInSummary(reportParserForActualValue),
                    CoreMatchers.equalTo(2L));
              }
          ),
          () -> assertAllRunnables(
              () -> {
                MatcherAssert.assertThat(detailSubjectFor(reportParserForExpectation, 0), CoreMatchers.containsString("[0]"));
                MatcherAssert.assertThat(detailBodyFor(reportParserForExpectation, 0), CoreMatchers.equalTo("parseInt"));
              },
              () -> {
                MatcherAssert.assertThat(detailSubjectFor(reportParserForExpectation, 1), CoreMatchers.containsString("[1]"));
                MatcherAssert.assertThat(detailBodyFor(reportParserForExpectation, 1), CoreMatchers.containsString(">=[200]"));
              },
              () -> {
                MatcherAssert.assertThat(
                    countDetailIndicesInSummary(reportParserForActualValue),
                    CoreMatchers.equalTo(2L));
              }
          ),
          () -> assertAllRunnables(
              () -> MatcherAssert.assertThat(
                  extractInputFromReport(reportParserForExpectation),
                  CoreMatchers.equalTo(
                      expectedInputList)),
              () -> MatcherAssert.assertThat(
                  extractOpFromReport(reportParserForExpectation),
                  CoreMatchers.equalTo(extractedOpNameList)),
              () -> MatcherAssert.assertThat(
                  extractInputFromReport(reportParserForActualValue),
                  CoreMatchers.equalTo(
                      expectedInputList)),
              () -> MatcherAssert.assertThat(
                  extractOpFromReport(reportParserForActualValue),
                  CoreMatchers.equalTo(extractedOpNameList)
              ))
      );
      throw new ExpectedException(e);
    }
  }
  public static class ExpectedException extends RuntimeException {
    public ExpectedException(Throwable t) {
      super(t);
    }
  }

  @Ignore
  @Test
  public void assertSmoke() {
    String title = "De Bello Gallico";
    String abstractText = "Gallia est omnis divisa in partes tres, quarum unam incolunt Belgae, aliam Aquitani, tertiam qui ipsorum lingua Celtae, nostra Galli appellantur.";
    Fluent4Example.OnGoing.Book book = new Fluent4Example.OnGoing.Book(title, abstractText);
    assertSmoke(book);
  }

  @Ignore
  @Test
  public void assumeSmoke() {
    String title = "De Bello Gallico";
    String abstractText = "Gallia est omnis divisa in partes tres, quarum unam incolunt Belgae, aliam Aquitani, tertiam qui ipsorum lingua Celtae, nostra Galli appellantur.";
    Fluent4Example.OnGoing.Book book = new Fluent4Example.OnGoing.Book(title, abstractText);
    try {
      assumeSmoke(book);
    } catch (AssumptionViolatedException e) {
      ReportParser.extractActualFrom(e).summary().records().forEach(System.out::println);
    }
  }


  private static void assertSmoke(Fluent4Example.OnGoing.Book book) {
    assertAll(
        getHello(),
        getTransform(book));
  }

  private static void assumeSmoke(Fluent4Example.OnGoing.Book book) {
    assumeAll(
        getHello(),
        getTransform(book));
  }

  private static Fluent4Example.OnGoing.BookTransformer getTransform(Fluent4Example.OnGoing.Book book) {
    return new Fluent4Example.OnGoing.BookTransformer(book)
        .transform((Fluent4Example.OnGoing.BookTransformer b) -> b.title()
            .transform((StringTransformer<String> ty) -> ty.then().notNull().done())
            .transform((StringTransformer<String> ty) -> ty.parseInt().then() // This is intended to produce a NumberFormatException
                .greaterThanOrEqualTo(10)
                .lessThan(40)
                .done()).done())
        .transform((Fluent4Example.OnGoing.BookTransformer b) -> b.abstractText()
            .transform((StringTransformer<String> ty) -> ty.then().checkWithPredicate(isNull().negate()).done())
            .transform((StringTransformer<String> ty) -> ty.length().then()
                .greaterThanOrEqualTo(200)
                .lessThan(400)
                .done()).done());
  }

  private static IntegerChecker<String> getHello() {
    return stringValue("hello").length().then().greaterThan(1);
  }

  private static List<String> extractInputFromReport(ReportParser reportParserForExpectation) {
    return reportParserForExpectation.summary().records()
        .stream()
        .filter(each -> each.in().isPresent())
        .map(each -> each.in()
            .orElseThrow(RuntimeException::new))
        .filter(each -> !Objects.equals("", each))
        .map(TestUtils::simplifyString)
        .collect(Collectors.toList());
  }

  private static List<String> extractOpFromReport(ReportParser reportParserForExpectation) {
    return reportParserForExpectation.summary().records()
        .stream()
        .map(ReportParser.Summary.Record::op)
        .filter(each -> !Objects.equals("", each))
        .map(TestUtils::simplifyString)
        .collect(Collectors.toList());
  }

  private static long countDetailIndicesInSummary(ReportParser reportParserForExpectation) {
    return reportParserForExpectation.summary().records().stream().filter((ReportParser.Summary.Record each) -> each.detailIndex().isPresent()).count();
  }

  private static void assertAllRunnables(Runnable... runnables) {
    class TestFailed extends RuntimeException {
      TestFailed(Throwable cause) {
        super(cause);
      }
    }
    List<Throwable> throwables = new LinkedList<>();
    for (Runnable each : runnables) {
      try {
        each.run();
      } catch (AssertionError error) {
        throwables.add(error);
      }
    }
    if (!throwables.isEmpty()) {
      for (Throwable error : throwables) {
        System.err.println(".ASSERTION FAILED:");
        System.err.println("----");
        error.printStackTrace();
        System.err.println("----");
        System.err.println();
      }
      // Make sure the entire test method fails even when an assertion library throws a ComparisonFailure.
      // I did this when I change an assertion library for some reason, it may throw a ComparisonFailure and once it happens, test failure will be masked.
      throw new TestFailed(throwables.get(0));
    }
  }

  private static String detailSubjectFor(ReportParser reportParserForActualValue, int index) {
    return reportParserForActualValue.details().get(index).subject();
  }

  private static String detailBodyFor(ReportParser reportParserForActualValue, int index) {
    return String.join("\n", reportParserForActualValue.details().get(index).body());
  }
}
