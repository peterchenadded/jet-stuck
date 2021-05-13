package com.test.exception;

public class TestException extends RuntimeException {
  
  private static final long serialVersionUID = -2308214167020253364L;
  private UnserializedClass unserializedClass;
  
  public TestException() {
    super();
    this.unserializedClass = new UnserializedClass();
  }
}
