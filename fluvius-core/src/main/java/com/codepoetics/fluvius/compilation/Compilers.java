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

/**
 * Utility class providing a fluent "builder" API for assembling {@link FlowCompiler}s with the required properties.
 */
public final class Compilers {

  private Compilers() {
  }

  /**
   * Obtain a fluent "builder" for specifying the properties of a {@link FlowCompiler}.
   *
   * @return A fluent "builder" for specifying the properties of a {@link FlowCompiler}.
   */
  public static Builder<FlowCompiler> builder() {
    return new Builder<>(Visitors.getDefault(), new CompilerMaker<FlowCompiler>() {
      @Override
      public FlowCompiler makeCompiler(FlowVisitor<Action> input) {
        return new VisitingCompiler(input);
      }
    });
  }

  private interface CompilerMaker<C> {
    C makeCompiler(FlowVisitor<Action> visitor);
  }

  /**
   * A fluent builder for specifying the properties of a {@link FlowCompiler} or {@link TracedFlowCompiler}.
   * @param <C> The type of the compiler to build.
   */
  public static final class Builder<C> {
    private final FlowVisitor<Action> visitor;
    private final CompilerMaker<C> compilerMaker;

    private Builder(FlowVisitor<Action> visitor, CompilerMaker<C> compilerMaker) {
      this.visitor = visitor;
      this.compilerMaker = compilerMaker;
    }

    /**
     * Specifies that {@link FlowExecution}s compiled by the constructed compiler will log messages to the console.
     *
     * @return A builder that will build the compiler as specified.
     */
    public Builder<C> loggingToConsole() {
      return loggingTo(Loggers.getConsoleLogger());
    }

    /**
     * Specifies that {@link FlowExecution}s compiled by the constructed compiler will log messages to the provided {@link FlowLogger}.
     *
     * @return A builder that will build the compiler as specified.
     */
    public Builder<C> loggingTo(FlowLogger logger) {
      return new Builder<>(Visitors.logging(visitor, logger), compilerMaker);
    }

    /**
     * Specifies that {@link FlowExecution}s compiled by the constructed compiler will try to enforce the rule that values stored in the {@link com.codepoetics.fluvius.api.scratchpad.Scratchpad} cannot be mutated.
     *
     * @return A builder that will build the compiler as specified.
     */
    public Builder<C> mutationChecking() {
      return new Builder<>(Visitors.mutationChecking(visitor), compilerMaker);
    }

    /**
     * Specifies that {@link FlowExecution}s compiled by the constructed compiler will record a trace of their execution history to the provided {@link FlowHistoryRepository}.
     *
     * @param repository The repository to which exeuction traces will be written.
     * @return A builder that will build the compiler as specified.
     */
    public Builder<FlowCompiler> recordingTo(final FlowHistoryRepository<?> repository) {
      return new Builder<>(visitor, new CompilerMaker<FlowCompiler>() {
        @Override
        public FlowCompiler makeCompiler(FlowVisitor<Action> input) {
          return History.makeCompiler(repository, visitor);
        }
      });
    }

    /**
     * Specifies the {@link FlowExecution}s compiled by the constructed {@link TracedFlowCompiler} will emit trace messages to the provided {@link TraceEventListener}.
     *
     * @param eventListener The event listener that will receiver trace messages from {@link FlowExecution}s compiled by the constructed compiler.
     * @return A builder that will build the compiler as specified.
     */
    public Builder<TracedFlowCompiler> tracingWith(final TraceEventListener eventListener) {
      return new Builder<>(visitor, new CompilerMaker<TracedFlowCompiler>() {
        @Override
        public TracedFlowCompiler makeCompiler(final FlowVisitor<Action> input) {
          return new TracedFlowCompiler() {
            @Override
            public <T> TracedFlowExecution<T> compile(Flow<T> flow) {
              return TraceMapCapturingFlowExecution.forFlow(flow, TracingFlowVisitor.wrapping(eventListener, input));
            }
          };
        }
      });
    }

    /**
     * Build and return the compiler as specified.
     *
     * @return The constructed compiler.
     */
    public C build() {
      return compilerMaker.makeCompiler(visitor);
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
