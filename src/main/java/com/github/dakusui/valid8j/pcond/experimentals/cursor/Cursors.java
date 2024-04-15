package com.github.dakusui.valid8j.pcond.experimentals.cursor;

import com.github.dakusui.valid8j.pcond.core.Evaluator;
import com.github.dakusui.valid8j.pcond.core.Evaluable;
import com.github.dakusui.valid8j.pcond.core.printable.PrintablePredicate;
import com.github.dakusui.valid8j.pcond.forms.Predicates;
import com.github.dakusui.valid8j.pcond.forms.Printables;
import com.github.dakusui.valid8j.pcond.internals.InternalUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static com.github.dakusui.valid8j.pcond.forms.Printables.function;
import static com.github.dakusui.valid8j.pcond.forms.Printables.predicate;
import static com.github.dakusui.valid8j.pcond.internals.InternalUtils.formatObject;
import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public enum Cursors {
  ;
  
  /**
   * Note that a predicate returned by this method is stateful and not to be re-used.
   *
   * @param locatorFactory A function to return a cursor which points the location where a given token appears in an original string.
   * @param tokens         Tokens to be found in a given string passed to the returned predicate.
   * @param <T>            The type of token to be searched for.
   * @return A predicate that checks if `tokens` are all contained in a given string
   * in the order, where they appear in the argument.
   */
  @SuppressWarnings("unchecked")
  static <T> Predicate<String> findTokens(Function<T, Function<String, Cursor>> locatorFactory, T... tokens) {
    AtomicBoolean result = new AtomicBoolean(true);
    AtomicInteger lastTestedPosition = new AtomicInteger(0);
    StringBuilder bExpectation = new StringBuilder();
    StringBuilder bActual = new StringBuilder();
    class CursoredString implements Evaluator.Snapshottable {
      public int previousFailingPosition;
      String originalString;
      int    position;

      CursoredString(String originalString) {
        this.originalString = originalString;
        this.position = 0;
      }

      CursoredString findNext(T token) {
        Function<String, Cursor> locator = locatorFactory.apply(token);
        Cursor cursor = locator.apply(originalString.substring(this.position));
        if (cursor.position >= 0) {
          updateOngoingExplanation(bExpectation, token, cursor, (lf, t) -> "found for:" + locatorFactory + "[" + t + "]");
          updateOngoingExplanation(bActual, token, cursor, (lf, t) -> "found for:" + locatorFactory + "[" + t + "]");

          this.position += cursor.position + cursor.length;
        } else {
          this.previousFailingPosition = this.position;
        }
        lastTestedPosition.set(this.position);
        return this;
      }

      private void updateOngoingExplanation(StringBuilder b, T token, Cursor cursor, BiFunction<Object, T, String> locatorFactoryFormatter) {
        b.append(this.originalString, this.position, this.position + cursor.position);
        b.append("<");
        b.append(formatObject(this.originalString.substring(this.position + cursor.position, this.position + cursor.position + cursor.length)));
        b.append(":");
        b.append(locatorFactoryFormatter.apply(locatorFactory, token));
        b.append(">");
      }

      @Override
      public Object snapshot() {
        return originalString.substring(position);
      }

      @Override
      public String toString() {
        return "CursoredString:[" + originalString + "]";
      }
    }
    CursoredString cursoredStringForSnapshotting = new CursoredString(null);
    class CursoredStringPredicate extends PrintablePredicate<CursoredString> implements
        Predicate<CursoredString>,
        Evaluable.LeafPred<CursoredString>,
        Evaluator.Explainable {
      final T each;

      CursoredStringPredicate(T each) {
        super(new Object(), emptyList(), () -> "findTokenBy[" + locatorFactory + "[" + each + "]]", cursoredString -> {
          cursoredStringForSnapshotting.previousFailingPosition = cursoredString.previousFailingPosition;
          cursoredStringForSnapshotting.position = cursoredString.position;
          cursoredStringForSnapshotting.originalString = cursoredString.originalString;
          return cursoredString.position != cursoredString.findNext(each).position;
        });
        this.each = each;
      }

      @Override
      public boolean test(CursoredString v) {
        boolean ret = super.test(v);
        result.set(ret && result.get());
        return ret;
      }

      @Override
      public String toString() {
        return "findTokenBy[" + locatorFactoryName() + "]";
      }

      private String locatorFactoryName() {
        return locatorFactory + "[" + each + "]";
      }

      @Override
      public Predicate<? super CursoredString> predicate() {
        return this;
      }


      @Override
      public Object explainOutputExpectation() {
        return formatExplanation(bExpectation, "SHOULD BE FOUND AFTER THIS POSITION");
      }

      @Override
      public Object explainActual(Object actualValue) {
        return formatExplanation(bActual, "BUT NOT FOUND");
      }

      private String formatExplanation(StringBuilder b, String keyword) {
        String ret = b.toString() + format("%n") + "<" + this.locatorFactoryName() + ":" + keyword + ">";
        b.delete(0, b.length());
        return ret;
      }
    }
    return Predicates.transform(function("findTokens" + formatObject(tokens), CursoredString::new))
        .check(Predicates.allOf(
            Stream.concat(
                    Arrays.stream(tokens).map(CursoredStringPredicate::new),
                    Stream.of(endMarkPredicateForString(lastTestedPosition, bExpectation, bActual, result, () -> cursoredStringForSnapshotting.originalString)))
                .toArray(Predicate[]::new)));

  }
  
  private static Predicate<Object> endMarkPredicateForString(AtomicInteger lastTestedPosition, StringBuilder ongoingExpectationExplanation, StringBuilder ongoingActualExplanation, AtomicBoolean result, Supplier<String> originalStringSupplier) {
    return makeExplainable((PrintablePredicate<? super Object>) predicate("(end)", v -> result.get()), new Evaluator.Explainable() {

      @Override
      public Object explainOutputExpectation() {
        return ongoingExpectationExplanation.toString() + originalStringSupplier.get().substring(lastTestedPosition.get());
      }

      @Override
      public Object explainActual(Object actualValue) {
        return ongoingActualExplanation.toString() + originalStringSupplier.get().substring(lastTestedPosition.get());
      }
    });
  }
  
  private static <T> Predicate<T> makeExplainable(PrintablePredicate<? super T> p, Evaluator.Explainable explainable) {
    class ExplainablePredicate extends PrintablePredicate<T> implements
        Predicate<T>,
        Evaluable.LeafPred<T>,
        Evaluator.Explainable {

      protected ExplainablePredicate() {
        super(new Object(), emptyList(), p::toString, p);
      }

      @Override
      public Predicate<? super T> predicate() {
        return predicate;
      }

      @Override
      public Object explainOutputExpectation() {
        return explainable.explainOutputExpectation();
      }

      @Override
      public Object explainActual(Object actualValue) {
        return explainable.explainActual(actualValue);
      }
    }

    return new ExplainablePredicate();
  }
  
  public static Predicate<String> findSubstrings(String... tokens) {
    return findTokens(Printables.function("substring", token -> string -> new Cursor(string.indexOf(token), token.length())), tokens);
  }
  
  public static Predicate<String> findRegexPatterns(Pattern... patterns) {
    return findTokens(function("matchesRegex", token -> string -> {
      java.util.regex.Matcher m = token.matcher(string);
      if (m.find()) {
        return new Cursor(m.start(), m.end() - m.start());
      } else
        return new Cursor(-1, 0);

    }), patterns);
  }
  
  public static Predicate<String> findRegexes(String... regexes) {
    return findRegexPatterns(Arrays.stream(regexes).map(Pattern::compile).toArray(Pattern[]::new));
  }
  
  @SuppressWarnings("unchecked")
  @SafeVarargs
  public static <E> Predicate<List<E>> findElements(Predicate<? super E>... predicates) {
    AtomicBoolean result = new AtomicBoolean(true);
    List<Object> expectationExplanationList = new LinkedList<>();
    List<Object> actualExplanationList = new LinkedList<>();
    List<Object> rest = new LinkedList<>();
    AtomicInteger previousPosition = new AtomicInteger(0);
    Function<Predicate<? super E>, Predicate<CursoredList<E>>> predicatePredicateFunction = (Predicate<? super E> p) -> (Predicate<CursoredList<E>>) cursoredList -> {
      AtomicInteger j = new AtomicInteger(0);
      boolean isFound = cursoredList.currentList().stream()
          .peek((E each) -> j.getAndIncrement())
          .anyMatch(p);
      if (isFound) {
        updateExplanationsForFoundElement(
            expectationExplanationList, actualExplanationList,
            cursoredList.currentList().get(j.get() - 1),
            p, (List<Object>) cursoredList.currentList().subList(0, j.get() - 1));
        rest.clear();
        rest.add(cursoredList.currentList().subList(j.get(), cursoredList.currentList().size()));
        cursoredList.position += j.get();
        previousPosition.set(cursoredList.position);
        return true;
      }
      updateExplanationsForMissedPredicateIfCursorMoved(
          expectationExplanationList, actualExplanationList,
          cursoredList.position > previousPosition.get(),
          p, cursoredList.currentList().subList(0, j.get()));
      result.set(false);
      previousPosition.set(cursoredList.position);
      return false;
    };
    return Predicates.transform(function("toCursoredList", (List<E> v)-> new CursoredList<>(v)))
        .check(Predicates.allOf(Stream.concat(
                Arrays.stream(predicates)
                    .map((Predicate<? super E> each) -> predicate("findElementBy[" + each + "]", predicatePredicateFunction.apply(each))),
                Stream.of(endMarkPredicateForList(result, expectationExplanationList, actualExplanationList, rest)))
            .toArray(Predicate[]::new)));
  }
  
  private static <E> void updateExplanationsForFoundElement(List<Object> expectationExplanationList, List<Object> actualExplanationList, E foundElement, Predicate<? super E> matchedPredicate, List<Object> skippedElements) {
    if (!skippedElements.isEmpty()) {
      //      expectationExplanationList.add(skippedElements);
      actualExplanationList.add(skippedElements);
    }
    actualExplanationList.add(new Explanation(foundElement, "<%s:found for:" + matchedPredicate + ">"));
    expectationExplanationList.add(new Explanation(matchedPredicate, "<matching element for:%s>"));
  }
  
  private static <E> void updateExplanationsForMissedPredicateIfCursorMoved(List<Object> expectationExplanationList, List<Object> actualExplanationList, boolean isCursorMoved, Predicate<? super E> missedPredicate, List<E> scannedElements) {
    if (isCursorMoved) {
      //expectationExplanationList.add(scannedElements);
      actualExplanationList.add(scannedElements);
    }
    Explanation missedInExpectation = new Explanation(missedPredicate, "<matching element for:%s>");
    expectationExplanationList.add(missedInExpectation);

    Explanation missedInActual = new Explanation(missedPredicate, "<NOT FOUND:matching element for:%s>");
    actualExplanationList.add(missedInActual);
  }
  
  private static Predicate<Object> endMarkPredicateForList(AtomicBoolean result, List<Object> expectationExplanationList, List<Object> actualExplanationList, List<?> rest) {
    return makeExplainable((PrintablePredicate<? super Object>) predicate("(end)", v -> result.get()), new Evaluator.Explainable() {

      @Override
      public Object explainOutputExpectation() {
        return renderExplanationString(expectationExplanationList);
      }

      @Override
      public Object explainActual(Object actualValue) {
        return renderExplanationString(createFullExplanationList(actualExplanationList, rest));
      }

      private List<Object> createFullExplanationList(List<Object> explanationList, List<?> rest) {
        return Stream.concat(explanationList.stream(), rest.stream()).collect(toList());
      }

      private String renderExplanationString(List<Object> fullExplanationList) {
        return fullExplanationList
            .stream()
            .map(e -> {
              if (e instanceof List) {
                return String.format("<%s:skipped>",
                    ((List<?>) e).stream()
                        .map(InternalUtils::formatObject)
                        .collect(joining(",")));
              }
              return e;
            })
            .map(Object::toString)
            .collect(joining(String.format("%n")));
      }
    });
  }
  
  static class Cursor {
    /**
     * The "relative" position, where the token was found, from the beginning of the string passed to a locator.
     * By convention, it is designed to pass a substring of the original string, which starts from the position,
     * where a token (element) searching attempt was made.
     */
    final int position;
    /**
     * A length of a token to be searched.
     */
    final int length;

    Cursor(int position, int length) {
      this.position = position;
      this.length = length;
    }
  }
  
  static class CursoredList<EE> extends AbstractList<EE> implements Evaluator.Snapshottable, Collection<EE> {
    int position;
    final List<EE> originalList;

    CursoredList(List<EE> originalList) {
      this.originalList = originalList;
    }

    List<EE> currentList() {
      return this.originalList.subList(position, this.originalList.size());
    }

    @Override
    public Object snapshot() {
      return originalList.subList(position, originalList.size());
    }

    @Override
    public int size() {
      return originalList.size() - position;
    }

    @Override
    public EE get(int index) {
      return originalList.get(position + index);
    }

    @Override
    public String toString() {
      return "CursoredList:" + originalList;
    }
  }
  
  private static class Explanation {
    final         Object value;
    private final String formatString;

    private Explanation(Object value, String formatString) {
      this.value = value;
      this.formatString = formatString;
    }

    @Override
    public String toString() {
      return format(formatString, formatObject(this.value));
    }
  }
}
