package com.codepoetics.fluvius.history;

import com.codepoetics.fluvius.api.*;
import com.codepoetics.fluvius.api.compilation.FlowCompiler;
import com.codepoetics.fluvius.api.history.FlowHistoryRepository;
import com.codepoetics.fluvius.api.scratchpad.Scratchpad;
import com.codepoetics.fluvius.api.tracing.TraceMap;
import com.codepoetics.fluvius.api.tracing.TracedFlowExecution;
import com.codepoetics.fluvius.execution.AbstractFlowExecution;
import com.codepoetics.fluvius.flows.Flows;

import java.util.UUID;

final class RecordingFlowCompiler implements FlowCompiler {

  static FlowCompiler using(FlowHistoryRepository<?> repository, FlowVisitor<Action> visitor) {
    return new RecordingFlowCompiler(repository, visitor);
  }

  private final FlowHistoryRepository<?> repository;
  private final FlowVisitor<Action> visitor;

  private RecordingFlowCompiler(FlowHistoryRepository<?> repository, FlowVisitor<Action> visitor) {
    this.repository = repository;
    this.visitor = visitor;
  }

  @Override
  public <T> FlowExecution<T> compile(Flow<T> flow) {
    return RecordingFlowExecution.forFlow(flow, repository, visitor);
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
