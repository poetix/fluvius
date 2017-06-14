package com.codepoetics.fluvius.api.tracing;

import java.util.Map;
import java.util.UUID;

/**
 * A listener which receives trace events from executing flows.
 */
public interface TraceEventListener {

  /**
   * Called when a flow step starts.
   * @param flowId The id of the running flow.
   * @param stepId The id of the step being started.
   * @param scratchpadState The complete scratchpad being passed into the step.
   */
  void stepStarted(UUID flowId, UUID stepId, Map<String, Object> scratchpadState);

  /**
   * Called when a flow step completes successfully.
   * @param flowId The id of the running flow.
   * @param stepId The id of the step which has succeeded.
   * @param result The result of executing the step.
   */
  void stepSucceeded(UUID flowId, UUID stepId, Object result);

  /**
   * Called when a flow step terminates with an exception.
   * @param flowId The id of the running flow.
   * @param stepId The id of the step which has failed.
   * @param throwable The exception that was thrown.
   */
  void stepFailed(UUID flowId, UUID stepId, Throwable throwable);

}
