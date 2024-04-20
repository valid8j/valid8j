package com.github.dakusui.valid8j.pcond.experimentals.currying.context;

import com.github.dakusui.valid8j.pcond.experimentals.currying.CurryingUtils;
import com.github.dakusui.valid8j.pcond.core.printable.PrintablePredicateFactory;
import com.github.dakusui.valid8j.pcond.experimentals.currying.CurriedFunction;
import com.github.dakusui.valid8j.pcond.forms.Predicates;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

/**
 * A utility class that collects helper methods for the "Context" mechanism.
 */
public enum CurriedContextUtils {
  ;

  public static Stream<CurriedContext> nest(Stream<?> stream, Collection<?> inner) {
    return toCurriedContextStream(stream)
        .flatMap((CurriedContext curriedContext) -> inner
            .stream()
            .map((Function<Object, CurriedContext>) curriedContext::append));
  }

  public static Stream<CurriedContext> toCurriedContextStream(Stream<?> stream) {
    return stream.map(CurriedContextUtils::toCurriedContext);
  }

  public static <T> CurriedContext toCurriedContext(T t) {
    return t instanceof CurriedContext ? (CurriedContext) t : CurriedContext.from(t);
  }

  public static <R> Predicate<CurriedContext> applyAndTest(CurriedFunction<Object, Object> curriedFunction, Predicate<? super R> pred, @SuppressWarnings("SameParameterValue") Class<? extends R> type, int... orderArgs) {
    List<Object> spec = asList(curriedFunction, pred, asList(Arrays.stream(orderArgs).boxed().toArray()));
    return PrintablePredicateFactory.parameterizedLeaf(
        args -> () -> String.format("%s(%s%s)", args.get(1), args.get(0), args.get(2)), args -> (CurriedContext curriedContext) -> (pred).test(type.cast(CurryingUtils.applyCurriedFunction(curriedFunction, orderArgs).apply(curriedContext))), spec, CurriedContextUtils.class
    );
  }

  public static Predicate<CurriedContext> toContextPredicate(CurriedFunction<Object, Object> curriedFunction, int... orderArgs) {
    return CurriedContextUtils.applyAndTest(curriedFunction, createPredicate("curry", Predicates.isTrue()), Boolean.class, orderArgs);
  }

  public static <T> Predicate<T> createPredicate(String s, Predicate<T> predicate) {
    requireNonNull(s);
    return PrintablePredicateFactory.leaf(() -> s, predicate, CurriedContextUtils.class);
  }
}
