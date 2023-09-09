package com.github.dakusui.thincrest.metamor;

public interface IoPair<I, O> {
  I input();
  
  O output();
  
  class Impl<I, O> implements IoPair<I, O> {
    
    private final I input;
    private final O output;
    
    public Impl(I input, O output) {
      this.input = input;
      this.output = output;
    }
    
    @Override
    public I input() {
      return input;
    }
    
    @Override
    public O output() {
      return output;
    }
    
    public String toString() {
      return String.format("[%s]=>[%s]", input, output);
    }
  }
  
  static <I, O> IoPair<I, O> create(I input, O output) {
    return new Impl<>(input, output);
  }
}
