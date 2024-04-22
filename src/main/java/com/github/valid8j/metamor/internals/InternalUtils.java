package com.github.valid8j.metamor.internals;

import com.github.valid8j.metamor.Dataset;
import com.github.valid8j.metamor.IoContext;

import java.util.function.Function;
import java.util.function.IntFunction;

public enum InternalUtils {
  ;

  public static <I, O> Function<Dataset<I>, Dataset<O>> createObservableProcessingPipeline(String contextName, Function<IoContext<I, O>, Function<I, O>> mapper, int numItems, IntFunction<String> variableNameFormatter, String outputContextName) {
    return IoContext.Utils.<I, O>toContextFunction(contextName, outputContextName)
        .andThen(IoContext.Utils.toContextEndomorphicFunction(mapper, numItems, variableNameFormatter))
        .andThen(IoContext.Utils.toCloseFunction(contextName))
        .andThen(IoContext.Utils.toOutputExtractorFunction(contextName));
  }
}
