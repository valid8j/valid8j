package com.github.dakusui.valid8j.examples.fluent;

import com.github.dakusui.valid8j.utils.TestUtils;
import com.github.dakusui.valid8j.examples.sut.MemberDatabase;
import com.github.dakusui.valid8j.fluent.Expectations;
import com.github.dakusui.valid8j.pcond.fluent.Statement;
import com.github.dakusui.valid8j.pcond.forms.Printables;
import org.junit.ComparisonFailure;
import org.junit.Test;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;

import static com.github.dakusui.valid8j.pcond.forms.Predicates.isEmptyString;
import static com.github.dakusui.valid8j.pcond.forms.Predicates.not;
import static java.lang.String.format;
import static java.util.Arrays.asList;

public class MoreFluentStyleExample {
  @Test
  public void test() {
    String givenValue = "helloWorld";
    Expectations.assertStatement(Statement.stringValue(givenValue)
        .toString(TestUtils.stringToLowerCase())
        .then()
        .equalTo("HELLOWORLD"));
  }


  @Test
  public void testExpectingException() {
    String givenValue = "helloWorld";
    Expectations.assertStatement(Statement.stringValue(givenValue)
        .expectException(Exception.class, TestUtils.stringToLowerCase())
        .then()
        .equalTo("HELLOWORLD"));
  }

  @Test
  public void testExpectingException2() {
    String givenValue = "helloWorld";
    Expectations.assertStatement(Statement.stringValue(givenValue)
        .expectException(Exception.class, throwRuntimeException())
        .getCause()
        .then()
        .notNull());
  }

  @Test
  public void testThrowableTransformer() {
    String givenValue = "helloWorld";
    Expectations.assertStatement(Statement.throwableValue(new Throwable(givenValue)).getMessage().then().equalTo("helloWorld!"));
  }

  private static Function<String, Object> throwRuntimeException() {
    return Printables.function("throwRuntimeException", v -> {
      throw new RuntimeException();
    });
  }


  @Test
  public void test2() {
    List<String> givenValues = asList("hello", "world");
    Expectations.assertStatement(Statement.listValue(givenValues).elementAt(0)
        .toString(TestUtils.stringToLowerCase())
        .then()
        .equalTo("HELLO"));
  }

  @Test
  public void test3() {
    List<String> givenValues = asList("hello", "world");
    Expectations.assertStatement(Statement.listValue(givenValues).elementAt(0)
        .toString(TestUtils.stringToLowerCase())
        .then()
        .equalTo("HELLO"));
  }

  @Test(expected = ComparisonFailure.class)
  public void test4() {
    try {
      Expectations.assertAll(
          Statement.stringValue("hello").toUpperCase().then().equalTo("HELLO"),
          Statement.stringValue("world").toLowerCase().then().containing("WORLD"));
    } catch (ComparisonFailure e) {
      e.printStackTrace();
      throw e;
    }
  }

  @Test
  public void test5() {
    String identifier = "0001";
    MemberDatabase database = new MemberDatabase();
    Function<String, Function<MemberDatabase, MemberDatabase.Member>> lookUpMemberWith =
        id -> Printables.function(
            () -> format("lookUpMember[%s]", id),
            d -> d.lookUp(id).orElseThrow(NoSuchElementException::new));
    Function<MemberDatabase.Member, String> memberLastName =
        Printables.function("memberLastName", MemberDatabase.Member::lastName);

    Expectations.assertStatement(Statement.objectValue(database)
        .toObject(lookUpMemberWith.apply(identifier))
        .toString(memberLastName)
        .then()
        .notNull()
        .notEmpty()
        .equalTo("Do"));
  }

  /**
   * org.junit.ComparisonFailure: Value:["Doe",["John","Doe","PhD"]] violated: (at[0] WHEN(treatAsIs (isNotNull&&!isEmpty))&&at[1] WHEN(treatAsList contains["DOE"]))
   */
  @Test
  public void givenKnownLastName_whenFindMembersByFullName_thenLastNastIsNotNullAndContainedInFullName() {
    MemberDatabase database = new MemberDatabase();
    String lastName = database.lookUp("0001")
        .orElseThrow(NoSuchElementException::new)
        .lastName();
    List<String> fullName = database.findMembersByLastName(lastName).get(0).toFullName();
    Expectations.assertAll(
        Statement.stringValue(lastName)
            .then()
            .allOf()
            .notNull()
            .checkWithPredicate(not(isEmptyString())),
        Statement.listValue(fullName)
            .then()
            .contains("DOE"));
  }

