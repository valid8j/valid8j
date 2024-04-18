package com.github.dakusui.valid8j.entrypoints;

import com.github.dakusui.valid8j.classic.Assertions;
import com.github.dakusui.valid8j.pcond.forms.Predicates;
import org.junit.Test;

public class AssertionsTest {
    public static class ExpectedException extends RuntimeException {
        public ExpectedException(Throwable t) {
            super(t);
        }
    }
    private static void assertTrue(boolean value) {
        // If we use JUnit's assertTrue, it will confuse pitest because it throws AssertionError, which is also thrown by methods in Assertion class.
        if (value)
            return;
        throw new RuntimeException();
    }

    @Test
    public void givenValidValue_whenPreconditionExercised_thenTrueReturned() {
        assertTrue(Assertions.precondition(0, Predicates.isEqualTo(0)));
    }

    @Test(expected = ExpectedException.class)
    public void givenInvalidValue_whenPreconditionExercised_thenAborted() {
        try {
            assertTrue(Assertions.precondition(0, Predicates.isEqualTo(1)));
        } catch (AssertionError e) {
            throw new ExpectedException(e);
        }
    }

    @Test
    public void givenValidValue_whenPostconditionExercised_thenTrueReturned() {
        assertTrue(Assertions.postcondition(0, Predicates.isEqualTo(0)));
    }

    @Test(expected = ExpectedException.class)
    public void givenInvalidValue_whenPostconditionExercised_thenAborted() {
        try {
            assertTrue(Assertions.postcondition(0, Predicates.isEqualTo(1)));
        } catch (AssertionError e) {
            throw new ExpectedException(e);
        }
    }

    @Test
    public void givenValidValue_whenInvariantConditionExercised_thenTrueReturned() {
        assertTrue(Assertions.that(0, Predicates.isEqualTo(0)));
    }

    @Test(expected = ExpectedException.class)
    public void givenInvalidValue_whenInvariantConditionExercised_thenAborted() {
        try {
            assertTrue(Assertions.that(0, Predicates.isEqualTo(1)));
        } catch (AssertionError e) {
            throw new ExpectedException(e);
        }
    }
}
