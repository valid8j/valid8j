package com.github.dakusui.valid8j_pcond.bugfixes;

import com.github.dakusui.shared.IllegalValueException;
import com.github.dakusui.valid8j.utils.testbase.TestBase;
import org.junit.Test;

import static com.github.dakusui.shared.TestUtils.validate;
import static com.github.dakusui.valid8j.utils.TestUtils.lineAt;
import static com.github.dakusui.valid8j.pcond.forms.Functions.elementAt;
import static com.github.dakusui.valid8j.pcond.forms.Functions.size;
import static com.github.dakusui.valid8j.pcond.forms.Predicates.*;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

public class Issue11Test extends TestBase {
  @Test(expected = IllegalValueException.class)
  public void whenReproduceIssue() {
    Object[] args = new Object[] { 123 };
    try {
      validate(asList(args),
          and(transform(size()).check(isEqualTo(1)),
              transform(elementAt(0)).check(and(isNotNull(), isInstanceOf(String.class)))));
    } catch (IllegalValueException e) {
      e.printStackTrace();
      int i = 0;
      assertThat(lineAt(e.getMessage(), ++i), containsString("and"));
      // skip diff line
      ++i;
      ++i;
      assertThat(lineAt(e.getMessage(), i), containsString("size"));
      assertThat(lineAt(e.getMessage(), ++i), containsString("check"));
      assertThat(lineAt(e.getMessage(), i), containsString("isEqualTo[1]"));
      assertThat(lineAt(e.getMessage(), ++i), containsString("at[0]"));
      assertThat(lineAt(e.getMessage(), ++i), containsString("check"));
      assertThat(lineAt(e.getMessage(), i), containsString("and"));
      // skip diff line
      ++i;
      assertThat(lineAt(e.getMessage(), ++i), containsString("isNotNull"));
      assertThat(lineAt(e.getMessage(), ++i), containsString("isInstanceOf"));
      // skip diff line
      ++i;
      throw e;
    }
  }
}
