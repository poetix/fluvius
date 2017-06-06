package com.codepoetics.fluvius.scratchpad;

import com.codepoetics.fluvius.api.scratchpad.KeyValue;
import com.codepoetics.fluvius.api.scratchpad.Scratchpad;

/**
 * Utility class for working with Scratchpads.
 */
public final class Scratchpads {

  private Scratchpads() {
  }

  /**
   * Create a new Scratchpad, populated with the supplied KeyValues.
   *
   * @param keyValues The KeyValues to write into the Scratchpad.
   * @return The created Scratchpad.
   */
  public static Scratchpad create(final KeyValue... keyValues) {
    return RealScratchpad.create(keyValues);
  }
}
