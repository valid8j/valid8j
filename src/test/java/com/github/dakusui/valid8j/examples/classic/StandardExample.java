package com.github.dakusui.valid8j.examples.classic;

import com.github.dakusui.valid8j.examples.sut.MemberDatabase;
import com.github.dakusui.valid8j.pcond.forms.Predicates;
import com.github.dakusui.valid8j.pcond.forms.Printables;
import org.junit.Test;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;

import static com.github.dakusui.valid8j.classic.Assertions.precondition;
import static com.github.dakusui.valid8j.classic.Requires.require;
import static com.github.dakusui.valid8j.classic.TestAssertions.assertThat;
import static com.github.dakusui.valid8j.pcond.forms.Functions.*;
import static com.github.dakusui.valid8j.pcond.forms.Predicates.*;
import static java.lang.String.format;
import static java.util.Arrays.asList;

public class StandardExample {
  public void publicMethod(String message) {
    require(message, and(isNotNull(), transform(length()).check(greaterThan(0))));
    privateMethod(message);
  }
  
  private void privateMethod(String message) {
    assert precondition(message, and(isNotNull(), transform(length()).check(greaterThan(0))));
    System.out.println(message);
  }

  @Test
  public void test2() {
    String identifier = "0001";
    MemberDatabase database = new MemberDatabase();
    Function<String, Function<MemberDatabase, MemberDatabase.Member>> lookUpMemberWith =
        id -> Printables.function(
            () -> format("lookUpMember[%s]", id),
            d -> d.lookUp(id).orElseThrow(NoSuchElementException::new));
    Function<MemberDatabase.Member, String> memberLastName =
        Printables.function("memberLastName", MemberDatabase.Member::lastName);

    assertThat(
        database,
        transform(lookUpMemberWith.apply(identifier).andThen(memberLastName)).check(allOf(
            isNotNull(),
            not(isEmptyString()),
            isEqualTo("Do")
        )));
  }

  @Test
  public void givenKnownLastName_whenFindMembersByFullName_thenLastNastIsNotNullAndContainedInFullName() {
    MemberDatabase database = new MemberDatabase();
    String lastName = database.lookUp("0001")
        .orElseThrow(NoSuchElementException::new)
        .lastName();
    List<String> fullName = database.findMembersByLastName(lastName).get(0).toFullName();
    assertThat(
        asList(lastName, fullName),
        allOf(
            transform(elementAt(0).andThen(cast(String.class))).check(allOf(isNotNull(), not(isEmptyString()))),
            transform(elementAt(1).andThen(castTo((List<String>)value()))).check(Predicates.contains(lastName))));
  }
}
