package com.github.valid8j.pcond.core.printable;

import com.github.valid8j.pcond.core.Evaluable;
import com.github.valid8j.pcond.core.Evaluator;
import com.github.valid8j.pcond.core.identifieable.Identifiable;
import com.github.valid8j.pcond.experimentals.currying.context.CurriedContext;
import com.github.valid8j.pcond.internals.InternalUtils;
import com.github.valid8j.pcond.forms.Predicates;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public enum PrintablePredicateFactory {
  NEGATION,
  CONJUNCTION,
  DISJUNCTION,
  LEAF,
  ;

  private static <T> PrintablePredicate<T> toPrintablePredicateIfNotPrintable(Predicate<T> predicate) {
    return (PrintablePredicate<T>) toLeafIfNotPrintable(predicate);
  }

  public static <T> Predicate<T> toLeafIfNotPrintable(Predicate<T> predicate) {
    if (!(predicate instanceof PrintablePredicate))
      return leaf(Identifiable.formatObjectName(predicate), predicate);
    return predicate;
  }

  public static <T> Predicate<T> leaf(String name, Predicate<T> predicate) {
    return leaf(() -> name, predicate);
  }

  public static <T> Predicate<T> leaf(Supplier<String> formatter, Predicate<T> predicate) {
    return leaf(formatter, predicate, PrintablePredicateFactory.class);
  }

  public static <T> Predicate<T> leaf(Supplier<String> formatter, Predicate<T> predicate, Object fallbackCreator) {
    return parameterizedLeaf(
        (args) -> formatter,
        (args) -> predicate,
        emptyList(),
        fallbackCreator);
  }

  public static <T> Predicate<T> parameterizedLeaf(
      Function<List<Object>, Supplier<String>> formatterFactory,
      Function<List<Object>, Predicate<T>> predicateFactory,
      List<Object> args,
      Object fallbackCreator
  ) {
    Supplier<String> formatter = formatterFactory.apply(args);
    Predicate<T> predicate = predicateFactory.apply(args);
    return Identifiable.creatorOf(predicate)
        .map(c -> new LeafPredicate<>(c, Identifiable.argsOf(predicate), formatter, predicate))
        .orElse(new LeafPredicate<>(fallbackCreator, args, formatter, predicate));
  }

  public static <P, O> TransformingPredicate.Factory<P, O> transform(Function<O, P> function) {
    return TransformingPredicate.Factory.create(function);
  }

  public static <T> Conjunction<T> and(List<Predicate<? super T>> predicates) {
    return new Conjunction<T>(predicates, true);
  }

  public static <T> Disjunction<T> or(List<Predicate<? super T>> predicates) {
    return new Disjunction<T>(predicates, true);
  }

  public static <T> Conjunction<T> allOf(List<Predicate<? super T>> predicates) {
    return new Conjunction<T>(predicates, false);
  }

  public static <T> Disjunction<T> anyOf(List<Predicate<? super T>> predicates) {
    return new Disjunction<T>(predicates, false);
  }

  public static <T> Predicate<T> not(Predicate<T> predicate) {
    return not_(predicate);
  }

  @SuppressWarnings("ClassEscapesDefinedScope")
  public static <T> Negation<T> not_(Predicate<T> predicate) {
    return new Negation<T>(toPrintablePredicateIfNotPrintable(predicate), singletonList(predicate));
  }

  public static <E> Predicate<Stream<E>> allMatch(Predicate<E> predicate) {
    return AllMatch.create(predicate);
  }

  public static <E> Predicate<Stream<E>> noneMatch(Predicate<E> predicate) {
    return NoneMatch.create(predicate);
  }

  public static <E> Predicate<Stream<E>> anyMatch(Predicate<E> predicate) {
    return AnyMatch.create(predicate);
  }

  public static <T> Predicate<CurriedContext> variableBundlePredicate(Predicate<T> predicate, int argIndex) {
    return CurriedContextPredicate.create(toPrintablePredicateIfNotPrintable(predicate), argIndex);
  }

  private static RuntimeException noPredicateGiven() {
    throw new IllegalArgumentException("No predicate was given");
  }

  /*
      Expected: "100 -> &&                                     ->   false"
       but: was "100 -> &&                                            ->   false"
   */
  public enum Leaf {
    ALWAYS_TRUE("alwaysTrue", v -> true),
    IS_TRUE("isTrue", (Boolean v) -> v),
    IS_FALSE("isFalse", (Boolean v) -> !v),
    IS_NULL("isNull", Objects::isNull),
    IS_NOT_NULL("isNotNull", Objects::nonNull),
    IS_EMPTY_STRING("isEmpty", v -> ((String) v).isEmpty()),
    IS_NULL_OR_EMPTY_STRING("isNullOrEmptyString", v -> v == null || ((String) v).isEmpty()),
    IS_EMPTY_ARRAY("isEmptyArray", objects -> ((Object[]) objects).length == 0),
    IS_EMPTY_COLLECTION("isEmpty", v -> ((Collection<?>) v).isEmpty()),
    ;
    final Predicate<?> instance;

    @SuppressWarnings("unchecked")
    Leaf(String s, Predicate<?> predicate) {
      this.instance = leaf(() -> s, (Predicate<? super Object>) predicate, this);
    }

    @SuppressWarnings("unchecked")
    public <T> Predicate<T> instance() {
      return (Predicate<T>) this.instance;
    }
  }

  public enum ParameterizedLeafFactory {
    IS_EQUAL_TO(
        (args) -> () -> format("isEqualTo[%s]", args.get(0)),
        (args) -> v -> Objects.equals(v, args.get(0))),
    @SuppressWarnings("unchecked") GREATER_THAN(
        (args) -> () -> format(">[%s]", args.get(0)),
        (args) -> v -> ((Comparable<? super Comparable<?>>) v).compareTo((Comparable<? super Comparable<?>>) args.get(0)) > 0),
    @SuppressWarnings("unchecked") GREATER_THAN_OR_EQUAL_TO(
        (args) -> () -> format(">=[%s]", args.get(0)),
        (args) -> v -> ((Comparable<? super Comparable<?>>) v).compareTo((Comparable<? super Comparable<?>>) args.get(0)) >= 0),
    @SuppressWarnings("unchecked") LESS_THAN_OR_EQUAL_TO(
        (args) -> () -> format("<=[%s]", args.get(0)),
        (args) -> v -> ((Comparable<? super Comparable<?>>) v).compareTo((Comparable<? super Comparable<?>>) args.get(0)) <= 0),
    @SuppressWarnings("unchecked") LESS_THAN(
        (args) -> () -> format("<[%s]", args.get(0)),
        (args) -> v -> ((Comparable<? super Comparable<?>>) v).compareTo((Comparable<? super Comparable<?>>) args.get(0)) < 0),
    @SuppressWarnings("unchecked") EQUAL_TO(
        (args) -> () -> format("=[%s]", args.get(0)),
        (args) -> v -> ((Comparable<? super Comparable<?>>) v).compareTo((Comparable<? super Comparable<?>>) args.get(0)) == 0),
    MATCHES_REGEX(
        (args) -> () -> String.format("matchesRegex[%s]", args.get(0)),
        (args) -> (s) -> ((String) s).matches((String) args.get(0))),
    CONTAINS_STRING(
        (args) -> () -> format("containsString[%s]", args.get(0)),
        (args) -> (s) -> ((String) s).contains((String) args.get(0))),
    STARTS_WITH(
        (args) -> () -> format("startsWith[%s]", args.get(0)),
        (args) -> (s) -> ((String) s).startsWith((String) args.get(0))),
    ENDS_WITH(
        (args) -> () -> format("endsWith[%s]", args.get(0)),
        (args) -> (s) -> ((String) s).endsWith((String) args.get(0))),
    EQUALS_IGNORE_CASE(
        (args) -> () -> format("equalsIgnoreCase[%s]", args.get(0)),
        (args) -> (s) -> {
          return ((String) s).equalsIgnoreCase((String) args.get(0));
        }),
    OBJECT_IS_SAME_AS(
        arg -> () -> format("==[%s]", arg.get(0)),
        args -> v -> v == args.get(0)),
    CONTAINS(
        (args) -> () -> format("contains[%s]", args.get(0)),
        (args) -> (c) -> ((Collection<?>) c).contains(args.get(0)));
    private final Function<List<Object>, Predicate<Object>> predicateFactory;
    private final Function<List<Object>, Supplier<String>>  formatterFactory;

    ParameterizedLeafFactory(Function<List<Object>, Supplier<String>> formatterFactory, Function<List<Object>, Predicate<Object>> predicateFactory) {
      this.predicateFactory = predicateFactory;
      this.formatterFactory = formatterFactory;
    }

    Function<List<Object>, Supplier<String>> formatterFactory() {
      return this.formatterFactory;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    <T> Function<List<Object>, Predicate<T>> functionFactory() {
      return (Function) this.predicateFactory;
    }

    public static <T> Predicate<T> create(ParameterizedLeafFactory parameterizedLeafFactory, List<Object> args) {
      return parameterizedLeaf(
          parameterizedLeafFactory.formatterFactory(), parameterizedLeafFactory.functionFactory(), args, parameterizedLeafFactory
      );
    }
  }

  private static class LeafPredicate<T> extends PrintablePredicate<T> implements Evaluable.LeafPred<T>, Evaluator.Explainable {
    protected LeafPredicate(Object creator, List<Object> args, Supplier<String> formatter, Predicate<? super T> predicate) {
      super(creator, args, formatter, predicate);
    }

    @Override
    public Predicate<? super T> predicate() {
      return predicate;
    }

    @Override
    public Object explainOutputExpectation() {
      return this.formatter.get();
    }

    @Override
    public Object explainActual(Object actualValue) {
      return actualValue;
    }

    @Override
    public String toString() {
      return InternalUtils.formatObject(InternalUtils.toNonStringObject(this.formatter.get()));
    }
  }

  static class Negation<T> extends PrintablePredicate<T> implements Evaluable.Negation<T> {
    final Evaluable<T> target;

    @SuppressWarnings("unchecked")
    protected Negation(Predicate<T> predicate, List<Object> args) {
      super(
          NEGATION,
          args,
          () -> format("!%s", predicate),
          (t) -> PrintablePredicate.unwrap((Predicate<Object>) predicate).negate().test(t));
      this.target = (Evaluable<T>) InternalUtils.toEvaluableIfNecessary(predicate);
      this.squashable = true;
    }

    @Override
    public Evaluable<T> target() {
      return target;
    }
  }

  public static class Conjunction<T> extends Junction<T> implements Evaluable.Conjunction<T> {
    protected Conjunction(List<Predicate<? super T>> predicates, boolean shortcut) {
      super(
          predicates,
          CONJUNCTION,
          "&&",
          Predicate::and,
          shortcut);
    }
  }

  public static class Disjunction<T> extends Junction<T> implements Evaluable.Disjunction<T> {
    protected Disjunction(List<Predicate<? super T>> predicates, boolean shortcut) {
      super(
          predicates,
          DISJUNCTION,
          "||",
          Predicate::or,
          shortcut);
    }
  }

  abstract static class Junction<T> extends PrintablePredicate<T> implements Evaluable.Composite<T> {
    final         List<Evaluable<T>> children;
    final private boolean            shortcut;

    @SuppressWarnings("unchecked")
    protected Junction(
        List<Predicate<? super T>> predicates,
        PrintablePredicateFactory creator,
        String junctionSymbol,
        BinaryOperator<Predicate<T>> junctionOp, boolean shortcut) {
      super(
          creator,
          new ArrayList<>(predicates),
          () -> formatJunction(predicates, junctionSymbol),
          junction(predicates, junctionOp));
      this.children = predicates.stream()
          .map(InternalUtils::toEvaluableIfNecessary)
          .map(each -> (Evaluable<T>) each)
          .collect(toList());
      this.shortcut = shortcut;
    }

    @Override
    public List<Evaluable<T>> children() {
      return this.children;
    }

    @Override
    public boolean shortcut() {
      return this.shortcut;
    }

    public List<Predicate<T>> childPredicates() {
      return childrenOfJunction(this);
    }

    static <T> String formatJunction(List<Predicate<? super T>> predicates, String junctionSymbol) {
      return predicates.stream()
          .map(PrintablePredicateFactory::toPrintablePredicateIfNotPrintable)
          .map(Object::toString)
          .collect(joining(junctionSymbol, "(", ")"));
    }

    @SuppressWarnings("unchecked")
    static <T> Predicate<T> junction(List<Predicate<? super T>> predicates_, BinaryOperator<Predicate<T>> junctionOp) {
      return predicates_
          .stream()
          .map(PrintablePredicate::unwrap)
          .map(p -> (Predicate<T>) p)
          .reduce(junctionOp)
          .orElseThrow(PrintablePredicateFactory::noPredicateGiven);
    }

    static <T> List<Predicate<T>> childrenOfJunction(Junction<T> junction) {
      return evaluablesToPredicates(junction.children);
    }

    @SuppressWarnings("unchecked")
    private static <T> List<Predicate<T>> evaluablesToPredicates(List<Evaluable<T>> evaluables) {
      return evaluables.stream()
          .peek(each -> {
            assert each instanceof Predicate;
          })
          .map(each -> (Predicate<T>) each)
          .collect(toList());
    }
  }

  /**
   * This is an interface that corresponds to a "matcher" in other assertion
   * libraries.
   */
  public static class TransformingPredicate<T, R> extends PrintablePredicate<T> implements Evaluable.Transformation<T, R> {
    private final Evaluable<? super T> mapper;
    private final Evaluable<? super R> checker;
    private final String               mapperName;
    private final String               checkerName;

    @SuppressWarnings("unchecked")
    public TransformingPredicate(String mapperName, String checkerName, Predicate<? super R> predicate, Function<? super T, ? extends R> function) {
      super(
          TransformingPredicate.class,
          asList(predicate, function),
          () -> mapperName == null ?
              format("%s %s", function, checkerName == null ? predicate : (checkerName + ":" + predicate.toString())) :
              format("%s(%s %s)", mapperName, function, checkerName == null ? predicate : checkerName + ":" + predicate.toString()),
          v -> predicate.test(function.apply(v)));
      this.mapper = (Evaluable<? super T>) InternalUtils.toEvaluableIfNecessary(function);
      this.mapperName = mapperName;
      this.checker = (Evaluable<? super R>) InternalUtils.toEvaluableIfNecessary(predicate);
      this.checkerName = checkerName;
      this.squashable = true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Evaluable<T> mapper() {
      return (Evaluable<T>) this.mapper;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Evaluable<R> checker() {
      return (Evaluable<R>) this.checker;
    }

    @Override
    public Optional<String> mapperName() {
      return Optional.ofNullable(this.mapperName);
    }

    @Override
    public Optional<String> checkerName() {
      return Optional.ofNullable(this.checkerName);
    }

    /**
     * This is an interface that corresponds to a "matcher" in other assertion
     * libraries.
     *
     * @param <P> Intermediate parameter type tested by a predicated.
     * @param <O> Input parameter type.
     */
    public static abstract class Factory<P, O> {
      public abstract Predicate<O> check(String condName, Predicate<? super P> cond);

      @SuppressWarnings("unchecked")
      public <OO> Factory<P, OO> castTo(@SuppressWarnings("unused") Class<OO> ooClass) {
        return (Factory<P, OO>) this;
      }

      public abstract Predicate<O> check(Predicate<? super P> cond);

      @SafeVarargs
      public final Predicate<O> checkAllOf(Predicate<? super P>... conds) {
        return check(Predicates.allOf(conds));
      }
      @SafeVarargs
      public final Predicate<O> allOf(Predicate<? super P>... conds) {
        return checkAllOf(conds);
      }

      public static <P, O> Factory<P, O> create(Function<O, P> function) {
        return create(null, null, function);
      }

      public static <P, O> Factory<P, O> create(String mapperName, String checkerName, Function<O, P> function) {
        return new Factory<P, O>() {
          @Override
          public Predicate<O> check(String condName, Predicate<? super P> cond) {
            return new TransformingPredicate<>(mapperName, condName, cond, function);
          }

          @Override
          public Predicate<O> check(Predicate<? super P> cond) {
            return new TransformingPredicate<>(mapperName, checkerName, toPrintablePredicateIfNotPrintable(cond), function);
          }
        };
      }
    }
  }

  static class CurriedContextPredicate extends PrintablePredicate<CurriedContext> implements Evaluable.CurriedContextPred {
    private final Evaluable<?> enclosed;
    private final int          argIndex;

    private <T> CurriedContextPredicate(Object creator, List<Object> args, Predicate<T> predicate, int argIndex) {
      super(
          creator,
          args,
          () -> format("curry[%s,%s]", predicate, argIndex),
          context -> PrintablePredicate.unwrap(predicate).test(context.valueAt(argIndex)));
      this.enclosed = (Evaluable<?>) InternalUtils.toEvaluableIfNecessary(predicate);
      this.argIndex = argIndex;

    }

    @SuppressWarnings("unchecked")
    @Override
    public <TT> Evaluable<TT> enclosed() {
      return (Evaluable<TT>) this.enclosed;
    }

    @Override
    public int argIndex() {
      return argIndex;
    }

    public static <T> CurriedContextPredicate create(Predicate<T> predicate, int argIndex) {
      return new CurriedContextPredicate(CurriedContextPredicate.class, asList(predicate, argIndex), predicate, argIndex);
    }
  }

  static class AllMatch<E> extends StreamPredicate<E> {
    @SuppressWarnings("unchecked")
    private AllMatch(Predicate<? super E> predicate) {
      super(
          AllMatch.class,
          singletonList(predicate),
          () -> format("allMatch[%s]", predicate),
          (Stream<E> stream) -> stream.allMatch(predicate),
          InternalUtils.toEvaluableIfNecessary((Predicate<? super Stream<? extends E>>) predicate),
          true,
          false);
    }

    static <E> StreamPredicate<E> create(Predicate<? super E> predicate) {
      return new AllMatch<>(
          predicate
      );
    }
  }

  static class AnyMatch<E> extends StreamPredicate<E> {
    @SuppressWarnings("unchecked")
    private AnyMatch(Predicate<? super E> predicate) {
      super(
          AnyMatch.class,
          singletonList(predicate),
          () -> format("anyMatch[%s]", predicate),
          (Stream<E> stream) -> stream.anyMatch(PrintablePredicate.unwrap(predicate)),
          (Evaluable<Stream<E>>) InternalUtils.toEvaluableIfNecessary(predicate),
          false,
          true);
    }

    public static <E> StreamPredicate<E> create(Predicate<? super E> predicate) {
      return new AnyMatch<>(
          predicate
      );
    }
  }

  static class NoneMatch<E> extends StreamPredicate<E> {
    @SuppressWarnings("unchecked")
    private NoneMatch(Predicate<? super E> predicate) {
      super(
          NoneMatch.class,
          singletonList(predicate),
          () -> format("noneMatch[%s]", predicate),
          (Stream<E> stream) -> stream.noneMatch(predicate),
          (Evaluable<Stream<E>>) InternalUtils.toEvaluableIfNecessary((Predicate<? super Stream<E>>) predicate),
          true,
          true);
    }

    @Override
    public boolean requestExpectationFlip() {
      return true;
    }

    public static <E> StreamPredicate<E> create(Predicate<E> predicate) {
      return new NoneMatch<E>(
          predicate
      ) {
      };
    }
  }

  public abstract static class StreamPredicate<E> extends PrintablePredicate<Stream<E>> implements Evaluable.StreamPred<E> {
    private final Evaluable<Stream<E>> cut;
    private final boolean              defaultValue;
    private final boolean              cutOn;

    private StreamPredicate(Object creator, List<Object> args, Supplier<String> formatter, Predicate<Stream<E>> predicate, Evaluable<Stream<E>> cut, boolean defaultValue, boolean cutOn) {
      super(creator, args, formatter, predicate);
      this.cut = requireNonNull(cut);
      this.defaultValue = defaultValue;
      this.cutOn = cutOn;
    }

    @Override
    public boolean defaultValue() {
      return defaultValue;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Evaluable<E> cut() {
      return (Evaluable<E>) this.cut;
    }

    @Override
    public boolean valueToCut() {
      return cutOn;
    }
  }
}
