package com.codepoetics.fluvius.api;

import com.codepoetics.fluvius.api.scratchpad.Scratchpad;

import java.io.Serializable;
import java.util.UUID;

/**
 * An Action takes a Scratchpad, performs some operation, and returns an updated Scratchpad.
 */
public interface Action extends Serializable {
  /**
   * Run the action.
   *
   * @param flowId The ID of the running flow.
   * @param scratchpad The Scratchpad to update.
   * @return The updated Scratchpad.
   */
  Scratchpad run(UUID flowId, Scratchpad scratchpad);
}
