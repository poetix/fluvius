package com.codepoetics.fluvius.api.history;

import com.codepoetics.fluvius.api.tracing.TraceEventListener;
import com.codepoetics.fluvius.api.tracing.TraceMap;

import java.util.UUID;

/**
 * A repository which stores the {@link TraceMap} and history of {@link FlowEvent}s for executed flows.
 *
 * @param <T> The type to which {@link FlowEvent} data has been serialised.
 */
public interface FlowHistoryRepository<T> extends TraceEventListener {

  /**
   * Store the {@link TraceMap} giving a map of all of the flow's possible execution paths.
   *
   * @param flowId The flow id to associated the stored trace map with.
   * @param traceMap The trace map to store.
   */

  void storeTraceMap(UUID flowId, TraceMap traceMap);

  /**
   * Retrieve the {@link FlowHistory} for the given flow id.
   *
   * @param flowId The flow id to retrieve the flow history for.
   * @return The retrieved FlowHistory.
   */
  FlowHistory<T> getFlowHistory(UUID flowId);

}
