package com.codepoetics.fluvius.api.history;

import com.codepoetics.fluvius.api.tracing.TraceEventListener;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class FlowEventRepository<T> implements TraceEventListener {

  /**
   * Create a {@link FlowEventRepository} that will write flow event data serialised with the provided {@link EventDataSerialiser} to the provided {@link FlowEventStore}.
   *
   * @param eventStore The event store to use to store flow events.
   * @param dataSerialiser The serialiser to use to serialise flow event data.
   * @param <T> The type to which flow event data will be serialised.
   * @return The constructed flow event repository.
   */
  public static <T> FlowEventRepository<T> using(FlowEventStore<T> eventStore, EventDataSerialiser<T> dataSerialiser) {
    return new FlowEventRepository<>(eventStore, dataSerialiser);
  }

  private final FlowEventStore<T> eventStore;
  private final EventDataSerialiser<T> dataSerialiser;

  private FlowEventRepository(FlowEventStore<T> eventStore, EventDataSerialiser<T> dataSerialiser) {
    this.eventStore = eventStore;
    this.dataSerialiser = dataSerialiser;
  }

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
