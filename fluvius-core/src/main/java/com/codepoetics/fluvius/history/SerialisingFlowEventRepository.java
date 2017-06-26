package com.codepoetics.fluvius.history;

import com.codepoetics.fluvius.api.history.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class SerialisingFlowEventRepository<T> implements FlowEventRepository<T> {

  public static <T> FlowEventRepository<T> using(FlowEventStore<T> eventStore, EventDataSerialiser<T> dataSerialiser) {
    return new SerialisingFlowEventRepository<>(eventStore, dataSerialiser);
  }

  private final FlowEventStore<T> eventStore;
  private final EventDataSerialiser<T> dataSerialiser;

  private SerialisingFlowEventRepository(FlowEventStore<T> eventStore, EventDataSerialiser<T> dataSerialiser) {
    this.eventStore = eventStore;
    this.dataSerialiser = dataSerialiser;
  }

  @Override
  public List<FlowEvent<T>> getEvents(UUID flowId) {
    return eventStore.retrieveEvents(flowId);
  }

  @Override
  public void stepStarted(UUID flowId, UUID stepId, Map<String, Object> scratchpadState) {
    Map<String, T> serialisedState = new LinkedHashMap<>();
    for (Map.Entry<String, Object> entry : scratchpadState.entrySet()) {
      serialisedState.put(entry.getKey(), dataSerialiser.serialise(entry.getValue()));
    }
    eventStore.storeEvent(FlowEvent.started(flowId, stepId, System.currentTimeMillis(), serialisedState));
  }

  @SuppressWarnings("unchecked")
  @Override
  public void stepSucceeded(UUID flowId, UUID stepId, Object result) {
    eventStore.storeEvent(FlowEvent.succeeded(flowId, stepId, System.currentTimeMillis(), dataSerialiser.serialise(result)));
  }

  @SuppressWarnings("unchecked")
  @Override
  public void stepFailed(UUID flowId, UUID stepId, Exception exception) {
    eventStore.storeEvent(FlowEvent.failed(flowId, stepId, System.currentTimeMillis(), dataSerialiser.serialiseException(exception)));
  }

}
