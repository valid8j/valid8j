package com.github.valid8j.ut.equallness;

import com.github.valid8j.utils.testbase.TestBase;
import com.github.valid8j.pcond.experimentals.currying.CurriedFunctions;
import com.github.valid8j.pcond.forms.Functions;
import com.github.valid8j.pcond.forms.Predicates;
import com.github.valid8j.pcond.forms.Printables;
import com.github.valid8j.ut.internal.FunctionsTest.MultiFunctionTest.TargetMethodHolder;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Parameterized;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.github.valid8j.ut.equallness.EqualnessTest.TestDef.DUMMY_OBJECT;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;

@RunWith(Parameterized.class)
@FixMethodOrder(value = MethodSorters.JVM)
public class EqualnessTest extends TestBase {
  final Object       target;
  final Object       targetSecond;
  final Object       equal;
  final List<Object> nonEquals;
  final boolean      cached;

  public EqualnessTest(TestDef testDef) {
    target = testDef.targetObjectSupplier.get();
    targetSecond = testDef.targetObjectSupplier.get();
    equal = testDef.equalObjectSupplier.get();
    nonEquals = testDef.nonEqualObjectSuppliers
        .stream()
        .map(Supplier::get)
        .collect(toList());
    cached = testDef.cached;
  }

  @Test
  public void print() {
    System.out.println("Test:");
    System.out.printf("  Target function (f):                                    %s%n", target);
    System.out.printf("  A function g that should return true when f.equals(g):  %s%n", targetSecond);
    System.out.printf("  A function g that should return false when f.equals(g): %s%n", nonEquals);
  }

  @Test
  public void returnsSameHashCode() {
    assertEquals(target.hashCode(), targetSecond.hashCode());
  }

  @Test
  public void equalsWithOneCreatedFromSameFactoryAndArguments() {
    assertEquals(target, targetSecond);
  }

  @Test
  public void equalsWithOneCreatedFromSameFactoryAndArguments_reversed() {
    assertEquals(targetSecond, target);
  }

  @Test
  public void equalsWithItself() {
    assertEquals(target, target);
  }

  @Test
  public void notEqualsWithAnother() {
    assertThat(
        target,
        allOf(nonEquals.stream().map(each -> not(equalTo(each))).collect(toList())));
  }

  @Test
  public void notEqualsWithForeignObject() {
    Assert.assertNotEquals(target, DUMMY_OBJECT);
  }

  @Test
  public void sameObject() {
    assumeTrue(this.cached);
    assertSame(this.target, this.targetSecond);
  }

  @Test
  public void notSameObject() {
    assumeTrue(!this.cached);
    assertNotSame(this.target, this.targetSecond);
  }

  @Test
  public void equalHashCode() {
    assertEquals(this.target.hashCode(), this.targetSecond.hashCode());
  }

