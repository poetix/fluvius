package com.codepoetics.fluvius.api.history;

/**
 * Translates flow events into some other type.
 *
 * @param <T> The type to which the flow events' data has been serialised.
 * @param <V> The type to translate flow events to.
 */
public interface FlowEventTranslator<T, V> {

  /**
   * Translate a {@link StepStartedEvent} to the required type.
   *
   * @param event The event to translate.
   * @return The translated event.
   */
  V translateStepStartedEvent(StepStartedEvent<T> event);

  /**
   * Translate a {@link StepSucceededEvent} to the required type.
   *
   * @param event The event to translate.
   * @return The translated event.
   */
  V translateStepSucceededEvent(StepSucceededEvent<T> event);

  /**
   * Translate a {@link StepFailedEvent} to the required type.
   *
   * @param event The event to translate.
   * @return The translated event.
   */
  V translateStepFailedEvent(StepFailedEvent<T> event);

}
