package com.codepoetics.fluvius.api;

import com.codepoetics.fluvius.api.scratchpad.KeyValue;
import com.codepoetics.fluvius.api.scratchpad.Scratchpad;

import java.util.UUID;

/**
 * A {@link com.codepoetics.fluvius.api.Flow} compiled for execution.
 * @param <T> The type of the value returned by executing the compiled flow.
 */
public interface FlowExecution<T> {

  /**
   * Run the compiled {@link com.codepoetics.fluvius.api.Flow} against the provided initial scratchpad.
   *
   * @param flowId The id to assign to the flow.
   * @param initialScratchpad The initial scratchpad to run the flow against.
   * @return The result of running the flow.
   */
  T run(UUID flowId, Scratchpad initialScratchpad);

  /**
   * Run the compiled {@link com.codepoetics.fluvius.api.Flow} against an initial scratchpad created with the provided values.
   *
   * @param flowId The id to assign to the flow.
   * @param initialKeyValues The initial key values to write into the scratchpad.
   * @return The result of running the flow.
   */
  T run(UUID flowId, KeyValue...initialKeyValues);
}
