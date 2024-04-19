package com.github.dakusui.valid8j.ut.compatibility;

import com.github.dakusui.valid8j.utils.testbase.TestBase;
import com.github.dakusui.valid8j.pcond.forms.Predicates;
import org.junit.Test;

import java.io.Serializable;
import java.util.Map;

import static com.github.dakusui.valid8j.classic.Requires.requireArgument;
import static com.github.dakusui.valid8j.pcond.forms.Predicates.*;

public class StringTest extends TestBase {
  @Test(expected = IllegalArgumentException.class)
  public void testMatchesRegex() {
    String var = "hello";
    requireArgument(var, matchesRegex("HELLO"));
    requireArgument(var, and(isNotNull(), matchesRegex("HELLO")));
    requireArgument(var, and(isNotNull(), or(matchesRegex("hello")), startsWith("h")));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testStartsWith() {
    String var = "hello";
    requireArgument(var, startsWith("HELLO"));
    requireArgument(var, and(isNotNull(), startsWith("HELLO")));
    requireArgument(var, and(isNotNull(), or(startsWith("hello")), startsWith("h")));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testContainsString() {
    String var = "hello";
    requireArgument(var, containsString("HELLO"));
    requireArgument(var, and(isNotNull(), containsString("HELLO")));
    requireArgument(var, and(isNotNull(), or(startsWith("hello")), containsString("h")));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEndsWith() {
    String var = "hello";
    requireArgument(var, startsWith("HELLO"));
    requireArgument(var, and(isNotNull(), startsWith("HELLO")));
    requireArgument(var, and(isNotNull(), or(startsWith("hello")), startsWith("h")));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEqualTo() {
    String var = "hello";
    requireArgument(var, isEqualTo("HELLO"));
    requireArgument(var, and(isNotNull(), isEqualTo("HELLO")));
    requireArgument(var, and(isNotNull(), or(isEqualTo("hello")), isEqualTo("h")));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEqualsIgnoreCase() {
    String var = "hello";
    requireArgument(var, equalsIgnoreCase("HELLO"));
    requireArgument(var, and(alwaysTrue(), isNotNull(), equalsIgnoreCase("HELLO")));
    requireArgument(var, and(isNotNull(), or(equalsIgnoreCase("hello")), equalsIgnoreCase("h")));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testIsEmptyString() {
    String var = "hello";
    requireArgument(var, isEmptyString());
    requireArgument(var, and(isNotNull(), isEmptyString()));
    requireArgument(var, and(isNotNull(), or(isEmptyString()), isEmptyString()));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testIsEmptyOrNullString() {
    String var = "hello";
    requireArgument(var, isNullOrEmptyString());
    requireArgument(var, and(isNullOrEmptyString(), isNullOrEmptyString()));
    requireArgument(var, and(isNotNull(), or(isNullOrEmptyString()), isNullOrEmptyString()));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testComparison() {
    String var = "hello";
    requireArgument(var, and(ge("A"), lt("Z")));
    requireArgument(var, or(le("a"), gt("z")));
    requireArgument(var, Predicates.eq("HELLO"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSameAs() {
    String var = "hello";
    requireArgument(var, isSameReferenceAs("A"));
    requireArgument(var, or(isSameReferenceAs("A"), isSameReferenceAs("B")));
  }

  @Test
  public void testIsInstanceOf() {
    String var = "hello";
    requireArgument(var, isInstanceOf(String.class));
    requireArgument(var, Predicates.<Object>and(
        isInstanceOf(String.class),
        isInstanceOf(Serializable.class),
        isInstanceOf(Comparable.class),
        not(isInstanceOf(Map.class))));
  }
}
