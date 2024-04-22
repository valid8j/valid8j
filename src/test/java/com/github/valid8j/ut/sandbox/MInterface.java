package com.github.valid8j.ut.sandbox;

public class MInterface {
  public interface A {
    default void methodInA() {
      System.out.println("methodInA");
    }

    B toB();
  }

  public interface B extends A {
    default void methodInA() {
      System.out.println("methodInA");
    }

    B toC();
  }

  public interface C extends A {
    default void methodInC() {
      System.out.println("methodInA");
    }

    B toB();
  }

  public static void main(String... args) {
    A a = new A() {
      @Override
      public B toB() {
        return new B() {

          @Override
          public B toC() {
            return this;
          }

          @Override
          public B toB() {
            return this;
          }
        };
      }
    };
  }
}
