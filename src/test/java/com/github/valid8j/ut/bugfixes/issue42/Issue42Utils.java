package com.github.valid8j.ut.bugfixes.issue42;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public enum Issue42Utils {
  ;

  public static Map<String, String> createPrivateExtendingPublicMap(String element) {
    return new PrivateExtendingPublic(element);
  }

  private static class PrivateExtendingPublic extends HashMap<String, String> {
    private final String element;

    PrivateExtendingPublic(String element) {
      this.element = element;
    }
    @Override
    public Set<String> keySet() {
      Set<String> ret = new HashSet<String>() {{
        this.add(element);
      }};
      return ret;
    }
  }
}
