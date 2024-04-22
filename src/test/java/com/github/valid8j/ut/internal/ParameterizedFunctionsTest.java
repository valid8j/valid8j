package com.github.valid8j.ut.internal;

import com.github.valid8j.pcond.forms.Functions;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.List;
import java.util.TreeSet;
import java.util.function.Function;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class ParameterizedFunctionsTest {
  private final Function       function;
  private final String         expectationForToString;
  private final List<TestItem> testItems;

  public ParameterizedFunctionsTest(Function function, String expectationForToString, List<TestItem> testItems) {
    this.function = function;
    this.expectationForToString = expectationForToString;
    this.testItems = testItems;
  }

  @Test
  public void exerciseToString() {
    assertEquals(expectationForToString, function.toString());
  }

  @SuppressWarnings("unchecked")
  @Test
  public void exercisePredicate() {
    for (TestItem testItem : testItems) {
      assertThat(String.format("testItem:%s", testItem), function.apply(testItem.data), testItem.matcher);
    }
  }

  @Parameterized.Parameters
  public static List<Object[]> parameters() {
    return asList(
        new Object[] {
            Functions.identity(),
            "identity",
            singletonList(
                TestItem.$("X", CoreMatchers.equalTo("X"))
            )
        },
        new Object[] {
            Functions.length(),
            "length",
            singletonList(
                TestItem.$("ABC", CoreMatchers.equalTo(3))
            )
        },
        new Object[] {
            Functions.cast(String.class),
            "castTo[String]",
            singletonList(
                TestItem.$("hello", CoreMatchers.equalTo("hello")) // Check only identity.
            )
        },
        new Object[] {
            Functions.collectionToList(),
            "collectionToList",
            singletonList(
                TestItem.$(
                    new TreeSet<String>() {{
                      add("C");
                      add("B");
                      add("A");
                    }},
                    CoreMatchers.equalTo(asList("A", "B", "C"))
                ))
        },
        new Object[] {
            Functions.arrayToList(),
            "arrayToList",
            singletonList(
                TestItem.$(new Object[] { "A", "B" }, CoreMatchers.equalTo(asList("A", "B")))
            )
        },
        new Object[] {
            Functions.countLines(),
            "countLines",
            singletonList(
                TestItem.$("hello\nworld", CoreMatchers.equalTo(2))
            )
        },
        new Object[] {
            Functions.arrayToList().andThen(Functions.size()),
            "arrayToList->size",
            singletonList(
                TestItem.$(
                    new Object[] { "A", "B", "C" },
                    CoreMatchers.equalTo(3)
                )
            )
        },
        new Object[] {
            Functions.size().compose(Functions.arrayToList()),
            "arrayToList->size",
            singletonList(
                TestItem.$(
                    new Object[] { "A", "B", "C" },
                    CoreMatchers.equalTo(3)
                )
            )
        }
    );
  }

  static class TestItem {
    final Object  data;
    final Matcher matcher;

    TestItem(Object data, Matcher matcher) {
      this.data = data;
      this.matcher = matcher;
    }

    @Override
    public String toString() {
      return String.format("data:%s; matcher:%s", data, matcher);
    }

    static TestItem $(Object data, Matcher matcher) {
      return new TestItem(data, matcher);
    }
  }
}
