package com.github.valid8j.entrypoints;

import com.github.valid8j.fluent.Expectations;
import com.github.valid8j.pcond.fluent.Statement;
import com.github.valid8j.pcond.forms.Predicates;
import org.junit.AssumptionViolatedException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * - Style:
 * - Classic, Fluent
 * - Multiplicity
 * - Singular, Plural
 * - Statement creation
 * - Types: int short string, list, ...
 * - Starting point: value and predicate, value only, transformer
 * - Passing Failing
 */
@RunWith(Parameterized.class)
public class ExpectationsTest {
  private final StatementValidator statementValidator;
  private final StatementFactory statementFactory;

  public ExpectationsTest(Object v, Object f) {
    this.statementValidator = (StatementValidator) v;
    this.statementFactory = (StatementFactory) f;
  }

  @Test
  public void testPassingStatement() {
    if (this.statementValidator.isPlural() && this.statementFactory == StatementFactory.STREAM_STATEMENT)
      throw new AssumptionViolatedException("This combination should be tested by other methods.");
    this.statementValidator.accept(this.statementFactory.passingStatement());
  }

  @Test
  public void testPassingValue() {
    if (this.statementValidator.isPlural() && this.statementFactory == StatementFactory.STREAM_STATEMENT)
      throw new AssumptionViolatedException("This combination should be tested by other methods.");
    this.statementValidator.accept(this.statementFactory.passingValue());
  }


  @Parameterized.Parameters(name = "{index}: {0}: {1}")
  public static Object[][] parameters() {
    return Arrays.stream(StatementValidator.values())
        .flatMap(v -> Arrays.stream(StatementFactory.values())
            .map(f -> new Object[]{v, f}))
        .toArray(Object[][]::new);
  }

  @Test
  public void testPassingThat() {
    if (this.statementValidator.isPlural() && this.statementFactory == StatementFactory.STREAM_STATEMENT)
      throw new AssumptionViolatedException("This combination should be tested by other methods.");
    this.statementValidator.accept(this.statementFactory.passingThat());
  }


  enum StatementValidator implements Consumer<Statement<?>> {
    ALL {
      @Override
      public void accept(Statement<?> statement) {
        Expectations.all(statement, statement);
      }

      @Override
      public boolean isPlural() {
        return true;
      }
    },
    DOLLAR {
      @Override
      public void accept(Statement<?> statement) {
        Expectations.$(statement, statement);
      }

      @Override
      public boolean isPlural() {
        return true;
      }
    },
    PRECONDITIONS {
      @Override
      public void accept(Statement<?> statement) {
        Expectations.preconditions(statement, statement);
      }

      @Override
      public boolean isPlural() {
        return true;
      }
    },
    PRECONDITION {
      @Override
      public void accept(Statement<?> statement) {
        Expectations.precondition(statement);
      }
    },
    INVARIANTS {
      @Override
      public void accept(Statement<?> statement) {
        Expectations.invariants(statement, statement);
      }

      @Override
      public boolean isPlural() {
        return true;
      }
    },
    INVARIANT {
      @Override
      public void accept(Statement<?> statement) {
        Expectations.invariant(statement);
      }
    },
    POSTCONDITIONS {
      @Override
      public void accept(Statement<?> statement) {
        Expectations.postconditions(statement, statement);
      }

      @Override
      public boolean isPlural() {
        return true;
      }
    },
    POSTCONDITION {
      @Override
      public void accept(Statement<?> statement) {
        Expectations.postcondition(statement);
      }
    },
    REQUIRE_ {
      @Override
      public void accept(Statement<?> statement) {
        Expectations.require(statement, statement);
      }

      @Override
      public boolean isPlural() {
        return true;
      }
    },
    REQUIRE {
      @Override
      public void accept(Statement<?> statement) {
        Expectations.require(statement);
      }
    },
    REQUIRE_ARGUMENTS {
      @Override
      public void accept(Statement<?> statement) {
        Expectations.requireArguments(statement, statement);
      }

      @Override
      public boolean isPlural() {
        return true;
      }
    },
    REQUIRE_ARGUMENT {
      @Override
      public void accept(Statement<?> statement) {
        Expectations.requireArgument(statement);
      }
    },
    REQUIRE_STATES {
      @Override
      public void accept(Statement<?> statement) {
        Expectations.requireStates(statement, statement);
      }

      @Override
      public boolean isPlural() {
        return true;
      }
    },
    REQUIRE_STATE {
      @Override
      public void accept(Statement<?> statement) {
        Expectations.requireState(statement);
      }
    },
    HOLD_ {
      @Override
      public void accept(Statement<?> statement) {
        Expectations.hold(statement, statement);
      }

      @Override
      public boolean isPlural() {
        return true;
      }
    },
    HOLD {
      @Override
      public void accept(Statement<?> statement) {
        Expectations.hold(statement);
      }
    },
    ENSURE_ {
      @Override
      public void accept(Statement<?> statement) {
        Expectations.ensure(statement, statement);
      }

      @Override
      public boolean isPlural() {
        return true;
      }
    },
    ENSURE {
      @Override
      public void accept(Statement<?> statement) {
        Expectations.ensure(statement);
      }
    },
    UNLESS_ {
      @Override
      public void accept(Statement<?> statement) {
        Expectations.fail(RuntimeException::new).unless(statement, statement);
      }

      @Override
      public boolean isPlural() {
        return true;
      }
    },
    UNLESS {
      @Override
      public void accept(Statement<?> statement) {
        Expectations.fail(RuntimeException::new).unless(statement);
      }
    },
    EXPECT {
      @Override
      public void accept(Statement<?> statement) {
        Expectations.expect(statement, RuntimeException::new);
      }
    };

