package com.codepoetics.fluvius.api.history;

import com.codepoetics.fluvius.api.tracing.TraceMap;

import java.util.List;
import java.util.UUID;

/**
 * Represents the entire history of a flow's execution.
 *
 * @param <T> The type to which the flow's event data has been serialised.
 */
public interface FlowHistory<T> {

  /**
   * Get the id of the flow to which this history belongs.
   * @return The id of the flow to which this history belongs.
   */
  UUID getFlowId();

  /**
   * Get a {@link TraceMap} representing all of the possible execution paths for this flow.
   *
   * @return A {@link TraceMap} representing all of the possible execution paths for this flow.
   */
  TraceMap getTraceMap();

  /**
   * Get the event history of this flow's execution, consisting of all the {@link FlowEvent}s that have been emitted during execution.
   * @return The event history of this flow's execution.
   */
  List<FlowEvent<T>> getEventHistory();

  /**
   * Get a translated event history, translated using the provided {@link FlowEventTranslator}.
   *
   * @param translator The event translator to use.
   * @param <V> The type to translate flow events into.
   * @return The translated event history.
   */
  <V> List<V> getTranslatedEventHistory(FlowEventTranslator<T, V> translator);

}
