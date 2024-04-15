package com.github.dakusui.valid8j.pcond.experimentals.currying;

import com.github.dakusui.valid8j.pcond.core.printable.PrintableFunctionFactory;
import com.github.dakusui.valid8j.pcond.experimentals.currying.context.CurriedContext;
import com.github.dakusui.valid8j.pcond.experimentals.currying.multi.MultiFunction;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.github.dakusui.valid8j.pcond.internals.InternalUtils.formatObject;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.joining;

/**
 * Intended for internal use only.
 */
public enum CurryingUtils {
  ;
  private static final ThreadLocal<Function<List<Object>, CurriedFunction<Object, Object>>> CURRIED_FUNCTION_FACTORY_POOL = new ThreadLocal<>();

  public static CurriedFunction<Object, Object> curry(MultiFunction<Object> function) {
    return curry(function, emptyList());
  }

  public static Function<List<Object>, CurriedFunction<Object, Object>> currier() {
    if (CURRIED_FUNCTION_FACTORY_POOL.get() == null)
      CURRIED_FUNCTION_FACTORY_POOL.set((List<Object> args) ->
          PrintableFunctionFactory.create(
              (args_) -> () -> functionNameFormatter(functionName(args_), ongoingContext(args_)).apply(function(args_)), (args_) -> new CurriedFunction.Impl(function(args_), ongoingContext(args_)), args, CurryingUtils.class
          ));
    return CURRIED_FUNCTION_FACTORY_POOL.get();
  }

  public static <R> Function<CurriedContext, R> applyCurriedFunction(CurriedFunction<Object, Object> curriedFunction, int... orderArgs) {
    return context -> {
      CurriedFunction<?, ?> cur = curriedFunction;
      int[] normalizedOrderArgs = normalizeOrderArgs(context, orderArgs);
      for (int i = 0; i < normalizedOrderArgs.length - 1; i++)
        cur = cur.applyNext(context.valueAt(normalizedOrderArgs[i]));
      return cur.applyLast(context.valueAt(normalizedOrderArgs[context.size() - 1]));
    };
  }

  public static int[] normalizeOrderArgs(CurriedContext curriedContext, int[] orderArgs) {
    int[] order;
    if (orderArgs.length == 0)
      order = IntStream.range(0, curriedContext.size()).toArray();
    else
      order = orderArgs;
    return order;
  }

  static CurriedFunction<Object, Object> curry(MultiFunction<Object> function, List<? super Object> ongoingContext) {
    return currier().apply(asList(function.name(), function, ongoingContext));
  }

  private static String functionName(List<Object> args) {
    return (String) args.get(0);
  }

  @SuppressWarnings("unchecked")
  private static MultiFunction<Object> function(List<Object> args) {
    return (MultiFunction<Object>) args.get(1);
  }

  @SuppressWarnings("unchecked")
  private static List<? super Object> ongoingContext(List<Object> args) {
    return (List<? super Object>) args.get((2));
  }

  private static Function<MultiFunction<Object>, String> functionNameFormatter(String functionName, List<? super Object> ongoingContext) {
    return (MultiFunction<Object> function) -> functionName +
        (!ongoingContext.isEmpty() ? IntStream.range(0, ongoingContext.size())
            .mapToObj(i -> function.parameterType(i).getSimpleName() + ":" + ongoingContext.get(i))
            .collect(joining(",", "(", ")")) : "") +
        IntStream.range(ongoingContext.size(), function.arity())
            .mapToObj(i -> "(" + function.parameterType(i).getSimpleName() + ")")
            .collect(joining());
  }

  public static String formatParameterOrder(List<Integer> paramOrder) {
    String formatted = formatParamOrder(paramOrder.stream());
    String uncustomized = formatParamOrder(IntStream.range(0, paramOrder.size()).boxed());
    return formatted.equals(uncustomized) ?
        "" :
        formatted;
  }

  private static String formatParamOrder(Stream<Integer> paramOrderStream) {
    return paramOrderStream.map(Object::toString).collect(joining(",", "(", ")"));
  }

  static Supplier<String> messageInvalidTypeArgument(Object value, Class<?> aClass) {
    return () -> "Given argument:" + formatObject(value) +
        (value == null ?
            "" :
            "(" + value.getClass() + ")") +
        " cannot be assigned to parameter:" + aClass.getCanonicalName();
  }
}
