package com.codepoetics.fluvius.history;

import com.codepoetics.fluvius.api.history.TraceMapRepository;
import com.codepoetics.fluvius.api.tracing.TraceMap;
import com.codepoetics.fluvius.exceptions.TraceMapNotFoundException;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class InMemoryTraceMapRepository implements TraceMapRepository {

  public static TraceMapRepository create() {
    return new InMemoryTraceMapRepository();
  }

  private InMemoryTraceMapRepository() {
  }

  private final ConcurrentMap<UUID, TraceMap> store = new ConcurrentHashMap<>();

  @Override
  public void storeTraceMap(UUID flowId, TraceMap traceMap) {
    store.put(flowId, traceMap);
  }

  @Override
  public TraceMap getTraceMap(UUID flowId) {
    TraceMap result = store.get(flowId);
    if (result == null) {
      throw TraceMapNotFoundException.forFlow(flowId);
    }
    return result;
  }
}
