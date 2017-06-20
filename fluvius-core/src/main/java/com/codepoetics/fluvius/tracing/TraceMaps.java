package com.codepoetics.fluvius.tracing;

import com.codepoetics.fluvius.api.Flow;
import com.codepoetics.fluvius.api.tracing.TraceMap;

/**
 * Utility class for obtaining the {@link com.codepoetics.fluvius.api.tracing.TraceMap} of a {@link Flow}
 */
public final class TraceMaps {

  private static final TraceMapFlowVisitor visitor = new TraceMapFlowVisitor();

  private TraceMaps() {
  }

  public static TraceMap getTraceMap(Flow<?> flow) {
    return flow.visit(visitor);
  }
}
