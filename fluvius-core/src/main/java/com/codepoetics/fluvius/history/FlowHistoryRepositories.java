package com.codepoetics.fluvius.history;

import com.codepoetics.fluvius.api.history.*;
import com.codepoetics.fluvius.api.tracing.TraceMap;

/**
 * Utility class for creating {@link FlowHistoryRepository}s out of smaller pieces.
 */
public final class FlowHistoryRepositories {

  private FlowHistoryRepositories() {
  }

  /**
   * Create an in-memory {@link FlowHistoryRepository} using the provided {@link EventDataSerialiser} to serialise flow event data.
   *
   * @param serialiser The serialiser to use to serialise flow event data.
   * @param <T> The type to which flow event data will be serialised.
   * @return The constructed flow history repository.
   */
  public static <T> FlowHistoryRepository<T> createInMemory(EventDataSerialiser<T> serialiser) {
    return create(
        InMemoryFlowEventStore.<T>create(),
        serialiser,
        InMemoryTraceMapRepository.create());
  }

  /**
   * Create a {@link FlowHistoryRepository} that will write flow event data serialised with the provided {@link EventDataSerialiser} to the provided {@link FlowEventStore}, and {@link TraceMap}s to the provided {@link TraceMapRepository}.
   *
   * @param eventStore The event store to use to store flow events.
   * @param serialiser The serialiser to use to serialise flow event data.
   * @param traceMapRepository The repository to store trace maps in.
   * @param <T> The type to which flow event data will be serialised.
   * @return The constructed flow history repository.
   */
  public static <T> FlowHistoryRepository<T> create(
      FlowEventStore<T> eventStore,
      EventDataSerialiser<T> serialiser,
      TraceMapRepository traceMapRepository) {
    return create(
        SerialisingFlowEventRepository.using(eventStore, serialiser),
        traceMapRepository
    );
  }

  /**
   * Create a {@link FlowHistoryRepository} that will write flow event data to the provided {@link FlowEventRepository}, and {@link TraceMap}s to the provided {@link TraceMapRepository}.
   *
   * @param eventRepository The repository to write flow events to.
   * @param traceMapRepository The repository to write trace maps to.
   * @param <T> The type to which flow event data will be serialised.
   * @return The constructed flow history repository.
   */
  public static <T> FlowHistoryRepository<T> create(
      FlowEventRepository<T> eventRepository,
      TraceMapRepository traceMapRepository) {
    return DefaultFlowHistoryRepository.using(eventRepository, traceMapRepository);
  }

}
