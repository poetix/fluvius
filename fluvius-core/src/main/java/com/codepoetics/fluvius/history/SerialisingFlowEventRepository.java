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
    eventStore.storeEvent(new SerialisedStepStartedEvent<>(flowId, stepId, System.currentTimeMillis(), serialisedState));
  }

  @SuppressWarnings("unchecked")
  @Override
  public void stepSucceeded(UUID flowId, UUID stepId, Object result) {
    eventStore.storeEvent(new SerialisedStepSucceededEvent(flowId, stepId, System.currentTimeMillis(), dataSerialiser.serialise(result)));
  }

  @SuppressWarnings("unchecked")
  @Override
  public void stepFailed(UUID flowId, UUID stepId, Exception exception) {
    eventStore.storeEvent(new SerialisedStepFailedEvent(flowId, stepId, System.currentTimeMillis(), dataSerialiser.serialiseException(exception)));
  }

  private abstract static class SerialisedFlowEvent<T> implements FlowEvent<T> {
    private final UUID flowId;
    private final UUID stepId;
    private final long timestamp;

    SerialisedFlowEvent(UUID flowId, UUID stepId, long timestamp) {
      this.flowId = flowId;
      this.stepId = stepId;
      this.timestamp = timestamp;
    }

    @Override
    public UUID getFlowId() {
      return flowId;
    }

    @Override
    public UUID getStepId() {
      return stepId;
    }

    @Override
    public long getTimestamp() {
      return timestamp;
    }
  }

  private static final class SerialisedStepStartedEvent<T> extends SerialisedFlowEvent<T> implements StepStartedEvent<T> {

    private final Map<String, T> scratchpadState;

    private SerialisedStepStartedEvent(UUID flowId, UUID stepId, long timestamp, Map<String, T> scratchpadState) {
      super(flowId, stepId, timestamp);
      this.scratchpadState = scratchpadState;
    }

    @Override
    public Map<String, T> getScratchpadState() {
      return scratchpadState;
    }

    @Override
    public <V> V translate(FlowEventTranslator<T, V> translator) {
      return translator.translateStepStartedEvent(this);
    }

    @Override
    public String toString() {
      return String.format("Step %s started with scratchpad %s", getStepId(), scratchpadState);
    }
  }

  private static final class SerialisedStepSucceededEvent<T> extends SerialisedFlowEvent<T> implements StepSucceededEvent<T> {

    private final T result;

    private SerialisedStepSucceededEvent(UUID flowId, UUID stepId, long timestamp, T result) {
      super(flowId, stepId, timestamp);
      this.result = result;
    }

    @Override
    public T getResult() {
      return result;
    }

    @Override
    public <V> V translate(FlowEventTranslator<T, V> translator) {
      return translator.translateStepSucceededEvent(this);
    }

    @Override
    public String toString() {
      return String.format("Step %s succeeded with result %s", getStepId(), result);
    }
  }

  private static final class SerialisedStepFailedEvent<T> extends SerialisedFlowEvent<T> implements StepFailedEvent<T> {

    private final T reason;

    private SerialisedStepFailedEvent(UUID flowId, UUID stepId, long timestamp, T reason) {
      super(flowId, stepId, timestamp);
      this.reason = reason;
    }

    @Override
    public T getReason() {
      return reason;
    }

    @Override
    public <V> V translate(FlowEventTranslator<T, V> translator) {
      return translator.translateStepFailedEvent(this);
    }

    @Override
    public String toString() {
      return String.format("Step %s failed with reason %s", getStepId(), reason);
    }
  }
}
