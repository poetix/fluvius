package com.codepoetics.fluvius.api.history;

import java.util.Map;
import java.util.UUID;

/**
 * Event representing the start of a step's execution.
 *
 * @param <T> The type to which event data is serialised.
 */
public final class StepStartedEvent<T> extends FlowEvent<T> {

  private final Map<String, T> scratchpadState;

  StepStartedEvent(UUID flowId, UUID stepId, long timestamp, Map<String, T> scratchpadState) {
    super(flowId, stepId, timestamp);
    this.scratchpadState = scratchpadState;
  }

  /**
   * Get the scratchpad state at the point where the step was started.
   *
   * @return The scratchpad state at the point where the step was started.
   */
  public Map<String, T> getScratchpadState() {
    return scratchpadState;
  }

  @Override
  public <V> V translate(FlowEventTranslator<T, V> translator) {
    return translator.translateStepStartedEvent(this);
  }
}
