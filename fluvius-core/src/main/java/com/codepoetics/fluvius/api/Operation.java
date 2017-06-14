package com.codepoetics.fluvius.api;

import com.codepoetics.fluvius.api.scratchpad.Scratchpad;

import java.io.Serializable;

/**
 * An Operation accepts a Scratchpad, and returns a value of some type.
 *
 * @param <T> The type of the value returned by the Operation.
 */
public interface Operation<T> extends Serializable {
  /**
   * Get the name of this Operation.
   *
   * @return The name of this Operation.
   */
  String getName();

  /**
   * Run this Operation against the supplied Scratchpad.
   *
   * @param scratchpad The Scratchpad to run this Operation against.
   * @return The resulting value.
   */
  T run(Scratchpad scratchpad);
}