  @Parameterized.Parameters
  public static TestDef[] parameters() {
    //noinspection StringOperationCanBeSimplified
    return new TestDef[] {
        // Functions
        // - identity
        define(args -> Function.identity())
            .$(),
        // - stringify
        define(args -> Functions.stringify())
            .$(),
        // - length
        define(args -> Functions.length())
            .$(),
        // - size
        define(args -> Functions.size())
            .$(),
        // - stream
        define(args -> Functions.stream())
            .$(),
        // - streamOf
        define(args -> Functions.streamOf())
            .$(),
        // - cast
        define(args -> Functions.cast(String.class))
            .nonEqualObjectSupplier(() -> Functions.cast(Object.class))
            .cached(false)
            .$(),
        // - collectionToList
        define(args -> Functions.collectionToList())
            .$(),
        // - arrayToList
        define(args -> Functions.arrayToList())
            .$(),
        // - countLines
        define(args -> Functions.countLines())
            .$(),
        define(args -> Functions.elementAt(1))
            .nonEqualObjectSupplier(() -> Functions.elementAt(2))
            .cached(false)
            .$(),
        // - custom
        define(args -> Printables.function(new String("custom"), Function.identity())) // Intentionally create a new String
            .nonEqualObjectSupplier(() -> Printables.function("CUSTOM", Functions.identity()))
            .cached(false)
            .$(),
        // - .compose
        define(args -> Functions.stringify().andThen(Functions.identity()))
            .equalObjectFactory(args -> Functions.identity().compose(Functions.stringify()))
            .cached(false).$(),
        // - curry
        define(args -> Functions.curry(TargetMethodHolder.class, "voidMethod", String.class, String.class))
            .nonEqualObjectSupplier(() -> Functions.curry(TargetMethodHolder.class, "greeting", String.class, String.class))
            .nonEqualObjectSupplier(() -> Functions.multifunction(TargetMethodHolder.class, "voidMethod", String.class, String.class))
            .cached(false)
            .$(),
        // - curry (2)
        // - functionForStaticMethod
        define(args -> Functions.multifunction(TargetMethodHolder.class, "voidMethod", String.class, String.class))
            .nonEqualObjectSupplier(() -> Functions.multifunction(TargetMethodHolder.class, "greeting", String.class, String.class))
            .nonEqualObjectSupplier(() -> Functions.curry(TargetMethodHolder.class, "voidMethod", String.class, String.class))
            .cached(true)
            .$(),
        // Predicates
        // - alwaysTrue
        define(args -> Predicates.alwaysTrue()).$(),
        // - isTrue
        define(args -> Predicates.isTrue()).$(),
        // - isFalse
        define(args -> Predicates.isFalse()).$(),
        // - isNull
        define(args -> Predicates.isNull()).$(),
        // - isNotNull
        define(args -> Predicates.isNotNull()).$(),
        // - isEqualTo
        define(args -> Predicates.isEqualTo(args[0]), "hello")
            .nonEqualObjectFactory(args -> Predicates.isEqualTo(args[0]), "world")
            .cached(false)
            .$(),
        // - isSameReferenceAs
        define(args -> Predicates.isSameReferenceAs(args[0]), new Object())
            .nonEqualObjectFactory(args -> Predicates.isSameReferenceAs(args[0]), new Object())
            .cached(false)
            .$(),
        // - isInstanceOf
        define(args -> Predicates.isSameReferenceAs(args[0]), new Object())
            .nonEqualObjectFactory(args -> Predicates.isSameReferenceAs(args[0]), new Object())
            .cached(false)
            .$(),
        // - greaterThan
        define(args -> Predicates.greaterThan((String) args[0]), "HELLO")
            .nonEqualObjectFactory(args -> Predicates.greaterThan((String) args[0]), "hello")
            .cached(false)
            .$(),
        // - greaterThanOrEqualTo
        define(args -> Predicates.greaterThanOrEqualTo((String) args[0]), "HELLO")
            .nonEqualObjectFactory(args -> Predicates.greaterThanOrEqualTo((String) args[0]), "hello")
            .cached(false)
            .$(),
        // - lessThan
        define(args -> Predicates.lessThan((String) args[0]), "HELLO")
            .nonEqualObjectFactory(args -> Predicates.lessThan((String) args[0]), "hello")
            .cached(false)
            .$(),
        // - lessThanOrEqualTo
        define(args -> Predicates.lessThanOrEqualTo((String) args[0]), "HELLO")
            .nonEqualObjectFactory(args -> Predicates.lessThanOrEqualTo((String) args[0]), "hello")
            .cached(false)
            .$(),
        // - matchesRegex
        define(args -> Predicates.matchesRegex((String) args[0]), "HELLO")
            .nonEqualObjectFactory(args -> Predicates.matchesRegex((String) args[0]), "hello")
            .cached(false)
            .$(),
        // - containsString
        define(args -> Predicates.containsString((String) args[0]), "HELLO")
            .nonEqualObjectFactory(args -> Predicates.containsString((String) args[0]), "hello")
            .cached(false)
            .$(),
        // - startsWith
        define(args -> Predicates.startsWith((String) args[0]), "HELLO")
            .nonEqualObjectFactory(args -> Predicates.startsWith((String) args[0]), "hello")
            .cached(false)
            .$(),
        // - endsWith
        define(args -> Predicates.endsWith((String) args[0]), "HELLO")
            .nonEqualObjectFactory(args -> Predicates.endsWith((String) args[0]), "hello")
            .cached(false)
            .$(),
        // - isEmptyString
        define(args -> Predicates.isEmptyString())
            .$(),
        // - isNullOrEmptyString
        define(args -> Predicates.isNullOrEmptyString())
            .$(),
        // - contains
        define(args -> Predicates.contains(args[0]), "HELLO")
            .nonEqualObjectFactory(args -> Predicates.contains(args[0]), "hello")
            .cached(false)
            .$(),
        // - isEmptyArray
        define(args -> Predicates.isEmptyArray())
            .$(),
        // - isEmpty
        define(args -> Predicates.isEmpty())
            .$(),
        // - allMatch
        define(args -> Predicates.allMatch((Predicate<?>) args[0]), Predicates.alwaysTrue())
            .nonEqualObjectFactory(args -> Predicates.allMatch((Predicate<?>) args[0]), Predicates.isNull())
            .nonEqualObjectFactory(args -> Predicates.noneMatch((Predicate<?>) args[0]), Predicates.alwaysTrue())
            .nonEqualObjectFactory(args -> Predicates.anyMatch((Predicate<?>) args[0]), Predicates.alwaysTrue())
            .cached(false)
            .$(),
        define(args -> Predicates.allMatch((Predicate<?>) args[0]), Predicates.alwaysTrue())
            .cached(false)
            .$(),
        // - noneMatch
        define(args -> Predicates.noneMatch((Predicate<?>) args[0]), Predicates.alwaysTrue())
            .nonEqualObjectFactory(args -> Predicates.noneMatch((Predicate<?>) args[0]), Predicates.isNull())
            .nonEqualObjectFactory(args -> Predicates.allMatch((Predicate<?>) args[0]), Predicates.alwaysTrue())
            .nonEqualObjectFactory(args -> Predicates.anyMatch((Predicate<?>) args[0]), Predicates.alwaysTrue())
            .cached(false)
            .$(),
        // - anyMatch
        define(args -> Predicates.anyMatch((Predicate<?>) args[0]), Predicates.alwaysTrue())
            .nonEqualObjectFactory(args -> Predicates.anyMatch((Predicate<?>) args[0]), Predicates.isNull())
            .nonEqualObjectFactory(args -> Predicates.allMatch((Predicate<?>) args[0]), Predicates.alwaysTrue())
            .nonEqualObjectFactory(args -> Predicates.noneMatch((Predicate<?>) args[0]), Predicates.alwaysTrue())
            .cached(false)
            .$(),
        // - and
        define(args -> Predicates.and(Predicates.isNotNull(), Predicates.alwaysTrue()))
            .nonEqualObjectFactory(args -> Predicates.and(Predicates.isNull(), Predicates.alwaysTrue()))
            .nonEqualObjectFactory(args -> Predicates.or(Predicates.isNotNull(), Predicates.alwaysTrue()))
            .cached(false)
            .$(),
        // - or
        define(args -> Predicates.or(Predicates.isNotNull(), Predicates.alwaysTrue()))
            .nonEqualObjectFactory(args -> Predicates.or(Predicates.isNull(), Predicates.alwaysTrue()))
            .nonEqualObjectFactory(args -> Predicates.and(Predicates.isNotNull(), Predicates.alwaysTrue()))
            .cached(false)
            .$(),
        // - not
        define(args -> Predicates.not(Predicates.isNotNull()))
            .nonEqualObjectFactory(args -> Predicates.not(Predicates.isNull()))
            .nonEqualObjectFactory(args -> Predicates.not(Predicates.not(Predicates.isNotNull())))
            .cached(false)
            .$(),
        define(args -> CurriedFunctions.toCurriedContextPredicate(Predicates.isNotNull()))
            .nonEqualObjectFactory(args -> Predicates.isNull())
            .nonEqualObjectFactory(args -> CurriedFunctions.toCurriedContextPredicate(Predicates.isNull()))
            .nonEqualObjectFactory(args -> CurriedFunctions.toCurriedContextPredicate(Predicates.isNotNull(), 1))
            .equalObjectFactory(args -> CurriedFunctions.toCurriedContextPredicate(Predicates.isNotNull()))
            .cached(false)
            .$()
    };
  }

