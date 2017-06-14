package com.codepoetics.fluvius.api.history;

import com.codepoetics.fluvius.api.tracing.TraceEventListener;
import com.codepoetics.fluvius.api.tracing.TraceMap;

import java.util.UUID;

public interface FlowHistoryRepository<T> extends TraceEventListener {
  void storeTraceMap(UUID flowId, TraceMap traceMap);
  FlowHistory<T> getFlowHistory(UUID flowId);
}
