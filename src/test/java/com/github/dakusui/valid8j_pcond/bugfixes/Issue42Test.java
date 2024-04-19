package com.github.dakusui.valid8j_pcond.bugfixes;

import com.github.dakusui.valid8j.utils.Validates;
import com.github.dakusui.valid8j_pcond.bugfixes.issue42.Issue42Utils;
import org.junit.Test;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static com.github.dakusui.valid8j.pcond.forms.Functions.*;
import static com.github.dakusui.valid8j.pcond.forms.Predicates.*;
import static org.junit.Assert.assertEquals;

public class Issue42Test {
  @Test
  public void givenPrivateClassOverridingPublicMethod$whenPublicMethodIsCalled$thenCallSucceeds() {
    Map<String, String> map = Validates.validate(
        Issue42Utils.createPrivateExtendingPublicMap("Hello"),
        transform(mapKeySet()).check(not(isEmpty())));
    assertEquals(Collections.singleton("Hello"), map.keySet());
  }

  private <K> Function<Map<? extends K, ?>, Set<K>> mapKeySet() {
    return call(instanceMethod(parameter(), "keySet"));
  }
}
