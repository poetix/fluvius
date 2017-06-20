package com.codepoetics.fluvius.history;

import com.codepoetics.fluvius.api.*;
import com.codepoetics.fluvius.api.compilation.FlowCompiler;
import com.codepoetics.fluvius.api.history.*;
import com.codepoetics.fluvius.api.tracing.TraceMap;

/**
 * Utility class for creating {@link FlowHistoryRepository}s and {@link FlowCompiler}s that will compile {@link Flow}s that record {@link FlowEvent}s to them.
 */
public final class History {

  private History() {
  }

  /**
   * Create an in-memory {@link FlowHistoryRepository} using the provided {@link EventDataSerialiser} to serialise flow event data.
   *
   * @param serialiser The serialiser to use to serialise flow event data.
   * @param <T> The type to which flow event data will be serialised.
   * @return The constructed flow history repository.
   */
  public static <T> FlowHistoryRepository<T> createInMemoryRepository(EventDataSerialiser<T> serialiser) {
    return createRepository(
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
  public static <T> FlowHistoryRepository<T> createRepository(
      FlowEventStore<T> eventStore,
      EventDataSerialiser<T> serialiser,
      TraceMapRepository traceMapRepository) {
    return createRepository(
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
  public static <T> FlowHistoryRepository<T> createRepository(
      FlowEventRepository<T> eventRepository,
      TraceMapRepository traceMapRepository) {
    return DefaultFlowHistoryRepository.using(eventRepository, traceMapRepository);
  }

  /**
   * Create a {@link FlowCompiler} that will compile {@link Flow}s to {@link FlowExecution}s that will record {@link FlowEvent}s to the provided {@link FlowHistoryRepository} on execution.
   *
   * @param repository The repository to which events will be written.
   * @param visitor The visitor to use to compile {@link Flow}s into executable {@link Action}s.
   * @param <T> The type to which flow event data will be serialised.
   * @return The constructed flow compiler.
   */
  public static <T> FlowCompiler makeCompiler(FlowHistoryRepository<T> repository, FlowVisitor<Action> visitor) {
    return RecordingFlowCompiler.using(repository, visitor);
  }

}
