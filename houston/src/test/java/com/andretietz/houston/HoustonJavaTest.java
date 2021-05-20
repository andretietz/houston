package com.andretietz.houston;

import org.junit.Test;

public class HoustonJavaTest {

  /**
   * This test is only to prove that you can use it in java as well.
   */
  @Test
  public void simpleTest() {
    Houston.init()
      .add(message -> {
      })
      .launch();

    Houston.send("test")
      .with("foo", "bar")
      .over();

    Houston.setEnabled(false);
  }
}
