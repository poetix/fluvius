package com.codepoetics.fluvius.history;

import com.codepoetics.fluvius.api.*;
import com.codepoetics.fluvius.api.history.*;
import com.codepoetics.fluvius.api.scratchpad.Scratchpad;
import com.codepoetics.fluvius.api.tracing.TraceMap;
import com.codepoetics.fluvius.api.tracing.TracedFlowExecution;
import com.codepoetics.fluvius.execution.AbstractFlowExecution;
import com.codepoetics.fluvius.flows.Flows;

import java.util.UUID;

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

  public static <T, V> FlowExecution<T> compileRecording(final Flow<T> flow, final FlowHistoryRepository<V> repository, final FlowVisitor<Action> flowVisitor) {
    return RecordingFlowExecution.forFlow(flow, repository, flowVisitor);
  }

  private static final class RecordingFlowExecution<T, V> extends AbstractFlowExecution<T> {

    private static <T, V> FlowExecution<T> forFlow(final Flow<T> flow, final FlowHistoryRepository<V> repository, final FlowVisitor<Action> flowVisitor) {
      return new RecordingFlowExecution<>(
          Flows.compileTracing(flow, repository, flowVisitor),
          repository);
    }

    private final TracedFlowExecution<T> tracedFlowExecution;
    private final FlowHistoryRepository<V> repository;

    private RecordingFlowExecution(final TracedFlowExecution<T> tracedFlowExecution, final FlowHistoryRepository<V> repository) {
      this.tracedFlowExecution = tracedFlowExecution;
      this.repository = repository;
    }

    @Override
    public T run(final UUID flowId, final Scratchpad initialScratchpad) {
      final TraceMap traceMap = tracedFlowExecution.getTraceMap();
      repository.storeTraceMap(flowId, traceMap);

      return tracedFlowExecution.run(flowId, initialScratchpad);
    }

    @Override
    public Runnable asAsync(final UUID flowId, final FlowResultCallback<T> callback, final Scratchpad initialScratchpad) {
      final TraceMap traceMap = tracedFlowExecution.getTraceMap();
      repository.storeTraceMap(flowId, traceMap);

      return tracedFlowExecution.asAsync(flowId, callback, initialScratchpad);
    }
  }
}
