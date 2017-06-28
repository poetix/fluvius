package com.codepoetics.fluvius.compilation;

import com.codepoetics.fluvius.api.Action;
import com.codepoetics.fluvius.api.Flow;
import com.codepoetics.fluvius.api.FlowExecution;
import com.codepoetics.fluvius.api.FlowVisitor;
import com.codepoetics.fluvius.api.compilation.FlowCompiler;
import com.codepoetics.fluvius.api.logging.FlowLogger;
import com.codepoetics.fluvius.api.tracing.TraceEventListener;
import com.codepoetics.fluvius.execution.KeyCheckingFlowExecution;
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
  public static Builder builder() {
    return new Builder(Visitors.getDefault());
  }

  /**
   * A fluent builder for specifying the properties of a {@link FlowCompiler}.
   */
  public static final class Builder {
    private final FlowVisitor<Action> visitor;

    private Builder(FlowVisitor<Action> visitor) {
      this.visitor = visitor;
    }

    /**
     * Specifies that {@link FlowExecution}s compiled by the constructed compiler will log messages to the console.
     *
     * @return A builder that will build the compiler as specified.
     */
    public Builder loggingToConsole() {
      return loggingTo(Loggers.getConsoleLogger());
    }

    /**
     * Specifies that {@link FlowExecution}s compiled by the constructed compiler will log messages to the provided {@link FlowLogger}.
     *
     * @return A builder that will build the compiler as specified.
     */
    public Builder loggingTo(FlowLogger logger) {
      return new Builder(Visitors.logging(visitor, logger));
    }

    /**
     * Specifies that {@link FlowExecution}s compiled by the constructed compiler will try to enforce the rule that values stored in the {@link com.codepoetics.fluvius.api.scratchpad.Scratchpad} cannot be mutated.
     *
     * @return A builder that will build the compiler as specified.
     */
    public Builder mutationChecking() {
      return new Builder(Visitors.mutationChecking(visitor));
    }

    /**
     * Specifies that {@link FlowExecution}s compiled by the constructed {@link FlowCompiler} will emit trace messages to the provided {@link TraceEventListener}.
     *
     * @param eventListener The event listener that will receiver trace messages from {@link FlowExecution}s compiled by the constructed compiler.
     * @return A builder that will build the compiler as specified.
     */
    public Builder tracingWith(final TraceEventListener eventListener) {
      return new Builder(TracingFlowVisitor.wrapping(eventListener, visitor));
    }

    /**
     * Build and return the compiler as specified.
     *
     * @return The constructed compiler.
     */
    public FlowCompiler build() {
      return new VisitingCompiler(visitor);
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
