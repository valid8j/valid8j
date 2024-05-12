package com.github.valid8j.examples.fluent;

import com.github.valid8j.pcond.core.fluent.AbstractObjectChecker;
import com.github.valid8j.utils.reporting.ReportParser;
import org.junit.ComparisonFailure;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.util.Objects;
import java.util.function.Predicate;

import static com.github.valid8j.classic.TestAssertions.assertThat;
import static com.github.valid8j.fluent.Expectations.assertAll;
import static com.github.valid8j.pcond.fluent.Statement.stringValue;
import static com.github.valid8j.pcond.forms.Functions.identity;
import static com.github.valid8j.pcond.forms.Predicates.*;
import static com.github.valid8j.pcond.forms.Printables.predicate;
import static com.github.valid8j.pcond.internals.InternalUtils.makeSquashable;

@RunWith(Enclosed.class)
public class Fluent3Example {

  public static class Done {
    @Test(expected = ComparisonFailure.class)
    public void secondExample() {
      assertAll(
          stringValue("Hello")
              .toBe()
              .nullValue());
    }

    @Test(expected = ComparisonFailure.class)
    public void thirdExample() {
      assertAll(
          stringValue("Hello")
              .allOf()
              .toBe()
              .notNull()
              .nullValue());
    }

    @Test(expected = ComparisonFailure.class)
    public void thirdExample_a() {
      assertAll(
          stringValue("Hello")
              .allOf()
              .toBe()
              .notNull()
              .nullValue());
    }

    @Test(expected = ComparisonFailure.class)
    public void thirdExample_b() {
      assertAll(
          stringValue("Hello")
              .anyOf()
              .toBe()
              .notNull()
              .nullValue());
    }

    @Test(expected = ComparisonFailure.class)
    public void thirdExample_c() {
      assertAll(
              stringValue(null)
                      .anyOf()
                      .toBe()
                      .not(AbstractObjectChecker::nullValue));
    }

    @Test(expected = ComparisonFailure.class)
    public void forthExample() {
      assertAll(
          stringValue("Hello").length().then().greaterThan(10));
    }

    @Test(expected = ComparisonFailure.class)
    public void fifth() {
      assertAll(
          stringValue("Hello5").length().then().greaterThan(10).lessThan(1));
    }

    @Test(expected = ComparisonFailure.class)
    public void test9() {
      assertAll(
          stringValue("Hello5")
              .then()
              .check(v -> v.nullValue().toPredicate())
      );
    }

    @Test(expected = ComparisonFailure.class)
    public void test7() {
      assertAll(
          stringValue("Hello5")
              .satisfies(tx -> tx.then().notNull())
              .satisfies(tx -> tx.then().nullValue()));
    }

    @Test(expected = ComparisonFailure.class)
    public void test6b() {
      assertAll(
          stringValue("Hello5")
              .satisfies(tx -> tx.then().nullValue()));
    }

    @Test(expected = ComparisonFailure.class)
    public void test6c() {
      assertAll(
          stringValue("Hello5")
              .anyOf()
              .satisfies(tx -> tx.then().nullValue()));
    }

    @Test(expected = ComparisonFailure.class)
    public void test6d() {
      assertAll(
          stringValue("Hello5")
              .satisfies(tx -> tx.then().predicate(Objects::isNull))
              .satisfies(tx -> tx.then().predicate(v -> !Objects.isNull(v)))
      );
    }

    /**
     * Works without calling `toStatement()` method.
     */
    @Test(expected = ComparisonFailure.class)
    public void test7_a() {
      assertAll(
          stringValue("Hello5")
              .satisfies(tx -> tx.then().notNull())
              .satisfies(tx -> tx.then().nullValue()));
    }

    @Test(expected = ComparisonFailure.class)
    public void test6() {
      assertAll(
          stringValue("Hello5")
              .satisfies(tx -> tx.length().then().greaterThan(10)));
    }
  }


  @Ignore
  public static class OnGoing {
    @Test(expected = ComparisonFailure.class)
    public void test8() {
      assertAll(
          stringValue("Hello5")
              .satisfies()
              .check(v -> v.nullValue().done())
              .check(v -> v.notNull().done()));
    }

    @Test(expected = ComparisonFailure.class)
    public void firstExample() {
      assertAll(
          stringValue("Hello").allOf()
              .satisfies(tx -> tx.length()
                  .then()
                  .allOf()
                  .greaterThan(10)
                  .lessThan(100)));
    }

    @Test(expected = ComparisonFailure.class)
    public void test7_C() {
      assertAll(
          stringValue("Hello5")
              .satisfies(tx -> tx
                  .satisfies(ty -> ty.length()
                      .toBe()
                      .greaterThanOrEqualTo(800)
                      .lessThan(1000)
                  )
                  .satisfies(ty -> ty.toBe().notNull()))
              .satisfies(tx -> tx.toBe().nullValue()));
    }

    @Test
    public void givenBookTitle_whenLength_thenNotNullAndAppropriateLength() {
      assertAll(
          stringValue("De Bello Gallico")
              .satisfies(ty -> ty.then().notNull())
              .satisfies(ty -> ty.length().then()
                  .greaterThanOrEqualTo(10)
                  .lessThan(40)));
    }

