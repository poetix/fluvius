package com.codepoetics.fluvius.api.history;

import com.codepoetics.fluvius.api.tracing.TraceMap;

import java.util.UUID;

public interface TraceMapRepository {
  void storeTraceMap(UUID flowId, TraceMap traceMap);
  TraceMap getTraceMap(UUID flowId);
}
