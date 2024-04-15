package com.github.dakusui.ut.valid8j.forms;

import com.github.dakusui.valid8j.pcond.experimentals.currying.CurryingUtils;
import com.github.dakusui.valid8j.pcond.experimentals.currying.context.CurriedContext;
import com.github.dakusui.valid8j.pcond.experimentals.currying.multi.MultiFunction;
import com.github.dakusui.valid8j.pcond.forms.Printables;

import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.github.dakusui.valid8j.classic.Requires.requireArgument;
import static com.github.dakusui.valid8j.pcond.forms.Predicates.*;
import static com.github.dakusui.valid8j.pcond.internals.InternalUtils.formatObject;
import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

enum Lab {
  ;

  public static Function<Collection<?>, Stream<List<?>>> cartesianWith(Collection<?>... inners) {
    return Printables.function(() -> "cartesianWith" + formatObject(inners), outer -> cartesian(outer, asList(inners)));
  }

  public static Predicate<List<?>> uncurry(Function<?, Predicate<Object>> curriedFunc) {
    return Printables.predicate(() -> "uncurried:" + curriedFunc, args -> applyValues(curriedFunc, args));
  }

  public static void main2(String... args) {
    System.out.println(isInstanceOf(Serializable.class));
    System.out.println(isInstanceOf(Serializable.class).test(null));

    System.out.println(applyValues(isInstanceOf(), asList("hello", String.class)) + "");
    System.out.println(applyValues(isInstanceOf(), asList("hello", Map.class)) + "");
  }

  @SuppressWarnings("unchecked")
  public static <T> T applyValues(Function<?, ?> func, List<?> args) {
    requireArgument(requireNonNull(args), not(isEmpty()));
    Object ret = func;
    for (Object arg : args)
      ret = applyOrTest(ret, arg);
    return (T) ret;
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  private static Object applyOrTest(Object func, Object arg) {
    requireArgument(func, or(isInstanceOf(Function.class), isInstanceOf(Predicate.class)));
    if (func instanceof Predicate)
      return ((Predicate<Object>) func).test(arg);
    return ((Function<Object, Object>) func).apply(arg);
  }

  public static void main(String... args) {
    System.out.println(isInstanceOf());
    System.out.println(isInstanceOf().apply(String.class));
    System.out.println(isInstanceOf().apply(String.class).test("Hello"));
    System.out.println(isInstanceOf().apply(Map.class).test("Hello"));
    System.out.println(isInstanceOf().apply(Class.class).test("Hello"));
    System.out.println(">>" + applyValues(isInstanceOf(), asList("Hello", Class.class)));
    System.out.println(requireArgument(
        asList("hello", new HashMap<>(), Object.class),
        transform(cartesianWith(asList(Map.class, List.class, String.class)))
            .check(noneMatch(uncurry(isInstanceOf())))
    ));
  }

  private static Stream<List<?>> cartesian(Collection<?> outer, List<Collection<?>> inners) {
    Stream<List<?>> ret = wrapWithList(outer.stream());
    for (Collection<?> i : inners)
      ret = cartesianPrivate(ret, i.stream());
    return ret;
  }

  private static Stream<List<?>> cartesianPrivate(Stream<List<?>> outer, Stream<?> inner) {
    return outer.flatMap(i -> inner.map(j -> new ArrayList<Object>(i) {{
      this.add(0, j);
    }}));
  }

  private static Stream<List<?>> wrapWithList(Stream<?> stream) {
    return stream.map(Collections::singletonList);
  }

  public static <R> Function<CurriedContext, R> apply(MultiFunction<R> multiFunction, int... orderArgs) {
    return context -> {
      IntStream orderStream = Arrays.stream(CurryingUtils.normalizeOrderArgs(context, orderArgs));
      return multiFunction.apply(orderStream.distinct().mapToObj(context::valueAt).collect(Collectors.toList()));
    };
  }
}
