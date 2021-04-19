package com.andretietz.houston;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class HoustonJavaTest {

  /**
   * This test is only to prove that you can use it in java as well.
   */
  @Test
  public void simpleTest() {
    Houston.init()
      .add(new TrackingTool() {
        @Override
        public boolean getInitialized() {
          return false;
        }

        @Override
        public void setInitialized(boolean initialized) {

        }

        @Override
        public void initialize() {

        }

        @Override
        public void send(@NotNull Message message) {

        }
      })
      .launch();

    Houston.send("test")
      .with("foo", "bar")
      .over();

    Houston.setEnabled(false);
  }
}
