package com.github.valid8j.ut.types;

import com.github.valid8j.utils.testbase.TestBase;

/**
 * Temporarily commented out for improving new fluent model.
 */
public class BooleanTest extends TestBase {
  /*
  @Test
  public void booleanTest() {
    boolean value = true;
    validate(value, when().asBoolean().then().isTrue());
  }

  @Test(expected = IllegalValueException.class)
  public void booleanTestFail() {
    boolean value = true;
    try {
      validate(value, when().asBoolean().then().isFalse());
    } catch (IllegalValueException e) {
      e.printStackTrace();
      // TODO
//      MatcherAssert.assertThat(e.getExpected(), CoreMatchers.containsString("true->isFalse->true"));
//      MatcherAssert.assertThat(e.getActual(), CoreMatchers.containsString("true->isFalse->false"));
      MatcherAssert.assertThat(
          e.getMessage().replaceAll(" +", ""),
          CoreMatchers.containsString("true->isFalse->true"));
      MatcherAssert.assertThat(
          e.getMessage().replaceAll(" +", ""),
          CoreMatchers.containsString("true->isFalse->false"));
      throw e;
    }
  }

  @Test
  public void booleanTransformerTest() {
    boolean value = true;
    validate(value, when().asObject().asBoolean().then().isTrue());
  }

  @Test(expected = IllegalValueException.class)
  public void booleanTransformerTestFail() {
    boolean value = true;
    validate(value, when().asObject().asBoolean().then().isFalse());
  }

   */
}
