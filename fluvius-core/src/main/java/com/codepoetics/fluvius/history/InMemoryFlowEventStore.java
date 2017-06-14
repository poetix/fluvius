package com.codepoetics.fluvius.history;

import com.codepoetics.fluvius.api.history.FlowEvent;
import com.codepoetics.fluvius.api.history.FlowEventStore;

import java.util.*;

public final class InMemoryFlowEventStore<T> implements FlowEventStore<T> {

  public static <T> FlowEventStore<T> create() {
    return new InMemoryFlowEventStore<>();
  }

  private final Map<UUID, List<FlowEvent<T>>> store = new HashMap<>();

  private InMemoryFlowEventStore() {
  }

  @Override
  public synchronized void storeEvent(final FlowEvent<T> event) {
    final UUID flowId = event.getFlowId();
    if (!store.containsKey(flowId)) {
      store.put(flowId, new ArrayList<FlowEvent<T>>());
    }
    final List<FlowEvent<T>> history = store.get(flowId);
    history.add(event);
  }

  @Override
  public List<FlowEvent<T>> retrieveEvents(final UUID flowID) {
    return store.containsKey(flowID)
        ? store.get(flowID)
        : Collections.<FlowEvent<T>>emptyList();
  }
}
