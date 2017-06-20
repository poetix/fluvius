package com.codepoetics.fluvius.api.history;

public interface StepFailedEvent<T> extends FlowEvent<T> {
  T getReason();
}
