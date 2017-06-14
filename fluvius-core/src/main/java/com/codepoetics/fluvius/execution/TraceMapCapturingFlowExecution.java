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

  public static <T> TracedFlowExecution<T> forFlow(final Flow<T> flow, final FlowVisitor<TracedAction> visitor) {
    final TracedAction action = flow.visit(visitor);
    return new TraceMapCapturingFlowExecution<>(
        KeyCheckingFlowExecution.forAction(action, flow.getRequiredKeys(), flow.getProvidedKey()),
        action.getTraceMap()
    );
  }

  private final FlowExecution<T> execution;
  private final TraceMap traceMap;

  private TraceMapCapturingFlowExecution(final FlowExecution<T> execution, final TraceMap traceMap) {
    this.execution = execution;
    this.traceMap = traceMap;
  }

  @Override
  public TraceMap getTraceMap() {
    return traceMap;
  }

  @Override
  public T run(final UUID flowId, final Scratchpad initialScratchpad) {
    return execution.run(flowId, initialScratchpad);
  }

  @Override
  public Runnable asAsync(final UUID flowId, final FlowResultCallback<T> callback, final Scratchpad initialScratchpad) {
    return execution.asAsync(flowId, callback, initialScratchpad);
  }
}
