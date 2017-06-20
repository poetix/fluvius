package com.codepoetics.fluvius.execution;

import com.codepoetics.fluvius.api.Flow;
import com.codepoetics.fluvius.api.FlowExecution;
import com.codepoetics.fluvius.api.FlowResultCallback;
import com.codepoetics.fluvius.api.FlowVisitor;
import com.codepoetics.fluvius.api.scratchpad.Scratchpad;
import com.codepoetics.fluvius.api.tracing.TraceMap;
import com.codepoetics.fluvius.api.tracing.TracedAction;
import com.codepoetics.fluvius.api.tracing.TracedFlowExecution;

import java.util.UUID;

public final class TraceMapCapturingFlowExecution<T> extends AbstractFlowExecution<T> implements TracedFlowExecution<T> {

  /**
   * Create a {@link TracedFlowExecution} by visiting the provided {@link Flow} with the provided {@link FlowVisitor}.
   *
   * @param flow The flow to create a {@link TracedFlowExecution} for.
   * @param visitor The visitor to use to convert the flow into a {@link TracedAction} that can be executed.
   * @param <T> The type of the value returned by the flow.
   * @return The constructed flow execution.
   */
  public static <T> TracedFlowExecution<T> forFlow(Flow<T> flow, FlowVisitor<TracedAction> visitor) {
    TracedAction action = flow.visit(visitor);
    return new TraceMapCapturingFlowExecution<>(
        KeyCheckingFlowExecution.forAction(action, flow.getRequiredKeys(), flow.getProvidedKey()),
        action.getTraceMap()
    );
  }

  private final FlowExecution<T> execution;
  private final TraceMap traceMap;

  private TraceMapCapturingFlowExecution(FlowExecution<T> execution, TraceMap traceMap) {
    this.execution = execution;
    this.traceMap = traceMap;
  }

  @Override
  public TraceMap getTraceMap() {
    return traceMap;
  }

  @Override
  public T run(UUID flowId, Scratchpad initialScratchpad) throws Exception {
    return execution.run(flowId, initialScratchpad);
  }

  @Override
  public Runnable asAsync(UUID flowId, FlowResultCallback<T> callback, Scratchpad initialScratchpad) {
    return execution.asAsync(flowId, callback, initialScratchpad);
  }
}
