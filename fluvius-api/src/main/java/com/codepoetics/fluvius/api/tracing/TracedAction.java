package com.codepoetics.fluvius.api.tracing;

import com.codepoetics.fluvius.api.Action;

/**
 * An {@link Action} which notifies a {@link TraceEventListener} of trace events correlated with a {@link TraceMap}.
 */
public interface TracedAction extends Action {
  /**
   * Get the trace map to which trace events emitted by this action refer.
   *
   * @return The trace map to which trace events emitted by this action refer.
   */
  TraceMap getTraceMap();
}
