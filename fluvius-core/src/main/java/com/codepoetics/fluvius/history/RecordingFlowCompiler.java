package com.codepoetics.fluvius.history;

import com.codepoetics.fluvius.api.*;
import com.codepoetics.fluvius.api.compilation.FlowCompiler;
import com.codepoetics.fluvius.api.history.FlowHistoryRepository;
import com.codepoetics.fluvius.api.scratchpad.Scratchpad;
import com.codepoetics.fluvius.api.tracing.TraceMap;
import com.codepoetics.fluvius.api.tracing.TracedAction;
import com.codepoetics.fluvius.api.tracing.TracedFlowExecution;
import com.codepoetics.fluvius.execution.AbstractFlowExecution;
import com.codepoetics.fluvius.execution.TraceMapCapturingFlowExecution;
import com.codepoetics.fluvius.tracing.TracingFlowVisitor;

import java.util.UUID;

/**
 * A compiler that compiles a {@link Flow} to a {@link FlowExecution} that records the flow's trace history to a {@link FlowHistoryRepository}.
 */
public final class RecordingFlowCompiler implements FlowCompiler {

  /**
   * Create a compiler that uses the supplied {@link FlowVisitor} to compile a {@link Flow} to a {@link FlowExecution} that records the flow's trace history to the supplied {@link FlowHistoryRepository}.
   * @param repository The repository to record trace histories to.
   * @param visitor The visitor to use to compile the flow.
   * @return The constructed compiler.
   */
  public static FlowCompiler using(FlowHistoryRepository<?> repository, FlowVisitor<Action> visitor) {
    return new RecordingFlowCompiler(repository, TracingFlowVisitor.wrapping(repository, visitor));
  }

  private final FlowHistoryRepository<?> repository;
  private final FlowVisitor<TracedAction> visitor;

  private RecordingFlowCompiler(FlowHistoryRepository<?> repository, FlowVisitor<TracedAction> visitor) {
    this.repository = repository;
    this.visitor = visitor;
  }

  @Override
  public <T> FlowExecution<T> compile(Flow<T> flow) {
    return new RecordingFlowExecution<>(
        TraceMapCapturingFlowExecution.forFlow(flow, visitor),
        repository);
  }

  private static final class RecordingFlowExecution<T, V> extends AbstractFlowExecution<T> {

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
