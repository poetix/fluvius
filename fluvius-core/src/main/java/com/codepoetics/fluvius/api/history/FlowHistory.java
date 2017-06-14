package com.codepoetics.fluvius.api.history;

import com.codepoetics.fluvius.api.tracing.TraceMap;

import java.util.List;
import java.util.UUID;

public interface FlowHistory<T> {
  UUID getFlowId();
  TraceMap getTraceMap();
  List<FlowEvent<T>> getEventHistory();
  <V> List<V> getTranslatedEventHistory(FlowEventTranslator<T, V> translator);
}
