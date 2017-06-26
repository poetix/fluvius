package com.codepoetics.fluvius.api.history;

import com.codepoetics.fluvius.api.tracing.TraceMap;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents the entire history of a flow's execution.
 *
 * @param <T> The type to which the flow's event data has been serialised.
 */
public final class FlowHistory<T> {

  public static <T> FlowHistory<T> of(UUID flowId, TraceMap traceMap, List<FlowEvent<T>> eventHistory) {
    return new FlowHistory<>(flowId, traceMap, eventHistory);
  }

  private final UUID flowId;
  private final TraceMap traceMap;
  private final List<FlowEvent<T>> eventHistory;

  private FlowHistory(UUID flowId, TraceMap traceMap, List<FlowEvent<T>> eventHistory) {
    this.flowId = flowId;
    this.traceMap = traceMap;
    this.eventHistory = eventHistory;
  }

  /**
   * Get the id of the flow to which this history belongs.
   * @return The id of the flow to which this history belongs.
   */
  public UUID getFlowId() {
    return flowId;
  }

  /**
   * Get a {@link TraceMap} representing all of the possible execution paths for this flow.
   *
   * @return A {@link TraceMap} representing all of the possible execution paths for this flow.
   */
  public TraceMap getTraceMap() {
    return traceMap;
  }

  /**
   * Get the event history of this flow's execution, consisting of all the {@link FlowEvent}s that have been emitted during execution.
   * @return The event history of this flow's execution.
   */
  public List<FlowEvent<T>> getEventHistory() {
    return eventHistory;
  }

  /**
   * Get a translated event history, translated using the provided {@link FlowEventTranslator}.
   *
   * @param translator The event translator to use.
   * @param <V> The type to translate flow events into.
   * @return The translated event history.
   */
  public <V> List<V> getTranslatedEventHistory(FlowEventTranslator<T, V> translator) {
    List<V> translatedHistory = new ArrayList<>(eventHistory.size());
    for (FlowEvent<T> event : eventHistory) {
      translatedHistory.add(event.translate(translator));
    }
    return translatedHistory;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Flow history for ").append(flowId).append("\nTrace map: ").append(traceMap).append("\nHistory:");
    for (FlowEvent<T> event : eventHistory) {
      sb.append("\n").append(event);
    }
    return sb.toString();
  }


}
