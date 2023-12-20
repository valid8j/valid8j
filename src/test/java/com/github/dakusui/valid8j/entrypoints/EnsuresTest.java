package com.github.dakusui.valid8j.entrypoints;

import com.github.dakusui.valid8j.Ensures;
import com.github.dakusui.valid8j_pcond.validator.exceptions.PostconditionViolationException;
import org.junit.Test;

import java.util.function.Predicate;

import static org.junit.Assert.assertEquals;

public class EnsuresTest {
    @Test
    public void givenValidArgument_whenEnsure_thenValueReturned() {
        Integer v = Ensures.ensure(0, Predicate.isEqual(0));
        assertEquals((Integer)0, v);
    }

    @Test(expected = PostconditionViolationException.class)
    public void givenIllegalValue_whenEnsure_thenPostconditionViolationThrown() {
        Ensures.ensure(0, Predicate.isEqual(1));
    }

    @Test
    public void givenValidState_whenEnsureState_thenStateReturned() {
        Integer v = Ensures.ensureState(0, Predicate.isEqual(0));
        assertEquals((Integer)0, v);
    }


    @Test
    public void givenNonNullValue_whenEnsureNonNull_thenValueReturned() {
        Integer v = Ensures.ensureNonNull(0);
        assertEquals((Integer)0, v);
    }
}
