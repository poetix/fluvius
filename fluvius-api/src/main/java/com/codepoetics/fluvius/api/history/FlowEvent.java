package com.codepoetics.fluvius.api.history;

import java.util.Map;
import java.util.UUID;

/**
 * An event that has occurred during flow execution.
 *
 * @param <T> The type of the event's serialised data (if it has any).
 */
public abstract class FlowEvent<T> {

  /**
   * Create an event representing the start of a step's execution.
   *
   * @param flowId The ID of the flow being executed.
   * @param stepId The step ID of the step being executed.
   * @param timestamp The time (in milliseconds since epoch) when the event occurred.
   * @param scratchpadState The state of the scratchpad at the point where the step was started.
   * @param <T> The type to which event data is serialised.
   * @return The constructed event.
   */
  public static <T> StepStartedEvent<T> started(UUID flowId, UUID stepId, long timestamp, Map<String, T> scratchpadState) {
    return new StepStartedEvent<>(flowId, stepId, timestamp, scratchpadState);
  }

  /**
   * Create an event representing the failure of a step's execution.
   *
   * @param flowId The ID of the flow being executed.
   * @param stepId The step ID of the step being executed.
   * @param timestamp The time (in milliseconds since epoch) when the event occurred.
   * @param reason The reason why the step failed.
   * @param <T> The type to which event data is serialised.
   * @return The constructed event.
   */
  public static <T> StepFailedEvent<T> failed(UUID flowId, UUID stepId, long timestamp, T reason) {
    return new StepFailedEvent<>(flowId, stepId, timestamp, reason);
  }

  /**
   * Create an event representing the successful completion of a step's execution.
   *
   * @param flowId The ID of the flow being executed.
   * @param stepId The step ID of the step being executed.
   * @param timestamp The time (in milliseconds since epoch) when the event occurred.
   * @param result The result of executing the step.
   * @param <T> The type to which event data is serialised.
   * @return The constructed event.
   */
  public static <T> StepSucceededEvent<T> succeeded(UUID flowId, UUID stepId, long timestamp, T result) {
    return new StepSucceededEvent<>(flowId, stepId, timestamp, result);
  }

  private final UUID flowId;
  private final UUID stepId;
  private final long timestamp;

  protected FlowEvent(UUID flowId, UUID stepId, long timestamp) {
    this.flowId = flowId;
    this.stepId = stepId;
    this.timestamp = timestamp;
  }

  /**
   * Get the id of the flow in which the event occurred.
   * @return The id of the flow in which the event occurred.
   */
  public UUID getFlowId() {
    return flowId;
  }

  /**
   * Get the id of the flow step in which the event occurred.
   * @return The id of the flow step in which the event occurred.
   */
  public UUID getStepId() {
    return stepId;
  }

  /**
   * Get the timestamp (milliseconds since epoch) of the flow event.
   * @return The timestamp (milliseconds since epoch) of the flow event.
   */
  public long getTimestamp() {
    return timestamp;
  }

  /**
   * Translate this event to some other type using the provided event translator.
   *
   * @param translator The translator to use to translate this event.
   * @param <V> The target type of translation.
   * @return The translated event.
   */
  public abstract <V> V translate(FlowEventTranslator<T, V> translator);
}
