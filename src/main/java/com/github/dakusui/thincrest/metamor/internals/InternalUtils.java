package com.github.dakusui.thincrest.metamor.internals;

import com.github.dakusui.thincrest.metamor.Dataset;
import com.github.dakusui.thincrest.metamor.IoContext;

import java.util.function.Function;
import java.util.function.IntFunction;

import static com.github.dakusui.thincrest.metamor.IoContext.Utils.toContextEndomorphicFunction;

public enum InternalUtils {
  ;
  
  public static <I, O> Function<Dataset<I>, Dataset<O>> createObservableProcessingPipeline(String contextName, Function<IoContext<I, O>, Function<I, O>> mapper, int numItems, IntFunction<String> variableNameFormatter, String outputContextName) {
    return IoContext.Utils.<I, O>toContextFunction(contextName, outputContextName)
        .andThen(toContextEndomorphicFunction(mapper, numItems, variableNameFormatter))
        .andThen(IoContext.Utils.toCloseFunction(contextName))
        .andThen(IoContext.Utils.toOutputExtractorFunction(contextName));
  }
}
