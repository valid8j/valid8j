package com.github.dakusui.valid8j.pcond.core.fluent.builtins;

import com.github.dakusui.valid8j.pcond.core.fluent.AbstractObjectChecker;
import com.github.dakusui.valid8j.pcond.experimentals.cursor.Cursors;
import com.github.dakusui.valid8j.pcond.forms.Predicates;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import static com.github.dakusui.valid8j.pcond.core.printable.ExplainablePredicate.explainableStringIsEqualTo;
import static com.github.dakusui.valid8j.pcond.internals.InternalUtils.trivialIdentityFunction;

public interface StringChecker<T> extends
    AbstractObjectChecker<
                    StringChecker<T>,
                    T,
                    String> {
  /**
   * Checks if the given token is contained by the target value.
   * @param token A token to be contained by the target value.
   * @return This object.
   */
  default StringChecker<T> containing(String token) {
    return this.checkWithPredicate(Predicates.containsString(token));
  }

  /**
   * Checks if given regular expressions are contained by the target value in the given order.
   *
   * @param regexes Regular expression patterns to be contained by the target value.
   * @return This object.
   */
  default StringChecker<T> containingRegexes(String... regexes) {
    return this.checkWithPredicate(Cursors.findRegexes(regexes));
  }

  /**
   * Checks if given tokens are contained by the target value in the given order.
   *
   * @param patterns Regular expression patterns to be contained by the target value.
   * @return This object.
   */
  default StringChecker<T> containingRegexes(Pattern... patterns) {
    return this.checkWithPredicate(Cursors.findRegexPatterns(patterns));
  }

  /**
   * Checks if given tokens are contained by the target value in the given order.
   *
   * @param tokens Tokens to be contained by the target value.
   * @return This object.
   */
  default StringChecker<T> containingSubstrings(String... tokens) {
    return this.checkWithPredicate(Cursors.findSubstrings(tokens));
  }

  default StringChecker<T> startingWith(String prefix) {
    return this.checkWithPredicate(Predicates.startsWith(prefix));
  }

  default StringChecker<T> endingWith(String prefix) {
    return this.checkWithPredicate(Predicates.endsWith(prefix));
  }

  default StringChecker<T> empty() {
    return this.checkWithPredicate(Predicates.isEmptyString());
  }

  default StringChecker<T> notEmpty() {
    return this.checkWithPredicate(Predicates.isEmptyString().negate());
  }

  default StringChecker<T> equalTo(String string) {
    return this.checkWithPredicate(explainableStringIsEqualTo(string));
  }

  default StringChecker<T> nullOrEmpty() {
    return this.checkWithPredicate(Predicates.isNullOrEmptyString());
  }

  default StringChecker<T> matchingRegex(String regex) {
    return this.checkWithPredicate(Predicates.matchesRegex(regex));
  }

  default StringChecker<T> equalToIgnoringCase(String s) {
    return this.checkWithPredicate(Predicates.equalsIgnoreCase(s));
  }

  @SuppressWarnings("unchecked")
  default StringChecker<T> check(Function<StringChecker<String>, Predicate<String>> phrase) {
    return this.addCheckPhrase(v -> phrase.apply((StringChecker<String>) v));
  }

  class Impl<T>
      extends
      Base<StringChecker<T>, T, String>
      implements
      StringChecker<T> {
    protected Impl(Supplier<T> rootValue, Function<T, String> transformFunction) {
      super(rootValue, transformFunction);
    }

    @Override
    public StringChecker<String> rebase() {
      return new Impl<>(this::value, trivialIdentityFunction());
    }
  }
}
