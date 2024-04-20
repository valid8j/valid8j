package com.github.dakusui.valid8j.pcond.core.printable;

import com.github.dakusui.valid8j.pcond.core.identifieable.Identifiable;
import com.github.dakusui.valid8j.pcond.experimentals.currying.CurryingUtils;
import com.github.dakusui.valid8j.pcond.core.Evaluable;
import com.github.dakusui.valid8j.pcond.experimentals.currying.multi.MultiFunction;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.github.dakusui.valid8j.pcond.core.refl.ReflUtils.formatMethodName;
import static com.github.dakusui.valid8j.pcond.core.refl.ReflUtils.invokeStaticMethod;
import static com.github.dakusui.valid8j.pcond.internals.InternalChecks.requireStaticMethod;
import static com.github.dakusui.valid8j.pcond.internals.InternalChecks.validateParamOrderList;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

public enum PrintableFunctionFactory {
  COMPOSE,
  ;
  
  public enum Simple {
    IDENTITY("identity", Function.identity()),
    STRINGIFY("stringify", Object::toString),
    LENGTH("length", (Function<String, Integer>) String::length),
    SIZE("size", (Function<Collection<?>, Integer>) Collection::size),
    STREAM("stream", (Function<Collection<?>, Stream<?>>) Collection::stream),
    STREAM_OF("streamOf", Stream::of),
    ARRAY_TO_LIST("arrayToList", (Function<Object[], List<Object>>) Arrays::asList),
    COUNT_LINES("countLines", (String v) -> v.split(String.format("%n")).length),
    COLLECTION_TO_LIST("collectionToList", (Collection<?> c) -> new ArrayList<Object>() {
      {
        addAll(c);
      }
    }),
    CAST_TO("cast@compileTime", (v) -> v),
    ;
    private final Function<?, ?> instance;
    
    Simple(String name, Function<?, ?> function) {
      instance = PrintableFunctionFactory.function(() -> name, function, this);
    }
    
    @SuppressWarnings("unchecked")
    public <T, R> Function<T, R> instance() {
      return (Function<T, R>) this.instance;
    }
  }
  
  public enum Parameterized {
    ELEMENT_AT((args) -> () -> format("at[%s]", args.get(0)), (args) -> (List<?> v) -> v.get((int) args.get(0))),
    CAST((args) -> () -> format("castTo[%s]", requireNonNull((Class<?>) args.get(0)).getSimpleName()), (args) -> (Object v) -> ((Class<?>) args.get(0)).cast(v)),
    ;
    final Function<List<Object>, Supplier<String>> formatterFactory;
    final Function<List<Object>, Function<?, ?>>   functionFactory;
    
    Parameterized(Function<List<Object>, Supplier<String>> formatterFactory, Function<List<Object>, Function<?, ?>> functionFactory) {
      this.formatterFactory = formatterFactory;
      this.functionFactory = functionFactory;
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <T, R> Function<T, R> create(List<Object> args) {
      return PrintableFunctionFactory.create(this.formatterFactory, (Function) this.functionFactory, args, this);
    }
  }
  
  @SuppressWarnings("unchecked")
  public static <T, R, S> PrintableFunction<T, S> compose(Function<? super T, ? extends R> before, Function<? super R, ? extends S> after) {
    PrintableFunction<? super T, ? extends R> before_ = toPrintableFunction(before);
    PrintableFunction<? super R, ? extends S> after_ = toPrintableFunction(after);
    PrintableFunction<Object, S> other = (PrintableFunction<Object, S>) after_;
    return new PrintableFunction<>(
        COMPOSE,
        asList(before_, after_),
        () -> format("%s->%s", before, after),
        (T v) -> PrintableFunction.unwrap(after).apply(PrintableFunction.unwrap(before).apply(v)),
        PrintableFunctionFactory.<T>extractHeadOf(before_),
        (Evaluable<?>) PrintableFunctionFactory.<T, R>extractTailOf(before)
            .map((Function<?, R> t) -> PrintableFunctionFactory.<Object, R, S>compose((Function<Object, R>) t, after))
            .orElse(other));
  }
  
  @SuppressWarnings("unchecked")
  private static <T> Function<? super T, Object> extractHeadOf(Function<? super T, ?> f) {
    Function<? super T, Object> func = (Function<? super T, Object>) f;
    Function<? super T, Object> ret;
    if (func instanceof PrintableFunction) {
      ret = ((PrintableFunction<? super T, Object>) func).head();
    } else {
      ret = func;
    }
    return requireNonNull(ret);
  }
  
  @SuppressWarnings("unchecked")
  private static <T, R> Optional<Function<?, R>> extractTailOf(Function<? super T, ? extends R> f) {
    Function<? super T, Object> func = (Function<? super T, Object>) f;
    Optional<Function<?, R>> ret;
    if (func instanceof PrintableFunction) {
      ret = ((PrintableFunction<?, ?>) func).tail().map(e -> (Function<?, R>) e);
    } else {
      ret = Optional.empty();
    }
    return ret;
  }
  
  public static <R> MultiFunction<R> multifunction(Method method, List<Integer> paramOrder) {
    validateParamOrderList(paramOrder, method.getParameterCount());
    requireStaticMethod(method);
    return new MultiFunction.Builder<R>(args -> invokeStaticMethod(method, (paramOrder).stream().map(args::get).toArray()))
        .name(method.getName())
        .formatter(() -> formatMethodName(method) + CurryingUtils.formatParameterOrder(paramOrder))
        .addParameters(paramOrder.stream().map(i -> method.getParameterTypes()[i]).collect(toList()))
        .identityArgs(asList(method, validateParamOrderList(paramOrder, method.getParameterCount())))
        .$();
  }
  
  public static <T, R> Function<T, R> function(Function<T, R> function) {
    if (function instanceof PrintableFunction)
      return function;
    return function("noname:" + function.toString(), function);
  }
  
  public static <
      T, R> Function<T, R> function(String name, Function<T, R> function) {
    return function(() -> name, function);
  }
  
  public static <
      T, R> Function<T, R> function(Supplier<String> formatter, Function<T, R> function) {
    return function(formatter, function, PrintableFunctionFactory.class);
  }
  
  public static <
      T, R> Function<T, R> function(Supplier<String> formatter, Function<T, R> function, Object fallbackCreator) {
    return create(
        (args) -> formatter,
        (args) -> function,
        emptyList(),
        fallbackCreator);
  }
  
  public static <T, R> PrintableFunction<T, R> create(
      Function<List<Object>, Supplier<String>> formatterFactory, Function<List<Object>, Function<T, R>> functionFactory, List<Object> args,
      Object fallbackCreator) {
    Supplier<String> formatter = formatterFactory.apply(args);
    Function<T, R> function = functionFactory.apply(args);
    return Identifiable.creatorOf(function)
        .map(c -> new PrintableFunction<>(c, Identifiable.argsOf(function), formatter, function))
        .orElse(new PrintableFunction<>(fallbackCreator, args, formatter, function));
  }
  
  private static <
      T, R> PrintableFunction<T, R> toPrintableFunction(Function<T, R> function) {
    return (PrintableFunction<T, R>) function(function);
  }
}
