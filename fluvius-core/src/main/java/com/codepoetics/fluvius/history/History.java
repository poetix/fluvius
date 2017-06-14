package com.codepoetics.fluvius.history;

import com.codepoetics.fluvius.api.*;
import com.codepoetics.fluvius.api.compilation.FlowCompiler;
import com.codepoetics.fluvius.api.history.*;

public final class History {

  private History() {
  }

  public static <T> FlowHistoryRepository<T> createInMemoryRepository(final EventDataSerialiser<T> serialiser) {
    return createRepository(
        InMemoryFlowEventStore.<T>create(),
        serialiser,
        InMemoryTraceMapRepository.create());
  }

  public static <T> FlowHistoryRepository<T> createRepository(
      final FlowEventStore<T> eventStore,
      final EventDataSerialiser<T> serialiser,
      final TraceMapRepository traceMapRepository) {
    return createRepository(
        SerialisingFlowEventRepository.using(eventStore, serialiser),
        traceMapRepository
    );
  }

  public static <T> FlowHistoryRepository<T> createRepository(
      final FlowEventRepository<T> eventRepository,
      final TraceMapRepository traceMapRepository) {
    return DefaultFlowHistoryRepository.using(eventRepository, traceMapRepository);
  }

  public static <T> FlowExecution<T> compileRecording(final Flow<T> flow, final FlowHistoryRepository<?> repository, final FlowVisitor<Action> flowVisitor) {
    return RecordingFlowCompiler.using(repository, flowVisitor).compile(flow);
  }

  public static <T> FlowCompiler makeCompiler(final FlowHistoryRepository<T> repository, final FlowVisitor<Action> visitor) {
    return RecordingFlowCompiler.using(repository, visitor);
  }

}
