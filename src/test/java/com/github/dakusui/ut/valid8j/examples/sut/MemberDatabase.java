package com.github.dakusui.ut.valid8j.examples.sut;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

public class MemberDatabase {
  public Optional<Member> lookUp(String id) {
    return MEMBERS.stream().filter(each -> Objects.equals(id, each.id())).findFirst();
  }

  public List<Member> findMembersByLastName(String lastName) {
    return findMembersByPredicate(each -> Objects.equals(lastName, each.lastName()));
  }

  List<Member> findMembersByFirstName(String firstName) {
    return findMembersByPredicate(each -> Objects.equals(firstName, each.firstName()));
  }

  private List<Member> findMembersByPredicate(Predicate<Member> p) {
    return MEMBERS.stream()
        .filter(p)
        .collect(Collectors.toList());
  }

  private static final List<Member> MEMBERS = asList(
      new Member("0001", "John", "Doe", "PhD"),
      new Member("0002", "Scott", "Tiger"),
      new Member("0003", "Taro", "Yamada"),
      new Member("0004", "John", "Tiger"),
      new Member("0005", "Jane", "Doe"),
      new Member("0006", "Charles", "de Gaulle"),
      new Member("0007", "Hanako", "Yamada", "PhD"),
      new Member("0008", "Joe", "Biden"),
      new Member("0009", "Joe", "Shmoe"),
      new Member("0010", "Joe", "Sixpack"),
      new Member("0011", "Woodrow", "Wilson", "PhD", "President of the United States"),
      new Member("0012", "Joshua", "Norton", "Emperor of the United State", "Protector of Mexico"));

  public static class Member {
    private final String       id;
    private final List<String> titles;
    private final String       firstName;
    private final String       lastName;

    Member(String id, String firstName, String lastName, String... titles) {
      this.id = id;
      this.firstName = firstName;
      this.lastName = lastName;
      this.titles = asList(titles);
    }

    String id() {
      return this.id;
    }

    String firstName() {
      return this.firstName;
    }

    public String lastName() {
      return this.lastName;
    }

    List<String> titles() {
      return this.titles;
    }

    public List<String> toFullName() {
      return Stream.concat(Stream.of(firstName(), lastName()), titles().stream()).collect(toList());
    }
  }
}
