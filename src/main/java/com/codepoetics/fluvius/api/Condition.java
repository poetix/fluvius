package com.codepoetics.fluvius.api;

import com.codepoetics.fluvius.api.scratchpad.Scratchpad;

import java.io.Serializable;

/**
 * A Condition tests a Scratchpad, returning true or false depending on the Scratchpad's state.
 */
public interface Condition extends Serializable {
  /**
   * Get the description of the Condition.
   *
   * @return The description of the Condition.
   */
  String getDescription();

  /**
   * Test the supplied Scratchpad.
   *
   * @param scratchpad The Scratchpad to test.
   * @return True if the Scratchpad matches this Condition, false otherwise.
   */
  boolean test(Scratchpad scratchpad);
}