  static TestDef.Builder define(Function<Object[], Object> targetObjectFactory, Object... args) {
    return new TestDef.Builder(targetObjectFactory, args);
  }

  static class TestDef {
    public static final Object DUMMY_OBJECT = new Object() {
      @Override
      public String toString() {
        return "DUMMY_OBJECT";
      }
    };

    final Supplier<Object>       targetObjectSupplier;
    final Supplier<Object>       equalObjectSupplier;
    final List<Supplier<Object>> nonEqualObjectSuppliers;
    final boolean                cached;

    TestDef(Supplier<Object> targetObjectSupplier, Supplier<Object> equalObjectSupplier, List<Supplier<Object>> nonEqualObjectSupplier, boolean cached) {
      this.targetObjectSupplier = targetObjectSupplier;
      this.equalObjectSupplier = equalObjectSupplier;
      this.nonEqualObjectSuppliers = nonEqualObjectSupplier;
      this.cached = cached;
    }

    static class Builder {
      private final Supplier<Object>       targetObjectSupplier;
      private       Supplier<Object>       equalObjectSupplier;
      private final List<Supplier<Object>> nonEqualObjectSuppliers;
      private       boolean                cached;

      Builder(Function<Object[], Object> targetObjectFactory, Object... args) {
        this(() -> targetObjectFactory.apply(args));
      }

      Builder(Supplier<Object> targetObjectSupplier) {
        this.targetObjectSupplier = targetObjectSupplier;
        this.equalObjectSupplier = targetObjectSupplier;
        this.nonEqualObjectSuppliers = new LinkedList<Supplier<Object>>() {{
          this.add(() -> DUMMY_OBJECT);
        }};
        this.cached(true);
      }

      Builder equalObjectFactory(Function<Object[], Object> equalObjectFactory, Object... args) {
        return this.equalObjectSupplier(() -> equalObjectFactory.apply(args));
      }

      Builder equalObjectSupplier(Supplier<Object> equalObjectSupplier) {
        this.equalObjectSupplier = requireNonNull(equalObjectSupplier);
        return this;
      }

      Builder nonEqualObjectFactory(Function<Object[], Object> nonEqualObjectFactory, Object... args) {
        return this.nonEqualObjectSupplier(() -> nonEqualObjectFactory.apply(args));
      }

      Builder nonEqualObjectSupplier(Supplier<Object> nonEqualObjectSupplier) {
        this.nonEqualObjectSuppliers.add(requireNonNull(nonEqualObjectSupplier));
        return this;
      }

      Builder cached(boolean cached) {
        this.cached = cached;
        return this;
      }

      TestDef $() {
        return new TestDef(this.targetObjectSupplier, this.equalObjectSupplier, this.nonEqualObjectSuppliers, this.cached);
      }
    }
  }
}
