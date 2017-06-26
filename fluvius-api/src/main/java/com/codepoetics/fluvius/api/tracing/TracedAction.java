package com.codepoetics.fluvius.api.tracing;

import com.codepoetics.fluvius.api.Action;
import com.codepoetics.fluvius.api.scratchpad.Scratchpad;

import java.util.UUID;

/**
 * An {@link Action} which notifies a {@link TraceEventListener} of trace events correlated with a {@link TraceMap}.
 */
public final class TracedAction implements Action {

  public static TracedAction of(TraceMap traceMap, Action action) {
    return new TracedAction(traceMap, action);
  }

  private final TraceMap traceMap;
  private final Action action;

  private TracedAction(TraceMap traceMap, Action action) {
    this.traceMap = traceMap;
    this.action = action;
  }

  public TraceMap getTraceMap() {
    return traceMap;
  }

  @Override
  public Scratchpad run(UUID flowId, Scratchpad scratchpad) {
    return action.run(flowId, scratchpad);
  }
}
