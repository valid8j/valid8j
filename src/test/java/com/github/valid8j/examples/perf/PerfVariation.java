package com.github.valid8j.examples.perf;

import com.github.valid8j.classic.Requires;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.Objects;

import static com.github.valid8j.classic.Assertions.that;
import static com.github.valid8j.pcond.forms.Predicates.*;

/**
 * -ea; evaluator mode
 * 1 billion times
 * 0: testNoCheck                                             5ms
 * 1: testObjectsRequireNonNull                           2s457ms
 * 2: testPreconditionsRequireNonNull                  6m 0s247ms
 * 3: testPreconditionsRequireNonNullWithSimpleLambda    18s 82ms
 * 4: tetAssertNonNull                                 6m 4s542ms
 * 7: testAssertThatRange                             31m27s990ms
 *
 * -da: evaluator mode
 * 0: testNoCheck                                             5ms
 * 1  testObjectsRequireNonNull                           2s446ms
 * 2: testPreconditionsRequireNonNull                  6m12s317ms
 * 3: testPreconditionsRequireNonNullWithSimpleLambda    18s131ms
 * 4: tetAssertNonNull                                        9ms
 * 5: testAssertThatRange                                     5ms
 *
 * -ea; fast mode
 * 0: testNoCheck                                              7ms
 * 1  testObjectsRequireNonNull                            2s781ms
 * 2: testPreconditionsRequireNonNull                      2s626ms
 * 3: testPreconditionsRequireNonNullWithSimpleLambda      2s508ms
 * 4: tetAssertNonNull                                     2s519ms
 * 5: testAssertThatRange                               1m 8s354ms
 *
 * -da; fast mode
 * 0: testNoCheck                                              5ms
 * 1  testObjectsRequireNonNull                            2s869ms
 * 2: testPreconditionsRequireNonNull                      2s633ms
 * 3: testPreconditionsRequireNonNullWithSimpleLambda      2s441ms
 * 4: tetAssertNonNull                                         4ms
 * 5: testAssertThatRange                                      6ms
 */
