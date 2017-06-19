package com.codepoetics.fluvius.history;

import com.codepoetics.fluvius.api.history.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class SerialisingFlowEventRepository<T> implements FlowEventRepository<T> {

  public static <T> FlowEventRepository<T> using(final FlowEventStore<T> eventStore, final EventDataSerialiser<T> dataSerialiser) {
    return new SerialisingFlowEventRepository<>(eventStore, dataSerialiser);
  }

  private final FlowEventStore<T> eventStore;
  private final EventDataSerialiser<T> dataSerialiser;

  private SerialisingFlowEventRepository(final FlowEventStore<T> eventStore, final EventDataSerialiser<T> dataSerialiser) {
    this.eventStore = eventStore;
    this.dataSerialiser = dataSerialiser;
  }

  @Override
  public List<FlowEvent<T>> getEvents(final UUID flowId) {
    return eventStore.retrieveEvents(flowId);
  }

  @Override
  public void stepStarted(final UUID flowId, final UUID stepId, final Map<String, Object> scratchpadState) {
    final Map<String, T> serialisedState = new LinkedHashMap<>();
    for (final Map.Entry<String, Object> entry : scratchpadState.entrySet()) {
      serialisedState.put(entry.getKey(), dataSerialiser.serialise(entry.getValue()));
    }
    eventStore.storeEvent(new SerialisedStepStartedEvent<>(flowId, stepId, System.currentTimeMillis(), serialisedState));
  }

  @SuppressWarnings("unchecked")
  @Override
  public void stepSucceeded(final UUID flowId, final UUID stepId, final Object result) {
    eventStore.storeEvent(new SerialisedStepSucceededEvent(flowId, stepId, System.currentTimeMillis(), dataSerialiser.serialise(result)));
  }

  @SuppressWarnings("unchecked")
  @Override
  public void stepFailed(final UUID flowId, final UUID stepId, final Exception exception) {
    eventStore.storeEvent(new SerialisedStepFailedEvent(flowId, stepId, System.currentTimeMillis(), dataSerialiser.serialiseException(exception)));
  }

  private abstract static class SerialisedFlowEvent<T> implements FlowEvent<T> {
    private final UUID flowId;
    private final UUID stepId;
    private final long timestamp;

    private SerialisedFlowEvent(final UUID flowId, final UUID stepId, final long timestamp) {
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

    private SerialisedStepStartedEvent(final UUID flowId, final UUID stepId, final long timestamp, final Map<String, T> scratchpadState) {
      super(flowId, stepId, timestamp);
      this.scratchpadState = scratchpadState;
    }

    @Override
    public Map<String, T> getScratchpadState() {
      return scratchpadState;
    }

    @Override
    public <V> V translate(final FlowEventTranslator<T, V> translator) {
      return translator.translateStepStartedEvent(this);
    }

    @Override
    public String toString() {
      return String.format("Step %s started with scratchpad %s", getStepId(), scratchpadState);
    }
  }

  private static final class SerialisedStepSucceededEvent<T> extends SerialisedFlowEvent<T> implements StepSucceededEvent<T> {

    private final T result;

    private SerialisedStepSucceededEvent(final UUID flowId, final UUID stepId, final long timestamp, final T result) {
      super(flowId, stepId, timestamp);
      this.result = result;
    }

    @Override
    public T getResult() {
      return result;
    }

    @Override
    public <V> V translate(final FlowEventTranslator<T, V> translator) {
      return translator.translateStepSucceededEvent(this);
    }

    @Override
    public String toString() {
      return String.format("Step %s succeeded with result %s", getStepId(), result);
    }
  }

  private static final class SerialisedStepFailedEvent<T> extends SerialisedFlowEvent<T> implements StepFailedEvent<T> {

    private final T reason;

    private SerialisedStepFailedEvent(final UUID flowId, final UUID stepId, final long timestamp, final T reason) {
      super(flowId, stepId, timestamp);
      this.reason = reason;
    }

    @Override
    public T getReason() {
      return reason;
    }

    @Override
    public <V> V translate(final FlowEventTranslator<T, V> translator) {
      return translator.translateStepFailedEvent(this);
    }

    @Override
    public String toString() {
      return String.format("Step %s failed with reason %s", getStepId(), reason);
    }
  }
}
