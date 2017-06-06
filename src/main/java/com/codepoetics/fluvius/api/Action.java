package com.codepoetics.fluvius.api;

import com.codepoetics.fluvius.api.scratchpad.Scratchpad;

import java.io.Serializable;

/**
 * An Action takes a Scratchpad, performs some operation, and returns an updated Scratchpad.
 */
public interface Action extends Serializable {
  /**
   * Run the action.
   *
   * @param scratchpad The Scratchpad to update.
   * @return The updated Scratchpad.
   */
  Scratchpad run(Scratchpad scratchpad);
}
