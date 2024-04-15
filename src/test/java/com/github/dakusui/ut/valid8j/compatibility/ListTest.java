package com.github.dakusui.ut.valid8j.compatibility;

import org.junit.Test;

import java.util.Collection;
import java.util.List;

import static com.github.dakusui.valid8j.classic.Requires.requireArgument;
import static com.github.dakusui.valid8j.pcond.forms.Functions.stream;
import static com.github.dakusui.valid8j.pcond.forms.Predicates.*;
import static java.util.Arrays.asList;

public class ListTest {
  @Test(expected = IllegalArgumentException.class)
  public void testListContains() {
    List<String> var = asList("hello", "world");
    requireArgument(var, contains("HELLO"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCollectionContains() {
    Collection<String> var = asList("hello", "world");
    requireArgument(var, contains("HELLO"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEmpty() {
    Collection<String> var = asList("hello", "world");
    requireArgument(var, isEmpty());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAllMatch() {
    Collection<String> var = asList("hello", "world", null);
    requireArgument(var, transform(stream()).check(allMatch(isNotNull())));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAnyMatch() {
    Collection<String> var = asList("hello", "world");
    requireArgument(var, transform(stream()).check(anyMatch(isNull())));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNoneMatch() {
    Collection<String> var = asList("hello", "world", null);
    requireArgument(var, transform(stream()).check(noneMatch(isNull())));
  }
}