  /**
   * org.junit.ComparisonFailure: Value:["Doe",["John","Doe","PhD"]] violated: (at[0] WHEN(DUMMY_FUNCTION:ALWAYSTHROW (isNotNull&&!isEmpty))&&at[1] WHEN(treatAsList contains["DOE"]))
   */
  @Test
  public void givenKnownLastName_whenFindMembersByFullName_thenLastNastIsNotNullAndContainedInFullName_2() {
    MemberDatabase database = new MemberDatabase();
    String lastName = database.lookUp("0001")
        .orElseThrow(NoSuchElementException::new)
        .lastName();
    List<String> fullName = database.findMembersByLastName(lastName).get(0).toFullName();
    Expectations.assertAll(
        Statement.stringValue(lastName)
            .then()
            .allOf()
            .notNull()
            .check(v -> v.anyOf()
                .check(w -> w.notNull().toPredicate())
                .check(w -> w.empty().toPredicate()).toPredicate())
            .check(v -> v.notEmpty().toPredicate()),
        Statement.listValue(fullName)
            .then()
            .contains("DOE"));
  }

  /*
  @Test
  public void testAllOf() {
    MemberDatabase database = new MemberDatabase();
    String lastName = database.lookUp("0001")
        .orElseThrow(NoSuchElementException::new)
        .lastName();
    Expectations.assertAll(
        Fluents.stringStatement(lastName)
            .then()
            .allOf()
            .appendChild(v -> v.isEqualTo("1"))
            .appendChild(v -> v.isEqualTo("2"))
            .appendChild(v -> v
                .anyOf()
                .appendChild(w -> w.isEqualTo("3"))
                .appendChild(w -> w.isEqualTo("4")))
            .appendChild(v -> v.isEqualTo("5")));
  }
*/

  @Test
  public void givenValidName_whenValidatePersonName_thenPass() {
    String s = "John Doe";

    Expectations.assertStatement(Statement.stringValue(s).split(" ").size()
        .then()
        .equalTo(2));
  }
/*
  @Test
  public void givenValidName_whenValidatePersonName_thenPass_2() {
    String s = "John doe";

    Expectations.assertStatement(
        Fluents.stringStatement(s)
            .split(" ")
            .then().allOf()
            .appendChild(tx -> tx.size().then().isEqualTo(2))
            .appendChild(tx -> tx.elementAt(0).asString().then().matchesRegex("[A-Z][a-z]+"))
            .appendChild(    tx -> tx.elementAt(1).asString().then().matchesRegex("[A-Z][a-z]+")));
  }
*/
  @Test
  public void checkTwoValues() {
    String s = "HI";
    List<String> strings = asList("HELLO", "WORLD");

    Expectations.assertAll(
        Statement.stringValue(s)
            .toString(TestUtils.stringToLowerCase())
            .then()
            .equalTo("HI"),
        Statement.listValue(strings)
            .then()
            .findElementsInOrder("HELLO", "WORLD"));
  }

  @Test
  public void checkTwoAspectsOfOneValue() {
    String s = "helloWorld";
    Expectations.assertAll(
        Statement.stringValue(s)
            .then()
            .notNull(),
        Statement.stringValue(s).length()
            .then()
            .greaterThan(100));
  }

  @Test
  public void checkTwoAspectsOfOneValue_2() {
    List<String> list = asList("helloWorld", "HI");
    Expectations.assertAll(
        Statement.listValue(list).size()
            .then()
            .greaterThan(3),
        Statement.listValue(list).elementAt(0).toString(v -> v)
            .then()
            .notNull(),
        Statement.listValue(list).elementAt(0).toString(v -> v).length()
            .then()
            .greaterThan(100));
  }
/*
  @Test
  public void checkTwoAspectsOfOneValue_3a() {
    List<String> list = asList("helloWorld", "HI");
    Expectations.assertAll(
        Fluents.listStatement(list).allOf()
            .appendChild(tx -> tx.size().then().greaterThan(3).toPredicate())
            .appendChild(tx -> tx.elementAt(0)
                .appendChild(ty -> ty.asString().then().isNotNull().toPredicate())
                .appendChild(ty -> ty.asString().length().then().greaterThan(100).toPredicate()).toPredicate()));
  }

  @Test
  public void checkTwoAspectsOfOneValue_3b() {
    List<String> list = asList("helloWorld", "HI");
    validateStatement(
        Fluents.listStatement(list).then().allOf()
            .appendChild(tx -> tx.size().then().greaterThan(3))
            .appendChild(tx -> tx.elementAt(0).asString().then().isNotNull())
            .appendChild(tx -> tx.elementAt(0).asString().length().then().greaterThan(100)));
  }

  @Test
  public void checkTwoAspectsOfOneValue_3c() {
    List<String> list = asList("helloWorld", "HI");
    validateStatement(
        Fluents.listStatement(list).then().allOf()
            .appendChild(tx -> tx.then().isNull())
            .appendChild(tx -> tx.elementAt(0).asString().then().isNotNull())
            .appendChild(tx -> tx.elementAt(0).asString().length().then().greaterThan(100)));
  }

 */
}
