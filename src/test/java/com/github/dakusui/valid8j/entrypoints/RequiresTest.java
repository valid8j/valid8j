package com.github.dakusui.valid8j.entrypoints;

import com.github.dakusui.valid8j.classic.Requires;
import com.github.dakusui.valid8j.pcond.forms.Functions;
import com.github.dakusui.valid8j.pcond.forms.Predicates;
import com.github.dakusui.valid8j.pcond.validator.exceptions.PreconditionViolationException;
import com.github.dakusui.valid8j.utils.testbase.TestBase;
import org.junit.Test;

import java.util.function.Predicate;

import static com.github.dakusui.valid8j.utils.TestUtils.firstLineOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.AllOf.allOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class RequiresTest extends TestBase {
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
    
    @Test(expected = NullPointerException.class)
    public void testRequireNonNull() {
        try {
            Requires.requireNonNull(null);
        } catch (NullPointerException e) {
            e.printStackTrace();
            assertThat(firstLineOf(e.getMessage()),
                allOf(
                    notNullValue(),
                    is("value:<null> violated precondition:value isNotNull")));
            throw e;
        }
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testRequireArgument() {
        try {
            Requires.requireArgument(null, Predicates.isNotNull());
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            assertThat(firstLineOf(e.getMessage()),
                allOf(
                    notNullValue(),
                    is("value:<null> violated precondition:value isNotNull")));
            throw e;
        }
    }
    
    @Test(expected = IllegalStateException.class)
    public void givenInvalidState$whenRequireState$thenIllegalStateExceptionThrown() {
        try {
            Requires.requireState(null, Predicates.isNotNull());
        } catch (IllegalStateException e) {
            e.printStackTrace();
            assertThat(firstLineOf(e.getMessage()),
                allOf(
                    notNullValue(),
                    is("value:<null> violated precondition:value isNotNull")));
            throw e;
        }
    }
    
    @Test
    public void givenValidState$whenRequireState$thenPass() {
        String var = Requires.requireState("hello", Predicates.isNotNull());
        assertNotNull(var);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testRequireWithTransformingPredicate() {
        String value = "hello";
        Requires.requireArgument(
            value,
            Predicates.transform(Functions.length()).check(Predicates.gt(100)));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testRequireWithCustomStringTransformingPredicate() {
        String value = "hello";
        try {
            Requires.requireArgument(
                value,
                Predicates.transform("TRANSFORM_TO_LENGTH", Functions.length())
                    .check("CHECK_LENGTH", Predicates.gt(100)));
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            assertThat(
                firstLineOf(e.getMessage()),
                is("value:<\"hello\"> violated precondition:value TRANSFORM_TO_LENGTH CHECK_LENGTH:>[100]"));
            throw e;
        }
    }
    
    @Test
    public void testRequireWithSatisfyingValue() {
        String value = "hello";
        assertThat(
            Requires.requireNonNull(value),
            is(value));
    }
    
    @Test
    public void testRequireWithTransformingPredicateAndSatisfyingValue() {
        String value = "hello";
        assertThat(
            Requires.requireArgument(
                value,
                Predicates.transform(Functions.length()).check(Predicates.gt(0))),
            is(value));
    }
    
    @Test
    public void testRequireWithCustomStringTransformingPredicateAndSatisfyingValue() {
        String value = "hello";
        assertThat(
            Requires.requireArgument(
                value,
                Predicates.transform(Functions.length()).check(Predicates.gt(0))),
            is(value));
    }
    
    @Test
    public void testRequire() {
        String message = Requires.require("hello", Predicates.isNotNull());
        assertNotNull(message);
    }
    
    @Test(expected = PreconditionViolationException.class)
    public void testRequire$thenError() {
        String value = null;
        String message = Requires.require(
            value,
            Predicates.isNotNull()
        );
        assertNotNull(message);
    }
    
}
