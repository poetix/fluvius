package com.codepoetics.fluvius.api.history;

public interface FlowEventTranslator<T, V> {
  V translateStepStartedEvent(StepStartedEvent<T> event);
  V translateStepSucceededEvent(StepSucceededEvent<T> event);
  V translateStepFailedEvent(StepFailedEvent<T> event);
}
