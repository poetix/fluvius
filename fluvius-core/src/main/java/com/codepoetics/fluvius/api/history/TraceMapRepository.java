package com.codepoetics.fluvius.api.history;

import com.codepoetics.fluvius.api.tracing.TraceMap;

import java.util.UUID;

/**
 * A repository which stores {@link TraceMap}s for flows.
 */
public interface TraceMapRepository {

  /**
   * Store the {@link TraceMap} for the given flow id.
   *
   * @param flowId The flow id to associate the provided trace map with.
   * @param traceMap The trace map to store.
   */
  void storeTraceMap(UUID flowId, TraceMap traceMap);

  /**
   * Fetch the {@link TraceMap} associated with the given flow id.
   *
   * @param flowId The id of the flow to fetch the trace map for.
   * @return The stored trace map.
   * @throws com.codepoetics.fluvius.exceptions.TraceMapNotFoundException If no trace map can be found for the given flow id.
   */
  TraceMap getTraceMap(UUID flowId);
}
