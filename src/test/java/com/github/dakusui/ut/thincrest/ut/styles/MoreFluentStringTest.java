package com.github.dakusui.ut.thincrest.ut.styles;

import com.github.dakusui.shared.utils.ut.TestBase;
import com.github.dakusui.valid8j.pcond.fluent.Statement;
import org.junit.Test;

import java.util.regex.Pattern;

import static com.github.dakusui.thincrest.TestFluents.assertStatement;

public class MoreFluentStringTest extends TestBase {
  @Test
  public void test_contains() {
    String var = "world";
    assertStatement(Statement.stringValue(var).toUpperCase().then().containing("W"));
  }

  @Test
  public void test_startingWith() {
    String var = "Hello, world";
    assertStatement(Statement.stringValue(var).toUpperCase().satisfies().startingWith("H"));
  }

  @Test
  public void test_endingWith() {
    String var = "Hello, world";
    assertStatement(Statement.stringValue(var).toUpperCase().toBe().endingWith("D"));
  }

  @Test
  public void test_isEmpty() {
    String var = "";
    assertStatement(Statement.stringValue(var).then().empty());
  }

  @Test
  public void test_isEqualTo() {
    String var = "hello";
    assertStatement(Statement.stringValue(var).then().equalTo("hello"));
  }

  @Test
  public void test_isNullOrEmpty() {
    String var = "";
    assertStatement(Statement.stringValue(var).then().nullOrEmpty());
  }

  @Test
  public void test_matchesRegex() {
    String var = "hello";
    assertStatement(Statement.stringValue(var).then().matchingRegex("he.+"));
  }

  @Test
  public void test_equalsIgnoreCase() {
    String var = "hello";
    assertStatement(Statement.stringValue(var).then().equalToIgnoringCase("HELLO"));
  }

  @Test
  public void test_findRegexes() {
    String var = "hello";
    assertStatement(Statement.stringValue(var).then().containingRegexes("he.", "lo"));
  }

  @Test
  public void test_findRegexPatterns() {
    String var = "hello";
    assertStatement(Statement.stringValue(var).then().containingRegexes(Pattern.compile("he."), Pattern.compile("lo")));
  }


  @Test
  public void test_findSubstrings() {
    String var = "hello";
    assertStatement(Statement.stringValue(var).then().containingSubstrings("hel", "lo"));
  }
}
