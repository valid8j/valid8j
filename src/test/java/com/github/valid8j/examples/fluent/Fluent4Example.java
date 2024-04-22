package com.github.valid8j.examples.fluent;

import com.github.valid8j.fluent.Expectations;
import com.github.valid8j.utils.reporting.ReportParser;
import com.github.valid8j.pcond.core.fluent.builtins.StringTransformer;
import com.github.valid8j.pcond.forms.Functions;
import com.github.valid8j.pcond.forms.Predicates;
import com.github.valid8j.pcond.forms.Printables;
import org.junit.ComparisonFailure;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.util.function.Predicate;

import static com.github.valid8j.fluent.Expectations.*;
import static com.github.valid8j.pcond.forms.Functions.length;
import static com.github.valid8j.pcond.forms.Predicates.*;
import static com.github.valid8j.pcond.internals.InternalUtils.makeSquashable;

@RunWith(Enclosed.class)
public class Fluent4Example {
  public static class Done {
    @Test(expected = ComparisonFailure.class)
    public void test() {
      assertStatement(that("INPUT_VALUE")
          .parseBoolean()
          .satisfies()
          .isTrue());
    }

    @Test//(expected = ComparisonFailure.class)
    public void test_b() {
      assertAll(
          stringTransformer("INPUT_VALUE")
              .toLowerCase()
              .parseBoolean()
              .then()
              .isTrue());
    }

    @Test//(expected = ComparisonFailure.class)
    public void test_c() {
      assertAll(
          stringTransformer("INPUT_VALUE")
              .parseBoolean()
              .then()
              .isTrue());
    }

    @Test(expected = ComparisonFailure.class)
    public void test_allOf_inWhen() {
      assertStatement(stringTransformer("INPUT_VALUE")
          .parseBoolean()
          .then()
          .isTrue()
          .isTrue());
    }


    @Test(expected = ComparisonFailure.class)
    public void test_allOf_inWhen_2() {
      assertStatement(stringTransformer("INPUT_VALUE")
          .parseBoolean()
          .then()
          .addCheckPhrase(v -> v.checkWithPredicate(isNotNull()).toPredicate())
          .addCheckPhrase(v -> v.checkWithPredicate(Predicates.isTrue()).toPredicate()));
    }

    @Test(expected = ComparisonFailure.class)
    public void test_allOf_inWhen_3() {
      assertStatement(stringTransformer("INPUT_VALUE")
          .parseBoolean()
          .then()
          .check(v -> v.isTrue().toPredicate())
          .check(v -> v.isTrue().toPredicate()));
    }

    @Test(expected = ComparisonFailure.class)
    public void test_allOf_inWhen_4() {
      assertStatement(stringTransformer("INPUT_VALUE")
          .toLowerCase()
          .then()
          .check(v -> v.containing("XYZ1").toPredicate())
          .check(v -> v.containing("ABC1").toPredicate()));
    }

    @Test(expected = ComparisonFailure.class)
    public void test_allOf_inWhen_5() {
      assertStatement(stringTransformer("INPUT_VALUE")
          .toLowerCase()
          .parseBoolean()
          .then()
          .check(v -> v.isTrue().toPredicate())
          .check(v -> v.isTrue().toPredicate()));
    }

    @Test(expected = ComparisonFailure.class)
    public void test_allOf_inWhen_7() {
      assertStatement(stringTransformer("INPUT_VALUE")
          .satisfies(
              tx -> tx.toLowerCase()
                  .parseBoolean()
                  .then()
                  .isTrue()
          )
          .satisfies(
              tx -> tx.toLowerCase()
                  .parseBoolean()
                  .then()
                  .isTrue()
          ));
    }

    @Test(expected = ComparisonFailure.class)
    public void test_allOf_inWhen_6() {
      assertStatement(stringTransformer("INPUT_VALUE")
          .satisfies(
              tx -> {
                Predicate<String> stringPredicate = tx.toLowerCase()
                    .parseBoolean()
                    .then()
                    .isTrue()
                    .toPredicate();
                System.out.println(stringPredicate);
                return tx.toBe().predicate(stringPredicate);
              }));
    }

