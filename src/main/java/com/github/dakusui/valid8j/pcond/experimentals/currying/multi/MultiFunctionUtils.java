package com.github.dakusui.valid8j.pcond.experimentals.currying.multi;

import com.github.dakusui.valid8j.pcond.core.printable.PrintableFunctionFactory;
import com.github.dakusui.valid8j.pcond.internals.InternalUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

/**
 * A utility class that collects helper methods for the multi-parameters function.
 */
public enum MultiFunctionUtils {
  ;
  private static final ThreadLocal<Map<List<Object>, MultiFunction<?>>> METHOD_BASED_FUNCTION_POOL = new ThreadLocal<>();

  @SuppressWarnings("unchecked")
  public static <R> MultiFunction<R> multifunction(int[] order, Class<?> aClass, String methodName, Class<?>... parameterTypes) {
    Map<List<Object>, MultiFunction<?>> methodBasedMultiParameterFunctionPool = methodBasedMultiParameterFunctionPool();
    List<Object> multiParamFuncDef = composeFuncDef(order, aClass, methodName, parameterTypes);
    methodBasedMultiParameterFunctionPool.computeIfAbsent(
        multiParamFuncDef,
        MultiFunctionUtils::createMultiParameterFunctionForStaticMethod);
    return (MultiFunction<R>) methodBasedMultiParameterFunctionPool.get(multiParamFuncDef);
  }

  private static List<Object> composeFuncDef(int[] order, Class<?> aClass, String methodName, Class<?>[] parameterTypes) {
    return asList(InternalUtils.getMethod(aClass, methodName, parameterTypes), Arrays.stream(order).boxed().collect(toList()));
  }

  private static <R> MultiFunction<R> createMultiParameterFunctionForStaticMethod(List<Object> multiParamFuncDef) {
    final Method method = (Method) multiParamFuncDef.get(0);
    @SuppressWarnings("unchecked") final List<Integer> paramOrder = (List<Integer>) multiParamFuncDef.get(1);
    return PrintableFunctionFactory.multifunction(method, paramOrder);
  }

  private static Map<List<Object>, MultiFunction<?>> methodBasedMultiParameterFunctionPool() {
    if (METHOD_BASED_FUNCTION_POOL.get() == null)
      METHOD_BASED_FUNCTION_POOL.set(new HashMap<>());
    return METHOD_BASED_FUNCTION_POOL.get();
  }
}
