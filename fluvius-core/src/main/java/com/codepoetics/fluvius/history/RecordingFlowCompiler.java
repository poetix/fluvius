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

    private static <T, V> FlowExecution<T> forFlow(Flow<T> flow, FlowHistoryRepository<V> repository, FlowVisitor<Action> flowVisitor) {
      return new RecordingFlowExecution<>(
          Flows.compileTracing(flow, repository, flowVisitor),
          repository);
    }

    private final TracedFlowExecution<T> tracedFlowExecution;
    private final FlowHistoryRepository<V> repository;

    private RecordingFlowExecution(TracedFlowExecution<T> tracedFlowExecution, FlowHistoryRepository<V> repository) {
      this.tracedFlowExecution = tracedFlowExecution;
      this.repository = repository;
    }

    @Override
    public T run(UUID flowId, Scratchpad initialScratchpad) throws Exception {
      TraceMap traceMap = tracedFlowExecution.getTraceMap();
      repository.storeTraceMap(flowId, traceMap);

      return tracedFlowExecution.run(flowId, initialScratchpad);
    }

    @Override
    public Runnable asAsync(UUID flowId, FlowResultCallback<T> callback, Scratchpad initialScratchpad) {
      TraceMap traceMap = tracedFlowExecution.getTraceMap();
      repository.storeTraceMap(flowId, traceMap);

      return tracedFlowExecution.asAsync(flowId, callback, initialScratchpad);
    }
  }
}