    @Test(expected = ComparisonFailure.class)
    public void test_allOf_inWhen_6a() {
      assertStatement(stringTransformer("INPUT_VALUE")
          .satisfies(
              tx -> tx.toLowerCase()
                  .parseBoolean()
                  .then()
                  .isTrue()
          )
          .satisfies().predicate(Predicates.transform(length()).check(isEqualTo(10))));
    }
  }

  @Ignore
  public static class OnGoing {
    @Test
    public void test_allOf_inWhen_6a() {
      assertStatement(stringTransformer("helloWorld1")
          .satisfies(
              tx -> tx.toLowerCase()
                  .parseBoolean()
                  .then()
                  .isTrue()
          )
          .satisfies()
          .predicate(Predicates.transform(length()).check(isEqualTo(10))));
    }

    @Test(expected = IllegalStateException.class)
    public void test_allOf_inWhen_6b() {
      assertStatement(stringTransformer("helloWorld2")
          .satisfies(
              tx -> tx.toLowerCase()
                  .parseBoolean()
                  .then()
                  .isTrue())
          .satisfies().predicate(Predicates.transform(length()).check(isEqualTo(10))));
    }

    @Test(expected = ComparisonFailure.class)
    public void test_allOf_inWhen_6c() {
      assertStatement(stringTransformer("helloWorld2")
          .satisfies(
              tx -> tx.toLowerCase()
                  .parseBoolean()
                  .then()
                  .isTrue())
          .satisfies().predicate(Predicates.transform(length()).check(isEqualTo(10))));
    }

    @Test
    public void givenBook_whenCheckTitleAndAbstract_thenTheyAreNotNullAndAppropriateLength() {
      try {
        Book book = new Book(
            "De Bello Gallico",
            "Gallia est omnis divisa in partes tres, quarum unam incolunt Belgae, "
                + "aliam Aquitani, tertiam qui ipsorum lingua Celtae, nostra Galli appellantur.");
        assertAll(
            new BookTransformer(book)
                .satisfies(tx -> tx.title()
                    .satisfies(ty -> ty
                        .then()
                        .checkWithPredicate(not(isNotNull())))
                    .satisfies(ty -> ty
                        .parseInt()
                        .then()
                        .greaterThanOrEqualTo(10)
                        .lessThan(40)))
                .satisfies(tx -> tx.abstractText()
                    .satisfies(ty -> ty
                        .then()
                        .notNull())
                    .satisfies(ty -> ty
                        .length()
                        .then()
                        .greaterThanOrEqualTo(200)
                        .lessThan(400))));
      } catch (ComparisonFailure e) {
        ReportParser reportParser = new ReportParser(e.getActual());
        reportParser.summary().records().forEach(each -> System.out.println(each.toString()));
        throw e;
      }
    }

    public static class Book {
      private final String abstractText;
      private final String title;

      public Book(String title, String abstractText) {
        this.abstractText = abstractText;
        this.title = title;
      }

      String title() {
        return title;
      }

      String abstractText() {
        return abstractText;
      }

      @Override
      public String toString() {
        return "Book:[title:<" + title + ">, abstract:<" + abstractText + ">]";
      }
    }

    public static class BookTransformer extends Expectations.CustomTransformer<BookTransformer, Book> {
      public BookTransformer(Book rootValue) {
        super(rootValue);
      }

      public StringTransformer<Book> title() {
        return toString(Printables.function("title", Book::title));
      }

      public StringTransformer<Book> abstractText() {
        return toString(Printables.function("abstractText", Book::abstractText));
      }
    }
  }

  private static StringTransformer.Impl<String> stringTransformer(String value) {
    return new StringTransformer.Impl<>(() -> value, makeSquashable(Functions.identity()));
  }
}