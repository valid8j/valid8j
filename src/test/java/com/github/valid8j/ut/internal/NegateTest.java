package com.github.valid8j.ut.internal;

import com.github.valid8j.utils.exceptions.ApplicationException;
import com.github.valid8j.utils.testbase.TestBase;
import com.github.valid8j.pcond.forms.Printables;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import java.util.function.Predicate;

import static com.github.valid8j.classic.Validates.validate;
import static com.github.valid8j.utils.TestUtils.lineAt;
import static com.github.valid8j.utils.TestUtils.simplifyString;
import static com.github.valid8j.pcond.forms.Functions.length;
import static com.github.valid8j.pcond.forms.Predicates.*;

public class NegateTest extends TestBase {
  @Test(expected = ApplicationException.class)
  public void whenInvertedTransformingPredicateFails_thenPrintDesignedMessage$transformIsntLeafAndNotMerged() {
    try {
      validate("",
          not(transform(length())                           // (1)
              .check(lt(100))),                       // (2)
          ApplicationException::new);
    } catch (ApplicationException e) {
      e.printStackTrace();
      int i = 0;
      MatcherAssert.assertThat(
          simplifyString(lineAt(e.getMessage(), i++)),
          CoreMatchers.equalTo("Value:'' violated: !length <[100]"));
      // (1)
      MatcherAssert.assertThat(
          simplifyString(lineAt(e.getMessage(), i++)),
          CoreMatchers.allOf(
              CoreMatchers.containsString("Mismatch<:"),
              CoreMatchers.containsString("not"),
              CoreMatchers.containsString("->true")));
      MatcherAssert.assertThat(
          simplifyString(lineAt(e.getMessage(), i++)),
          CoreMatchers.allOf(
              CoreMatchers.containsString("Mismatch>:"),
              CoreMatchers.containsString("not"),
              CoreMatchers.containsString("->false")));
      MatcherAssert.assertThat(
          simplifyString(lineAt(e.getMessage(), i++)),
          CoreMatchers.allOf(
              CoreMatchers.containsString("length"),
              CoreMatchers.containsString("->0")));
      // expected (2)
      MatcherAssert.assertThat(
          simplifyString(lineAt(e.getMessage(), i++)),
          CoreMatchers.allOf(
              CoreMatchers.containsString("Mismatch<:"),
              CoreMatchers.containsString("check:<[100]"),
              CoreMatchers.containsString("->false")));
      // actual (2)
      MatcherAssert.assertThat(
          simplifyString(lineAt(e.getMessage(), i++)),
          CoreMatchers.allOf(
              CoreMatchers.containsString("Mismatch>:"),
              CoreMatchers.containsString("check:<[100]"),
              CoreMatchers.containsString("->true")));
      throw e;
    }
  }

  @Test(expected = ApplicationException.class)
  public void whenInvertedTrasformingPredicateFails_thenPrintDesignedMessage$notMergedWhenMismatch() {
    try {
      validate("Hello",
          not(equalTo("Hello")),                 // (1)
          ApplicationException::new);
    } catch (ApplicationException e) {
      e.printStackTrace();
      MatcherAssert.assertThat(
          simplifyString(lineAt(e.getMessage(), 0)),
          CoreMatchers.equalTo("Value:'Hello' violated: !=[Hello]"));
      // expected (1)
      MatcherAssert.assertThat(
          simplifyString(lineAt(e.getMessage(), 1)),
          CoreMatchers.allOf(
              CoreMatchers.containsString("Mismatch<:"),
              CoreMatchers.containsString("'Hello'"),
              CoreMatchers.containsString("->not:=[Hello]"),
              CoreMatchers.containsString("->true")
          ));
      // actual (1)
      MatcherAssert.assertThat(
          simplifyString(lineAt(e.getMessage(), 2)),
          CoreMatchers.allOf(
              CoreMatchers.containsString("Mismatch>:"),
              CoreMatchers.containsString("'Hello'"),
              CoreMatchers.containsString("->not:=[Hello]"),
              CoreMatchers.containsString("->false")));
      throw e;
    }
  }

  @Test(expected = ApplicationException.class)
  public void whenInvertedTrasformingPredicateFails_thenPrintDesignedMessage$mergedWhenNotMismatch() {
    try {
      validate(
          "Hello",
          and(                                      // (1)
              not(equalTo("WORLD")),         // (2)
              alwaysFalse()),                       // (3)
          ApplicationException::new);
    } catch (ApplicationException e) {
      e.printStackTrace();
      MatcherAssert.assertThat(
          simplifyString(lineAt(e.getMessage(), 0)),
          CoreMatchers.equalTo("Value:'Hello' violated: (!=[WORLD]&&alwaysFalse)"));
      // expected (1)
      MatcherAssert.assertThat(
          simplifyString(lineAt(e.getMessage(), 1)),
          CoreMatchers.allOf(
              CoreMatchers.containsString("Mismatch<:"),
              CoreMatchers.containsString("'Hello'"),
              CoreMatchers.containsString("and"),
              CoreMatchers.containsString("->true")));
      // actual (1)
      MatcherAssert.assertThat(
          simplifyString(lineAt(e.getMessage(), 2)),
          CoreMatchers.allOf(
              CoreMatchers.containsString("Mismatch>:"),
              CoreMatchers.containsString("'Hello'"),
              CoreMatchers.containsString("and"),
              CoreMatchers.containsString("->false")));
      // (2)
      MatcherAssert.assertThat(
          simplifyString(lineAt(e.getMessage(), 3)),
          CoreMatchers.allOf(
              CoreMatchers.containsString("not:=[WORLD]"),
              CoreMatchers.containsString("->true")));
      // expected (3)
      MatcherAssert.assertThat(
          simplifyString(lineAt(e.getMessage(), 4)),
          CoreMatchers.allOf(
              CoreMatchers.containsString("Mismatch<:"),
              CoreMatchers.containsString("alwaysFalse"),
              CoreMatchers.containsString("->true")));
      // actual (3)
      MatcherAssert.assertThat(
          simplifyString(lineAt(e.getMessage(), 5)),
          CoreMatchers.allOf(
              CoreMatchers.containsString("Mismatch>:"),
              CoreMatchers.containsString("alwaysFalse"),
              CoreMatchers.containsString("->false")));
      throw e;
    }
  }

  private static Predicate<String> alwaysFalse() {
    return Printables.predicate("alwaysFalse", v -> false);
  }
}
