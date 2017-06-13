package com.codepoetics.fluvius.api.tracing;

import com.codepoetics.fluvius.api.FlowExecution;

/**
 * A {@link FlowExecution} which emits trace messages correlated with a {@link TraceMap} of the flow that will be executed.
 * @param <T> The type of value returned by executing the flow.
 */
public interface TracedFlowExecution<T> extends FlowExecution<T> {
  /**
   * Get a trace map of the flow that will be executed.
   * @return The trace map of the flow that will be executed.
   */
  TraceMap getTraceMap();
}
