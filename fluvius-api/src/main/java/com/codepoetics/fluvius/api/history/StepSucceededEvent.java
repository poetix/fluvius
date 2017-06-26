package com.codepoetics.fluvius.api.history;

import java.util.UUID;
/**
 * Event representing the successful completion of a step.
 *
 * @param <T> The type to which event data is serialised.
 */
public final class StepSucceededEvent<T> extends FlowEvent<T> {

  private final T result;

  StepSucceededEvent(UUID flowId, UUID stepId, long timestamp, T result) {
    super(flowId, stepId, timestamp);
    this.result = result;
  }

  /**
   * Get the result of executing the step.
   *
   * @return The result of executing the step.
   */
  public T getResult() {
    return result;
  }

  @Override
  public <V> V translate(FlowEventTranslator<T, V> translator) {
    return translator.translateStepSucceededEvent(this);
  }
}
