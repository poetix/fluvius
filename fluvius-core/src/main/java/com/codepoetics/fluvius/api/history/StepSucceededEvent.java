package com.codepoetics.fluvius.api.history;

public interface StepSucceededEvent<T> extends FlowEvent<T> {
  T getResult();
}
