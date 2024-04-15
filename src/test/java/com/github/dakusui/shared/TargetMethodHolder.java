package com.github.dakusui.shared;

import java.util.Objects;

public enum TargetMethodHolder {
  ;

  @SuppressWarnings("unused") // Called through reflection
  public static boolean stringEndsWith(String s, String suffix) {
    return s.endsWith(suffix);
  }

  @SuppressWarnings("unused") // Called through reflection
  public static boolean areEqual(Object object, Object another) {
    return Objects.equals(object, another);
  }
}
