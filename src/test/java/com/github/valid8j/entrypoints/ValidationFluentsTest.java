package com.github.valid8j.entrypoints;

import com.github.valid8j.fluent.internals.ValidationFluents;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(Enclosed.class)
public class ValidationFluentsTest {
    public static class Singular {
        @Test
        public void test1() {
            assertTrue(ValidationFluents.precondition(ValidationFluents.value("Hello").asString().then().equalTo("Hello")));
        }

        @Test
        public void test2() {
            assertTrue(ValidationFluents.postcondition(ValidationFluents.value("Hello").asString().then().equalTo("Hello")));
        }

        @Test
        public void test3() {
            assertTrue(ValidationFluents.that(ValidationFluents.value("Hello").asString().then().equalTo("Hello")));
        }

        @Test
        public void test4() {
            assertEquals("Hello", ValidationFluents.requireState(ValidationFluents.value("Hello").asString().then().equalTo("Hello")));
        }

        @Test
        public void test5() {
            assertEquals("Hello", ValidationFluents.ensureState(ValidationFluents.value("Hello").asString().then().equalTo("Hello")));
        }

        @Test
        public void test6() {
            assertEquals("Hello", ValidationFluents.requireArgument(ValidationFluents.value("Hello").asString().then().equalTo("Hello")));
        }

        @Test
        public void test7() {
            assertEquals("Hello", ValidationFluents.requireStatement(ValidationFluents.value("Hello").asString().then().equalTo("Hello")));
        }
        @Test
        public void test8() {
            assertEquals("Hello", ValidationFluents.ensureStatement(ValidationFluents.value("Hello").asString().then().equalTo("Hello")));
        }
    }

    public static class Plural {
        @Test
        public void test1() {
            assertTrue(ValidationFluents.preconditions(
                    ValidationFluents.value("World").asString().then().equalTo("World"),
                    ValidationFluents.value("World").asString().then().equalTo("World")
            ));
        }

        @Test
        public void test2() {
            assertTrue(ValidationFluents.postconditions(
                    ValidationFluents.value("World").asString().then().equalTo("World"),
                    ValidationFluents.value("World").asString().then().equalTo("World")
            ));
        }

        @Test
        public void test3() {
            assertTrue(ValidationFluents.all(
                    ValidationFluents.value("World").asString().then().equalTo("World"),
                    ValidationFluents.value("World").asString().then().equalTo("World")
            ));
        }

        @Test
        public void test4() {
            ValidationFluents.requireStates(
                    ValidationFluents.value("World").asString().then().equalTo("World"),
                    ValidationFluents.value("World").asString().then().equalTo("World")
            );
        }

        @Test
        public void test5() {
            ValidationFluents.ensureStates(
                    ValidationFluents.value("World").asString().then().equalTo("World"),
                    ValidationFluents.value("World").asString().then().equalTo("World")
            );
        }

        @Test
        public void test6() {
            ValidationFluents.requireArguments(
                    ValidationFluents.value("World").asString().then().equalTo("World"),
                    ValidationFluents.value("World").asString().then().equalTo("World")
            );
        }

        @Test
        public void test7() {
            ValidationFluents.requireAll(
                    ValidationFluents.value("World").asString().then().equalTo("World"),
                    ValidationFluents.value("World").asString().then().equalTo("World")
            );
        }
        @Test
        public void test8() {
            ValidationFluents.ensureAll(
                    ValidationFluents.value("World").asString().then().equalTo("World"),
                    ValidationFluents.value("World").asString().then().equalTo("World")
            );
        }
    }
}
