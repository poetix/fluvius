package com.codepoetics.fluvius.api.history;

import com.codepoetics.fluvius.api.tracing.TraceEventListener;

import java.util.List;
import java.util.UUID;

public interface FlowEventRepository<T> extends TraceEventListener {
  List<FlowEvent<T>> getEvents(UUID flowId);
}
