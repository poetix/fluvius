package com.codepoetics.fluvius.test.builders;

import com.codepoetics.fluvius.api.history.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class TestFlowEvent<T, F extends TestFlowEvent<T, F>> implements FlowEvent<T> {

  public static <T> TestStepStartedEvent<T> started(UUID flowId, UUID stepId) {
    return startedWith(flowId, stepId, new HashMap<String, T>());
  }

  public static <T> TestStepStartedEvent<T> startedWith(UUID flowId, UUID stepId, Map<String, T> scratchpadContents) {
    return new TestStepStartedEvent<>(flowId, stepId, System.currentTimeMillis(), scratchpadContents);
  }

  public static <T> TestStepSucceededEvent<T> succeeded(UUID flowId, UUID stepId, T result) {
    return new TestStepSucceededEvent<>(flowId, stepId, System.currentTimeMillis(), result);
  }

  public static <T> TestStepFailedEvent<T> failed(UUID flowId, UUID stepId, T reason) {
    return new TestStepFailedEvent<>(flowId, stepId, System.currentTimeMillis(), reason);
  }
  
  private final UUID flowId;
  private final UUID stepId;
  private final long timestamp;

  private TestFlowEvent(UUID flowId, UUID stepId, long timestamp) {
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

  public static final class TestStepStartedEvent<T> extends TestFlowEvent<T, TestStepStartedEvent<T>> implements StepStartedEvent<T> {

    private final Map<String, T> scratchpadState;

    private TestStepStartedEvent(UUID flowId, UUID stepId, long timestamp, Map<String, T> scratchpadState) {
      super(flowId, stepId, timestamp);
      this.scratchpadState = scratchpadState;
    }
    
    public TestStepStartedEvent<T> withScratchpadState(String key, T value) {
      scratchpadState.put(key, value);
      return this;
    }

    @Override
    public Map<String, T> getScratchpadState() {
      return scratchpadState;
    }

    @Override
    public <V> V translate(FlowEventTranslator<T, V> translator) {
      return translator.translateStepStartedEvent(this);
    }
  }

  public static final class TestStepSucceededEvent<T> extends TestFlowEvent<T, TestStepSucceededEvent<T>> implements StepSucceededEvent<T> {

    private final T result;

    private TestStepSucceededEvent(UUID flowId, UUID stepId, long timestamp, T result) {
      super(flowId, stepId, timestamp);
      this.result = result;
    }

    @Override
    public <V> V translate(FlowEventTranslator<T, V> translator) {
      return translator.translateStepSucceededEvent(this);
    }

    @Override
    public T getResult() {
      return result;
    }
  }

  public static final class TestStepFailedEvent<T> extends TestFlowEvent<T, TestStepFailedEvent<T>> implements StepFailedEvent<T> {

    private final T reason;

    private TestStepFailedEvent(UUID flowId, UUID stepId, long timestamp, T reason) {
      super(flowId, stepId, timestamp);
      this.reason = reason;
    }

    @Override
    public <V> V translate(FlowEventTranslator<T, V> translator) {
      return translator.translateStepFailedEvent(this);
    }

    @Override
    public T getReason() {
      return reason;
    }
  }
}
