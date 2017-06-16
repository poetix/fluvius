package com.codepoetics.fluvius.api.history;

import java.util.UUID;

/**
 * An event that has occurred during flow execution.
 *
 * @param <T> The type of the event's serialised data (if it has any).
 */
public interface FlowEvent<T> {

  /**
   * Get the id of the flow in which the event occurred.
   * @return The id of the flow in which the event occurred.
   */
  UUID getFlowId();

  /**
   * Get the id of the flow step in which the event occurred.
   * @return The id of the flow step in which the event occurred.
   */
  UUID getStepId();

  /**
   * Get the timestamp (milliseconds since epoch) of the flow event.
   * @return The timestamp (milliseconds since epoch) of the flow event.
   */
  long getTimestamp();

  /**
   * Translate this event to some other type using the provided event translator.
   *
   * @param translator The translator to use to translate this event.
   * @param <V> The target type of translation.
   * @return The translated event.
   */
  <V> V translate(FlowEventTranslator<T, V> translator);
}
