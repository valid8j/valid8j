package com.github.dakusui.valid8j_pcond.experimentals;

import com.github.dakusui.valid8j.classic.IllegalValueException;
import com.github.dakusui.valid8j.utils.testbase.TestBase;
import com.github.dakusui.valid8j.pcond.core.printable.PrintableFunctionFactory;
import com.github.dakusui.valid8j.pcond.experimentals.currying.CurriedFunctions;
import com.github.dakusui.valid8j.pcond.experimentals.currying.context.CurriedContext;
import org.junit.Test;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.github.dakusui.valid8j.utils.experimentals.ExperimentalsUtils.stringEndsWith;
import static com.github.dakusui.valid8j.classic.Validates.validate;
import static com.github.dakusui.valid8j.pcond.experimentals.currying.CurriedFunctions.nest;
import static com.github.dakusui.valid8j.pcond.forms.Functions.stream;
import static com.github.dakusui.valid8j.pcond.forms.Predicates.*;
import static com.github.dakusui.valid8j.pcond.forms.Printables.predicate;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.*;

public class TestAssertionsCurriedFunctionsTest extends TestBase {

  @Test(expected = IllegalValueException.class)
  public void helloError() {
    validate(
        singletonList("hello"),
        transform(stream().andThen(nest(singletonList("o"))))
            .check(noneMatch(CurriedFunctions.toCurriedContextPredicate(stringEndsWith()))));
  }

  @Test
  public void toContextPredicateTest() {
    assertFalse(CurriedFunctions.toCurriedContextPredicate(isNotNull()).test(CurriedContext.from(null)));
    assertTrue(CurriedFunctions.toCurriedContextPredicate(isNotNull()).test(CurriedContext.from(new Object())));
  }

  @Test
  public void parameterizedPredicateTest() {
    Predicate<String> p = CurriedFunctions.<String>parameterizedPredicate("containsStringIgnoreCase")
        .factory(args -> v -> v.toUpperCase().contains(args.get(0).toString().toUpperCase()))
        .create("hello");
    assertTrue(p.test("hello!"));
    assertTrue(p.test("Hello!"));
    assertFalse(p.test("World!"));
    assertEquals("containsStringIgnoreCase[hello]", p.toString());
  }

  @Test
  public void parameterizedPredicate_() {

    Predicate<String> p = CurriedFunctions.<String>parameterizedPredicate("containsStringIgnoreCase")
        .factory((args) -> predicate(() -> "toUpperCase().contains(" + args.get(0) + ")", (String v) -> v.toUpperCase().contains(args.get(0).toString().toUpperCase())))
        .create("hello");
    System.out.println("p:<" + p + ">");
    assertTrue(p.test("hello!"));
    assertTrue(p.test("Hello!"));
    assertFalse(p.test("World!"));
    assertEquals("containsStringIgnoreCase[hello]", p.toString());

  }

  @Test
  public void parameterizedFunctionTest() {
    Function<Object[], Object> f = CurriedFunctions.<Object[], Object>parameterizedFunction("arrayElementAt")
        .factory(args -> v -> v[(int) args.get(0)])
        .create(1);
    assertEquals("HELLO1", f.apply(new Object[] { 0, "HELLO1" }));
    assertEquals("HELLO2", f.apply(new Object[] { "hello", "HELLO2" }));
    assertEquals("arrayElementAt[1]", f.toString());
  }

  @Test
  public void usageExample() {
    Function<List<Object>, Function<String, String>> functionFactory = pathToUriFunctionFactory();
    Function<String, String> pathToUriOnLocalHost = functionFactory.apply(asList("http", "localhost", 80));
    System.out.println(pathToUriOnLocalHost);
    System.out.println(pathToUriOnLocalHost.apply("path/to/resource"));
    System.out.println(pathToUriOnLocalHost.apply("path/to/another/resource"));

    Function<String, String> pathToUriOnRemoteHost = functionFactory.apply(asList("https", "example.com", 8443));
    System.out.println(pathToUriOnRemoteHost);
    System.out.println(pathToUriOnRemoteHost.apply("path/to/resource"));
    System.out.println(pathToUriOnRemoteHost.apply("path/to/another/resource"));

    Function<String, String> pathToUriOnLocalHost_2 = functionFactory.apply(asList("http", "localhost", 80));
    System.out.println(pathToUriOnLocalHost.hashCode() == pathToUriOnLocalHost_2.hashCode());
    System.out.println(pathToUriOnLocalHost.equals(pathToUriOnLocalHost_2));

    System.out.println(pathToUriOnLocalHost.hashCode() == pathToUriOnRemoteHost.hashCode());
    System.out.println(pathToUriOnLocalHost.equals(pathToUriOnRemoteHost));
  }

  private static Function<List<Object>, Function<String, String>>
  pathToUriFunctionFactory() {
    return v -> PrintableFunctionFactory.create(
        (List<Object> args) -> () -> "buildUri" + args, (List<Object> args) -> (String path) -> String.format("%s://%s:%s/%s", args.get(0), args.get(1), args.get(2), path), v, TestAssertionsCurriedFunctionsTest.class
    );
  }


}
