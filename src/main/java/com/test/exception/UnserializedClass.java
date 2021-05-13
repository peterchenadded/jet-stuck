package com.test.exception;

import org.springframework.stereotype.Component;

@Component
public class UnserializedClass {
  private String test = "test";

  public String getTest() {
    return test;
  }

  public void setTest(String test) {
    this.test = test;
  }

}
