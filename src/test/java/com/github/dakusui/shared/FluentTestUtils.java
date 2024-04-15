package com.github.dakusui.shared;

import java.util.List;

import static java.util.Arrays.asList;

public enum FluentTestUtils {
  ;

  public static List<?> list(Object... args) {
    return asList(args);
  }

}
