package com.codepoetics.fluvius.api.history;

import java.util.UUID;

/**
 * Event representing the failure of a step to execute.
 *
 * @param <T> The type to which the failure reason is serialised.
 */
public final class StepFailedEvent<T> extends FlowEvent<T> {

  private final T reason;

  StepFailedEvent(UUID flowId, UUID stepId, long timestamp, T reason) {
    super(flowId, stepId, timestamp);
    this.reason = reason;
  }

  /**
   * Get the reason why execution of the step failed.
   *
   * @return The reason why execution of the step failed.
   */
  public T getReason() {
    return reason;
  }

  @Override
  public <V> V translate(FlowEventTranslator<T, V> translator) {
    return translator.translateStepFailedEvent(this);
  }
}
