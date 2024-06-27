package com.github.valid8j.ut.valuechecker;

import com.github.valid8j.pcond.forms.Functions;
import com.github.valid8j.pcond.forms.Predicates;
import com.github.valid8j.pcond.validator.Validator;
import com.github.valid8j.utils.testbase.TestBase;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import java.util.Objects;
import java.util.Properties;

import static com.github.valid8j.pcond.forms.Functions.length;
import static com.github.valid8j.pcond.forms.Predicates.*;
import static com.github.valid8j.utils.TestUtils.lineAt;
import static com.github.valid8j.utils.TestUtils.numLines;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

public class DefaultValidatorTest extends TestBase {
  @Test(expected = IllegalArgumentException.class)
  public void withoutEvaluator_conj_thenFail() {
    try {
      createAssertionProvider(useEvaluator(newProperties(), false))
          .requireArgument("Hello", and(isNotNull(), isEmptyString().negate(), transform(length()).check(gt(10))));
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
      System.out.printf("----%n%s<%s>----%n", e.getMessage(), numLines(e.getMessage()));
      assertEquals(1, numLines(e.getMessage()));
      throw e;
    }
  }
  
  @Test
  public void withoutEvaluator_conj_thenPass() {
    createAssertionProvider(useEvaluator(newProperties(), false))
        .requireArgument("Hello World, everyone", and(isNotNull(), isEmptyString().negate(), transform(length()).check(gt(10))));
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void withEvaluator_columns100_conj() {
    try {
      createAssertionProvider(nameWidth(useEvaluator(newProperties(), true), 100))
          .requireArgument("Hello", and(isNotNull(), isEmptyString().negate(), transform(length()).check(gt(10))));
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
      throw e;
    }
  }
  
  @Test
  public void withEvaluator_nativePredicate() {
    String value = createAssertionProvider(nameWidth(useEvaluator(newProperties(), true), 100))
        .requireArgument("Hello", v -> v.equals("Hello"));
    assertThat(value, equalTo("Hello"));
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void withEvaluator_disj$or$_thenFail() {
    try {
      createAssertionProvider(nameWidth(useEvaluator(newProperties(), true), 100))
          .requireArgument("Hello",
                           or(                                       // (1)
                                                                     isEqualTo("hello"),             // (2)
                                                                     isEqualTo("HELLO")));           // (3)
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
      int i = 1;
      
      // actual (1)
      assertThat(lineAt(e.getMessage(), i++), allOf(
          CoreMatchers.containsString("Mismatch>:"),
          CoreMatchers.containsString("or"),
          CoreMatchers.containsString("->"),
          CoreMatchers.containsString("false")
      
      ));
      // actual (2)
      assertThat(lineAt(e.getMessage(), i++), allOf(
          CoreMatchers.containsString("Mismatch>:"),
          CoreMatchers.containsString("isEqualTo[hello]"),
          CoreMatchers.containsString("->"),
          CoreMatchers.containsString("false")
      
      ));
      // actual (3)
      assertThat(lineAt(e.getMessage(), i), allOf(
          CoreMatchers.containsString("Mismatch>:"),
          CoreMatchers.containsString("  isEqualTo[HELLO]"),
          CoreMatchers.containsString("->"),
          CoreMatchers.containsString("false")
      ));
      throw e;
    }
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void withEvaluator_disj$anyOf$_thenFail() {
    try {
      createAssertionProvider(nameWidth(useEvaluator(newProperties(), true), 100))
          .requireArgument("Hello",
                           anyOf(                               // (1)
                                                                isEqualTo("hello"),
                                                                isEqualTo("HELLO")));
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
      //actual (1)
      assertThat(lineAt(e.getMessage(), 1), allOf(
          CoreMatchers.containsString("Mismatch>:"),
          CoreMatchers.containsString("anyOf"),
          CoreMatchers.containsString("->"),
          CoreMatchers.containsString("false")
      ));
      throw e;
    }
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void withEvaluator_transforming_thenFail() {
    try {
      createAssertionProvider(nameWidth(useEvaluator(newProperties(), true), 100))
          .requireArgument("Hello",
                           transform(Functions.length())                    // (1)
                                                                            .check(                                      // (2) - 1
                                                                                                                         Predicates.gt(10)));               // (2) - 2
    } catch (IllegalArgumentException e) {
      e.printStackTrace(System.out);
      int i = 0;
      // (1)
      assertThat(lineAt(e.getMessage(), ++i), allOf(
          CoreMatchers.containsString("length"),
          CoreMatchers.containsString("->"),
          CoreMatchers.containsString("5")
      
      ));
      // (2) - 1
      assertThat(lineAt(e.getMessage(), ++i),
                 allOf(
                     CoreMatchers.containsString("Mismatch>:"),
                     CoreMatchers.containsString("check"),
                     CoreMatchers.containsString(">[10]"),
                     CoreMatchers.containsString("->"),
                     CoreMatchers.containsString("false")));
      throw e;
    }
  }
  
  @Test
  public void withEvaluator_disj_thenPass() {
    try {
      createAssertionProvider(nameWidth(useEvaluator(newProperties(), true), 100))
          .requireArgument("hello", or(isEqualTo("hello"), isEqualTo("HELLO")));
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
      throw e;
    }
  }
  
  /**
   * Expected message:
   * <pre>
   * java.lang.IllegalArgumentException: value:null violated precondition:value ((isNotNull&&!isEmpty)&&length >[10])
   * null -> &&          ->     false
   *           isNotNull -> false
   * </pre>
   */
  @Test(expected = IllegalArgumentException.class)
  public void withEvaluator_columns100$whenNull() {
    try {
      createAssertionProvider(nameWidth(useEvaluator(newProperties(), true), 100))
          .requireArgument(null,
                           and(                                              // (1)
                                                                             isNotNull(),                                  // (2)
                                                                             isEmptyString().negate(),
                                                                             transform(length()).check(gt(10))));
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
      int i = 1;
      // actual (1)
      assertThat(lineAt(e.getMessage(), i++), allOf(
          CoreMatchers.containsString("Mismatch>:"),
          CoreMatchers.containsString("and"),
          CoreMatchers.containsString("->false")
      
      ));
      // actual (2)
      assertThat(lineAt(e.getMessage(), i), allOf(
          CoreMatchers.containsString("Mismatch>:"),
          CoreMatchers.containsString("isNotNull"),
          CoreMatchers.containsString("->false")
      
      ));
      throw e;
    }
  }
  
  /**
   * Expected message:
   * ----
   * java.lang.IllegalArgumentException: value:"hello" violated precondition:value (isNotNull&&!isEmpty&&length >[10])
   * Mismatch>:"hello"->and               ->false                // (1)
   * isNotNull       ->true                 // (2)
   * not(isEmpty)    ->true                 // (3)
   * transform:length->5                    // (4)
   * Mismatch>:5      ->  check:>[10]     ->false                // (5)
   * ----
   */
  @Test(expected = IllegalArgumentException.class)
  public void withEvaluator_columns100$whenShorterThan10() {
    try {
      createAssertionProvider(nameWidth(useEvaluator(newProperties(), true), 100))
          .requireArgument("hello",
                           and(                                                // (1)
                                                                               isNotNull(),                                    // (2)
                                                                               isEmptyString().negate(),                       // (3)
                                                                               transform(length())                             // (4)
                                                                                                                               .check(gt(10))));                     // (5)
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
      int i = 1;
      // actual (1)
      assertThat(lineAt(e.getMessage(), i++), allOf(
          CoreMatchers.containsString("Mismatch>:"),
          CoreMatchers.containsString("and"),
          CoreMatchers.containsString("->false")
      
      ));
      // (2)
      assertThat(lineAt(e.getMessage(), i++), allOf(
          CoreMatchers.not(CoreMatchers.containsString("Mismatch:")),
          CoreMatchers.containsString("isNotNull"),
          CoreMatchers.containsString("->true")
      
      ));
      // (3)
      assertThat(lineAt(e.getMessage(), i++), allOf(
          CoreMatchers.not(CoreMatchers.containsString("Mismatch:")),
          CoreMatchers.containsString("not:isEmpty"),
          CoreMatchers.containsString("->true")
      
      ));
      // (4)
      assertThat(lineAt(e.getMessage(), i++), allOf(
          CoreMatchers.not(CoreMatchers.containsString("Mismatch")),
          CoreMatchers.containsString("length"),
          CoreMatchers.containsString("->5")
      
      ));
      // actual (5)
      assertThat(lineAt(e.getMessage(), i), allOf(
          CoreMatchers.containsString("Mismatch>:"),
          CoreMatchers.containsString("check:"),
          CoreMatchers.containsString(">[10]"),
          CoreMatchers.containsString("->false")
      ));
      throw e;
    }
  }
  
  public Validator.Impl createAssertionProvider(Properties properties) {
    return new Validator.Impl(Validator.configurationFromProperties(properties));
  }
  
  public static Properties useEvaluator(Properties properties, boolean useEvaluator) {
    properties.setProperty("useEvaluator", Objects.toString(useEvaluator));
    return properties;
  }
  
  public static Properties nameWidth(Properties properties, int columns) {
    properties.setProperty("summarizedStringLength", Objects.toString(columns));
    return properties;
  }
  
  public static Properties newProperties() {
    return new Properties();
  }
}