    @Test(expected = ComparisonFailure.class)
    public void givenBookTitleAndAbstract_whenCheckThem_thenTheyAreNotNullAndAppropriateLength() {
      String bookTitle = "De Bello Gallico";
      String bookAbstract = "Gallia est omnis divisa in partes tres, quarum unam incolunt Belgae, aliam Aquitani, tertiam qui ipsorum lingua Celtae, nostra Galli appellantur.";
      assertAll(
          stringValue(bookTitle)
              .satisfies(ty -> ty.then().notNull())
              .satisfies(ty -> ty.length().then()
                  .greaterThanOrEqualTo(10)
                  .lessThan(40)),
          stringValue(bookAbstract)
              .satisfies(ty -> ty.toBe().notNull())
              .satisfies(ty -> ty.length().toBe().greaterThanOrEqualTo(200).lessThan(400)));
    }

    @Test
    public void givenBook_whenCheckTitleAndAbstract_thenTheyAreNotNullAndAppropriateLength() {
      Fluent4Example.OnGoing.Book book = new Fluent4Example.OnGoing.Book("De Bello Gallico", "Gallia est omnis divisa in partes tres, quarum unam incolunt Belgae, aliam Aquitani, tertiam qui ipsorum lingua Celtae, nostra Galli appellantur.");
      assertAll(
          new Fluent4Example.OnGoing.BookTransformer(book)
              .satisfies(b -> b.title()
                  .satisfies(ty -> ty.then().notNull()))
              .satisfies(b -> b.abstractText()
                  .satisfies(ty -> ty.then().notNull())));
    }

    @Test(expected = ComparisonFailure.class)
    public void test6() {
      assertAll(
          stringValue("Hello5")
              .satisfies(tx -> tx.length().then().greaterThanOrEqualTo(10).lessThan(100)));
    }

    @Test(expected = ComparisonFailure.class)
    public void test6b() {
      assertAll(stringValue("Hello5")
          .length()
          .then()
          .greaterThanOrEqualTo(10)
          .lessThan(100));
    }

    @Test(expected = ComparisonFailure.class)
    public void thirdExample_a() {
      assertAll(
          stringValue("Hello")
              .length()
              .then()
              .allOf()
              .notNull()
              .nullValue());
    }

    @Test(expected = ComparisonFailure.class)
    public void test7() {
      assertAll(
          stringValue("Hello5")
              .satisfies(tx -> tx.then().notNull())
              .satisfies(tx -> tx.then().nullValue()));
    }

    @Test//(expected = ComparisonFailure.class)
    public void givenBook_whenCheckTitleAndAbstract_thenTheyAreNotNullAndAppropriateLength_2() {
      Fluent4Example.OnGoing.Book book = new Fluent4Example.OnGoing.Book("De Bello Gallico", "Gallia est omnis divisa in partes tres, quarum unam incolunt Belgae, aliam Aquitani, tertiam qui ipsorum lingua Celtae, nostra Galli appellantur.");
      try {
        assertAll(
            stringValue("hello").length().then().greaterThan(1),
            new Fluent4Example.OnGoing.BookTransformer(book)
                .satisfies(b -> b.title()
                    .satisfies(ty -> ty.then().notNull())
                    .satisfies(ty -> ty.parseInt().then()
                        .greaterThanOrEqualTo(10)
                        .lessThan(40)))
                .satisfies(b -> b.abstractText()
                    .satisfies(ty -> ty.then().checkWithPredicate(isNull().negate()))
                    .satisfies(ty -> ty.length().then()
                        .greaterThanOrEqualTo(200)
                        .lessThan(400))));
      } catch (ComparisonFailure e) {
        ReportParser reportParser = new ReportParser(e.getActual());
        System.out.println("summary=" + reportParser.summary());
        reportParser.details().forEach(
            each -> {
              System.out.println(each.subject());
              System.out.println(each.body());
            }
        );
        throw e;
      }
    }

    @Test//(expected = ComparisonFailure.class)
    public void givenBook_whenCheckTitleAndAbstract_thenTheyAreNotNullAndAppropriateLength_3() {
      Fluent4Example.OnGoing.Book book = new Fluent4Example.OnGoing.Book("De Bello Gallico", "Gallia est omnis divisa in partes tres, quarum unam incolunt Belgae, aliam Aquitani, tertiam qui ipsorum lingua Celtae, nostra Galli appellantur.");
      try {
        assertAll(
            new Fluent4Example.OnGoing.BookTransformer(book)
                .satisfies(b -> b.title()
                    .satisfies(ty -> ty.then().notNull())
                    .satisfies(ty -> ty.length().then()
                        .greaterThanOrEqualTo(10)
                        .checkWithPredicate(errorThrowingPredicate())
                    ))
                .satisfies(b -> b.abstractText()
                    .satisfies(ty -> ty.then().notNull())
                    .satisfies(ty -> ty.length().then()
                        .greaterThanOrEqualTo(200)
                        .lessThan(400)
                    )));
      } catch (ComparisonFailure e) {
        ReportParser reportParser = new ReportParser(e.getActual());
        System.out.println("summary=" + reportParser.summary());
        reportParser.details().forEach(
            each -> {
              System.out.println(each.subject());
              System.out.println(each.body());
            }
        );
        throw e;
      }
    }

    private Predicate<? super Integer> errorThrowingPredicate() {
      return predicate("errorThrowingPredicate", p -> {
        throw new RuntimeException("Intentional runtime exception!!!");
      });
    }

    @Test(expected = ComparisonFailure.class)
    public void makeTrivialTest() {
      assertThat("hello", transform(makeSquashable(identity())).check(transform(makeSquashable(identity())).check(isEqualTo("HELLO"))));
    }
  }
}