@Ignore
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PerfVariation {
  private static final int numLoops = 1_000_000_000;

  @BeforeClass
  public static void warmUp() {
    int w = 0, x = 0, y = 0, z = 0;
    for (int i = 0; i < 10_000_000; i++) {
      w = noCheck(w);
      x = objectsRequireNonNull(x);
      y = preconditionsRequireNonNull(y);
      z = preconditionsRequireNonNullWithSimpleLambda(z);
    }
  }

  @Test
  public void a0_testNoCheck() {
    testNoCheck();
  }

  @Test
  public void a1_testObjectsRequireNonNull() {
    testObjectsRequireNonNull();
  }

  @Test
  public void a2_testPreconditionsRequireNonNull() {
    testPreconditionsRequireNonNull();
  }

  @Test
  public void a3_testPreconditionsRequireNonNullWithSimpleLambda() {
    testPreconditionsRequireNonNullWithSimpleLambda();
  }

  @Test
  public void a4_testAssertNonNull() {
    testAssertNonNulls();
  }

  @Test
  public void a7_testAssertThatRange() {
    testAssertThatRange();
  }

  @Test
  public void b0_testNoCheck() {
    testNoCheck();
  }

  @Test
  public void b1_testObjectsRequireNonNull() {
    testObjectsRequireNonNull();
  }

  @Test
  public void b2_testPreconditionsRequireNonNull() {
    testPreconditionsRequireNonNull();
  }

  @Test
  public void b3_testPreconditionsRequireNonNullWithSimpleLambda() {
    testPreconditionsRequireNonNullWithSimpleLambda();
  }

  @Test
  public void b4_testAssertNonNull() {
    testAssertNonNulls();
  }


  @Test
  public void c0_testNoCheck() {
    testNoCheck();
  }

  @Test
  public void c1_testObjectsRequireNonNull() {
    testObjectsRequireNonNull();
  }

  @Test
  public void c2_testPreconditionsRequireNonNull() {
    testPreconditionsRequireNonNull();
  }

  @Test
  public void c3_testPreconditionsRequireNonNullWithSimpleLambda() {
    testPreconditionsRequireNonNullWithSimpleLambda();
  }

  @Test
  public void c4_testAssertNonNull() {
    testAssertNonNulls();
  }

  @Test
  public void d1_testObjectsRequireNonNull() {
    testObjectsRequireNonNull();
  }

  @Test
  public void d2_testPreconditionsRequireNonNull() {
    testPreconditionsRequireNonNull();
  }

  @Test
  public void d0_testNoCheck() {
    testNoCheck();
  }

  @Test
  public void d3_testPreconditionsRequireNonNullWithSimpleLambda() {
    testPreconditionsRequireNonNullWithSimpleLambda();
  }

  @Test
  public void d4_testAssertNonNull() {
    testAssertNonNulls();
  }

  @Test
  public void e0_testNoCheck() {
    testNoCheck();
  }

  @Test
  public void e1_testObjectsRequireNonNull() {
    testObjectsRequireNonNull();
  }

  @Test
  public void e2_testPreconditionsRequireNonNull() {
    testPreconditionsRequireNonNull();
  }

  @Test
  public void e3_testPreconditionsRequireNonNullWithSimpleLambda() {
    testPreconditionsRequireNonNullWithSimpleLambda();
  }

  @Test
  public void e4_testAssertNonNull() {
    testAssertNonNulls();
  }

  @Test
  public void f0_testNoCheck() {
    testNoCheck();
  }

  @Test
  public void f1_testObjectsRequireNonNull() {
    testObjectsRequireNonNull();
  }

  @Test
  public void f2_testPreconditionsRequireNonNull() {
    testPreconditionsRequireNonNull();
  }

  @Test
  public void f3_testPreconditionsRequireNonNullWithSimpleLambda() {
    testPreconditionsRequireNonNullWithSimpleLambda();
  }

  @Test
  public void f4_testAssertNonNull() {
    testAssertNonNulls();
  }

  @Test
  public void g0_testNoCheck() {
    testNoCheck();
  }

  @Test
  public void g1_testObjectsRequireNonNull() {
    testObjectsRequireNonNull();
  }

  @Test
  public void g2_testPreconditionsRequireNonNull() {
    testPreconditionsRequireNonNull();
  }

  @Test
  public void g3_testPreconditionsRequireNonNullWithSimpleLambda() {
    testPreconditionsRequireNonNullWithSimpleLambda();
  }

  @Test
  public void g4_testAssertNonNull() {
    testAssertNonNulls();
  }

  @Test
  public void h0_testNoCheck() {
    testNoCheck();
  }

  @Test
  public void h1_testObjectsRequireNonNull() {
    testObjectsRequireNonNull();
  }

  @Test
  public void h2_testPreconditionsRequireNonNull() {
    testPreconditionsRequireNonNull();
  }

  @Test
  public void h3_testPreconditionsRequireNonNullWithSimpleLambda() {
    testPreconditionsRequireNonNullWithSimpleLambda();
  }

  @Test
  public void h4_testAssertNonNull() {
    testAssertNonNulls();
  }

  @Test
  public void i0_testNoCheck() {
    testNoCheck();
  }

  @Test
  public void i1_testObjectsRequireNonNull() {
    testObjectsRequireNonNull();
  }

  @Test
  public void i2_testPreconditionsRequireNonNull() {
    testPreconditionsRequireNonNull();
  }

  @Test
  public void i3_testPreconditionsRequireNonNullWithSimpleLambda() {
    testPreconditionsRequireNonNullWithSimpleLambda();
  }

  @Test
  public void i4_testAssertNonNull() {
    testAssertNonNulls();
  }

  @Test
  public void j0_testNoCheck() {
    testNoCheck();
  }

  @Test
  public void j1_testObjectsRequireNonNull() {
    testObjectsRequireNonNull();
  }

  @Test
  public void j2_testPreconditionsRequireNonNull() {
    testPreconditionsRequireNonNull();
  }

  @Test
  public void j3_testPreconditionsRequireNonNullWithSimpleLambda() {
    testPreconditionsRequireNonNullWithSimpleLambda();
  }

  @Test
  public void j4_testAssertNonNull() {
    testAssertNonNulls();
  }

  public static void testNoCheck() {
    int i = 0;
    long before = System.currentTimeMillis();
    while (i < numLoops)
      i = noCheck(i);
    long after = System.currentTimeMillis();
    System.out.println("noCheck:" + i + ":" + (after - before));
  }

  public static void testObjectsRequireNonNull() {
    int i = 0;
    long before = System.currentTimeMillis();
    while (i < numLoops)
      i = objectsRequireNonNull(i);
    long after = System.currentTimeMillis();
    System.out.println("objectsRequireNonNull:" + i + ":" + (after - before));
  }

  public static void testPreconditionsRequireNonNull() {
    int i = 0;
    long before = System.currentTimeMillis();
    while (i < numLoops)
      i = preconditionsRequireNonNull(i);
    long after = System.currentTimeMillis();
    System.out.println("preconditionsRequireNonNull:" + i + ":" + (after - before));

  }

  public static void testPreconditionsRequireNonNullWithSimpleLambda() {
    int i = 0;
    long before = System.currentTimeMillis();
    while (i < numLoops)
      i = preconditionsRequireNonNullWithSimpleLambda(i);
    long after = System.currentTimeMillis();
    System.out.println("preconditionsRequireNonNullWithSimpleLambda:" + i + ":" + (after - before));
  }

  public static void testAssertNonNulls() {
    int i = 0;
    long before = System.currentTimeMillis();
    while (i < numLoops)
      i = assertNonNull(i);
    long after = System.currentTimeMillis();
    System.out.println("assertNonNull:" + i + ":" + (after - before));
  }

  public static void testAssertThatRange() {
    int i = 0;
    long before = System.currentTimeMillis();
    while (i < numLoops)
      i = assertThatRange(i);
    long after = System.currentTimeMillis();
    System.out.println("assertNonNull:" + i + ":" + (after - before));
  }

  private static int assertThatRange(int i) {
    assert that(i, and(ge(0), lt(Integer.MAX_VALUE)));
    return i + 1;
  }

  public static int noCheck(int i) {
    return i + 1;
  }

  public static int objectsRequireNonNull(Integer i) {
    return Objects.requireNonNull(i) + 1;
  }

  public static int preconditionsRequireNonNull(Integer i) {
    return Requires.requireNonNull(i) + 1;
  }

  public static int assertNonNull(int i) {
    assert that(i, isNotNull());
    return i + 1;
  }


  public static int preconditionsRequireNonNullWithSimpleLambda(Integer i) {
    //noinspection Convert2MethodRef
    return Requires.requireArgument(i, v -> v != null) + 1;
  }

  @Ignore
  @Test
  public void test() {
    assert that(null, isNotNull());
  }
}
