package com.github.dakusui.valid8j.entrypoints;

import com.github.dakusui.valid8j.classic.Requires;
import org.junit.Test;

import java.util.function.Predicate;

import static org.junit.Assert.assertEquals;

public class RequiresTest {
    @Test
    public void givenValidValue_whenRequire_thenValueReturned() {
        Integer v = Requires.require(0, Predicate.isEqual(0));
        assertEquals((Integer)0, v);
    }

    @Test
    public void givenValidArgument_whenRequireArgument_thenValueReturned() {
        Integer v = Requires.requireArgument(0, Predicate.isEqual(0));
        assertEquals((Integer)0, v);
    }

    @Test
    public void givenValidState_whenRequireState_thenStateReturned() {
        Integer v = Requires.requireState(0, Predicate.isEqual(0));
        assertEquals((Integer)0, v);
    }

    @Test(expected = IllegalArgumentException.class)
    public void givenIllegalArgument_whenRequireArgument_thenIllegalArgumentThrown() {
        Requires.requireArgument(0, Predicate.isEqual(1));
    }

    @Test
    public void givenNonNullValue_whenRequireNonNull_thenValueReturned() {
        Integer v = Requires.requireNonNull(0);
        assertEquals((Integer)0, v);
    }

}
