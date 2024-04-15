package com.github.dakusui.valid8j.pcond.core.fluent.builtins;


import com.github.dakusui.valid8j.pcond.core.fluent.AbstractObjectTransformer;

public interface ComparableNumberTransformer<
    TX extends ComparableNumberTransformer<TX, V, T, N>,
    V extends ComparableNumberChecker<V, T, N>,
    T,
    N extends Number & Comparable<N>> extends
    AbstractObjectTransformer<TX, V, T, N> {
}