    boolean isPlural() {
      return false;
    }
  }

  enum StatementFactory {
    BOOLEAN_STATEMENT {
      @Override
      public Statement<?> passingStatement() {
        return Expectations.statement((boolean) value(), predicate());
      }

      @Override
      public Statement<?> passingValue() {
        return Expectations.value((boolean) value()).satisfies().predicate(predicate());
      }

      @Override
      public Statement<?> passingThat() {
        return Expectations.that((boolean)value()).satisfies().predicate(predicate());
      }

      @Override
      Boolean value() {
        return true;
      }

      @Override
      Predicate<Boolean> predicate() {
        return Predicates.alwaysTrue();
      }
    },
    SHORT_STATEMENT {
      @Override
      public Statement<?> passingStatement() {
        return Expectations.statement((short) value(), predicate());
      }

      @Override
      public Statement<?> passingValue() {
        return Expectations.value((short) value()).satisfies().predicate(predicate());
      }

      @Override
      public Statement<?> passingThat() {
        return Expectations.that((short)value()).satisfies().predicate(predicate());
      }

      @Override
      Short value() {
        return (short) 1;
      }

      @Override
      Predicate<Short> predicate() {
        return Predicates.greaterThan((short) 0);
      }
    },
    INT_STATEMENT {
      @Override
      public Statement<?> passingStatement() {
        return Expectations.statement(1, Predicates.greaterThan(0));
      }

      @Override
      public Statement<?> passingValue() {
        return Expectations.value((int) value()).satisfies().predicate(predicate());
      }

      @Override
      public Statement<?> passingThat() {
        return Expectations.that((int) value()).satisfies().predicate(predicate());
      }

      @Override
      Integer value() {
        return 1;
      }

      @Override
      Predicate<Integer> predicate() {
        return Predicates.greaterThan(0);
      }
    },
    LONG_STATEMENT {
      @Override
      public Statement<?> passingStatement() {
        return Expectations.statement(1L, Predicates.greaterThan(0L));
      }

      @Override
      public Statement<?> passingValue() {
        return Expectations.value((long) value()).satisfies().predicate(predicate());
      }

      @Override
      public Statement<?> passingThat() {
        return Expectations.that((long) value()).satisfies().predicate(predicate());
      }

      @Override
      Long value() {
        return 1L;
      }

      @Override
      Predicate<Long> predicate() {
        return Predicates.greaterThan(0L);
      }
    },
    FLOAT_STATEMENT {
      @Override
      public Statement<?> passingStatement() {
        return Expectations.statement((float) 1.0, Predicates.greaterThan((float) 0.1));
      }

      @Override
      public Statement<?> passingValue() {
        return Expectations.value((float) value()).satisfies().predicate(predicate());
      }

      @Override
      public Statement<?> passingThat() {
        return Expectations.that((float) value()).satisfies().predicate(predicate());
      }

      @Override
      Float value() {
        return (float) 1.0;
      }

      @Override
      Predicate<Float> predicate() {
        return Predicates.greaterThan(0.0f);
      }
    },
    DOUBLE_STATEMENT {
      @Override
      public Statement<?> passingStatement() {
        return Expectations.statement((double) value(), predicate());
      }

      @Override
      public Statement<?> passingValue() {
        return Expectations.value(1.0d).satisfies().predicate(predicate());
      }

      @Override
      public Statement<?> passingThat() {
        return Expectations.that(1.0d).satisfies().predicate(predicate());
      }

      @Override
      Double value() {
        return 1.0d;
      }

      @Override
      Predicate<Double> predicate() {
        return Predicates.greaterThan(0.1d);
      }
    },
    STRING_STATEMENT {
      @Override
      public Statement<?> passingStatement() {
        return Expectations.statement(value(), predicate());
      }

      @Override
      public Statement<?> passingValue() {
        return Expectations.value(value()).satisfies().predicate(predicate());
      }

      @Override
      public Statement<?> passingThat() {
        return Expectations.that(value()).satisfies().predicate(predicate());
      }

      @Override
      String value() {
        return "hello";
      }

      @Override
      Predicate<String> predicate() {
        return Predicates.isNotNull();
      }
    },
    LIST_STATEMENT {
      @Override
      public Statement<?> passingStatement() {
        return Expectations.statement(value(), predicate());
      }

      @Override
      public Statement<?> passingValue() {
        return Expectations.value(value()).then().predicate(predicate());
      }

      @Override
      public Statement<?> passingThat() {
        return Expectations.that(value()).satisfies().predicate(predicate());
      }

      @Override
      List<String> value() {
        return Collections.singletonList("hello");
      }

      @Override
      Predicate<List<String>> predicate() {
        return (Predicate<List<String>>) (Predicate) Predicates.isEmpty().negate();
      }
    },
    STREAM_STATEMENT {
      @Override
      public Statement<?> passingStatement() {
        return Expectations.statement(value(), Predicates.anyMatch(predicate()));
      }

      @Override
      public Statement<?> passingValue() {
        return Expectations.value(value()).then().anyMatch(predicate());
      }

      @Override
      public Statement<?> passingThat() {
        return Expectations.that(value()).then().anyMatch(predicate());
      }

      @Override
      Stream<String> value() {
        return Stream.of("Hello");
      }

      @Override
      Predicate<String> predicate() {
        return Predicates.isNotNull();
      }
    },
    OBJECT_STATEMENT {
      @Override
      public Statement<?> passingStatement() {
        return Expectations.statement(value(), predicate());
      }

      @Override
      public Statement<?> passingValue() {
        return Expectations.value(value()).then().predicate(predicate());
      }

      @Override
      public Statement<?> passingThat() {
        return Expectations.that(value()).then().predicate(predicate());
      }

      @Override
      Object value() {
        return new Object();
      }

      @Override
      Predicate<Object> predicate() {
        return Predicates.isNotNull();
      }
    },
    THROWABLE_STATEMENT {
      @Override
      public Statement<?> passingStatement() {
        return Expectations.statement(value(), predicate());
      }

      @Override
      public Statement<?> passingValue() {
        return Expectations.value(value()).then().predicate(predicate());
      }

      @Override
      public Statement<?> passingThat() {
        return Expectations.that(value()).then().predicate(predicate());
      }

      @Override
      Throwable value() {
        return new RuntimeException();
      }

      @Override
      Predicate<Throwable> predicate() {
        return Predicates.isNotNull();
      }
    }
    ;

    public abstract Statement<?> passingStatement();

    public abstract Statement<?> passingValue();

    public abstract Statement<?> passingThat();

    abstract Object value();

    abstract Predicate<?> predicate();
  }
}
