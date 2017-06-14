package com.codepoetics.fluvius.compilation;

import com.codepoetics.fluvius.api.Action;
import com.codepoetics.fluvius.api.Flow;
import com.codepoetics.fluvius.api.FlowExecution;
import com.codepoetics.fluvius.api.FlowVisitor;
import com.codepoetics.fluvius.api.compilation.FlowCompiler;
import com.codepoetics.fluvius.api.compilation.TracedFlowCompiler;
import com.codepoetics.fluvius.api.functional.F1;
import com.codepoetics.fluvius.api.history.FlowHistoryRepository;
import com.codepoetics.fluvius.api.logging.FlowLogger;
import com.codepoetics.fluvius.api.tracing.TraceEventListener;
import com.codepoetics.fluvius.api.tracing.TracedFlowExecution;
import com.codepoetics.fluvius.execution.KeyCheckingFlowExecution;
import com.codepoetics.fluvius.execution.TraceMapCapturingFlowExecution;
import com.codepoetics.fluvius.history.History;
import com.codepoetics.fluvius.logging.Loggers;
import com.codepoetics.fluvius.tracing.TracingFlowVisitor;
import com.codepoetics.fluvius.visitors.Visitors;

public final class Compilers {

  private Compilers() {
  }

  public static Builder<FlowCompiler> builder() {
    return new Builder<>(Visitors.getDefault(), new F1<FlowVisitor<Action>, FlowCompiler>() {
      @Override
      public FlowCompiler apply(FlowVisitor<Action> input) {
        return new VisitingCompiler(input);
      }
    });
  }

  public static final class Builder<C> {
    private final FlowVisitor<Action> visitor;
    private final F1<FlowVisitor<Action>, C> compilerMaker;

    private Builder(FlowVisitor<Action> visitor, F1<FlowVisitor<Action>, C> compilerMaker) {
      this.visitor = visitor;
      this.compilerMaker = compilerMaker;
    }

    public Builder<C> loggingToConsole() {
      return loggingTo(Loggers.getConsoleLogger());
    }

    public Builder<C> loggingTo(FlowLogger logger) {
      return new Builder<>(Visitors.logging(visitor, logger), compilerMaker);
    }

    public Builder<C> mutationChecking() {
      return new Builder<>(Visitors.mutationChecking(visitor), compilerMaker);
    }

    public Builder<FlowCompiler> recordingTo(final FlowHistoryRepository<?> repository) {
      return new Builder<FlowCompiler>(visitor, new F1<FlowVisitor<Action>, FlowCompiler>() {
        @Override
        public FlowCompiler apply(FlowVisitor<Action> input) {
          return History.makeCompiler(repository, visitor);
        }
      });
    }

    public Builder<TracedFlowCompiler> tracingWith(final TraceEventListener eventListener) {
      return new Builder<>(visitor, new F1<FlowVisitor<Action>, TracedFlowCompiler>() {
        @Override
        public TracedFlowCompiler apply(final FlowVisitor<Action> input) {
          return new TracedFlowCompiler() {
            @Override
            public <T> TracedFlowExecution<T> compile(Flow<T> flow) {
              return TraceMapCapturingFlowExecution.forFlow(flow, TracingFlowVisitor.wrapping(eventListener, input));
            }
          };
        }
      });
    }

    public C build() {
      return compilerMaker.apply(visitor);
    }
  }

  private static final class VisitingCompiler implements FlowCompiler {
    private final FlowVisitor<Action> visitor;

    private VisitingCompiler(FlowVisitor<Action> visitor) {
      this.visitor = visitor;
    }

    @Override
    public <T> FlowExecution<T> compile(Flow<T> flow) {
      return KeyCheckingFlowExecution.forFlow(flow, visitor);
    }
  }
}
