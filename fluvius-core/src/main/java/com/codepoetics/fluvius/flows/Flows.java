package com.codepoetics.fluvius.flows;

import com.codepoetics.fluvius.api.*;
import com.codepoetics.fluvius.api.logging.FlowLogger;
import com.codepoetics.fluvius.api.scratchpad.Key;
import com.codepoetics.fluvius.api.scratchpad.KeyValue;
import com.codepoetics.fluvius.api.scratchpad.Scratchpad;
import com.codepoetics.fluvius.api.tracing.TraceEventListener;
import com.codepoetics.fluvius.api.tracing.TraceMap;
import com.codepoetics.fluvius.api.tracing.TracedAction;
import com.codepoetics.fluvius.api.tracing.TracedFlowExecution;
import com.codepoetics.fluvius.describers.FlowDescriber;
import com.codepoetics.fluvius.describers.PrettyPrintingDescriptionWriter;
import com.codepoetics.fluvius.execution.AbstractFlowExecution;
import com.codepoetics.fluvius.scratchpad.Scratchpads;
import com.codepoetics.fluvius.tracing.TracingFlowVisitor;
import com.codepoetics.fluvius.visitors.Visitors;

import java.util.UUID;

/**
 * Provides the public API for constructing flows.
 */
public final class Flows {

  private Flows() {
  }

  /**
   * Obtain a pretty-printed string representation of the supplied Flow.
   *
   * @param flow The Flow to pretty-print.
   * @return A string representation of the supplied Flow.
   */
  public static String prettyPrint(final Flow<?> flow) {
    final PrettyPrintingDescriptionWriter descriptionWriter = PrettyPrintingDescriptionWriter.create();
    FlowDescriber.describe(flow).writeTo(descriptionWriter);
    return descriptionWriter.toString();
  }

  /**
   * Start defining a Flow which takes values from the given input keys.
   *
   * @param inputKeys The keys from which the Flow takes values.
   * @return The next stage of the Fluent API.
   */
  public static InputKeysCapture from(final Key<?>... inputKeys) {
    return Fluent.inputKeysCapture(inputKeys);
  }

  /**
   * Start defining a Flow which will write a value to the given target key.
   *
   * @param target The key to write the Flow result to.
   * @param <O>    The type of the Flow result.
   * @return The next stage of the Fluent API.
   */
  public static <O> TargetCapture<O> obtaining(final Key<O> target) {
    return Fluent.targetCapture(target);
  }

  public static <T> FlowExecution<T> compile(final Flow<T> flow) {
    return compile(flow, Visitors.getDefault());
  }

  public static <T> FlowExecution<T> compile(final Flow<T> flow, final FlowVisitor<Action> flowVisitor) {
    return KeyCheckingFlowExecution.forFlow(flow, flowVisitor);
  }

  public static <T> FlowExecution<T> compileLogging(final Flow<T> flow, final FlowLogger logger) {
    return compile(flow, Visitors.logging(Visitors.getDefault(), logger));
  }

  public static <T> TracedFlowExecution<T> compileTracing(final Flow<T> flow, final TraceEventListener listener) {
    return compileTracing(flow, listener, Visitors.getDefault());
  }

  public static <T> TracedFlowExecution<T> compileTracing(final Flow<T> flow, final TraceEventListener listener, final FlowVisitor<Action> flowVisitor) {
    return TraceMapCapturingFlowExecution.forFlow(flow, TracingFlowVisitor.wrapping(listener, flowVisitor));
  }

  /**
   * Run the supplied Flow, using the given FlowVisitor to construct an execution plan, providing it with the given initial Scratchpad.
   *
   * @param flow              The Flow to run.
   * @param flowVisitor       The FlowVisitor to use to construct an execution plan.
   * @param initialScratchpad The Scratchpad to start with.
   * @param <T>               The type of the Flow result.
   * @return The result of running the Flow.
   */
  @Deprecated
  public static <T> T run(final Flow<T> flow, final FlowVisitor<Action> flowVisitor, final Scratchpad initialScratchpad) {
    return KeyCheckingFlowExecution.forFlow(flow, flowVisitor).run(UUID.randomUUID(), initialScratchpad);
  }

  /**
   * Run the supplied Flow, using the given FlowVisitor to construct an execution plan, providing it with an initial Scratchpad containing the given key values.
   *
   * @param flow        The Flow to run.
   * @param flowVisitor The FlowVisitor to use to construct an execution plan.
   * @param keyValues   The key values to write into the initial Scratchpad.
   * @param <T>         The type of the Flow result.
   * @return The result of running the Flow.
   */
  @SuppressWarnings("deprecation")
  @Deprecated
  public static <T> T run(final Flow<T> flow, final FlowVisitor<Action> flowVisitor, final KeyValue... keyValues) {
    return run(flow, flowVisitor, Scratchpads.create(keyValues));
  }

  /**
   * Start defining a branching flow, beginning with the supplied Condition and Flow to execute if the condition is met.
   *
   * @param condition The first Condition in the branching flow.
   * @param ifTrue    The Flow to execute if the condition is met.
   * @param <T>       The type of the value returned by the Flow.
   * @return The next stage of the Fluent API.
   */
  public static <T> BranchBuilder<T> branch(final Condition condition, final Flow<T> ifTrue) {
    return BranchBuilder.startingWith(condition, ifTrue);
  }

  private static final class TraceMapCapturingFlowExecution<T> extends AbstractFlowExecution<T> implements TracedFlowExecution<T> {

    private static <T> TracedFlowExecution<T> forFlow(final Flow<T> flow, final FlowVisitor<TracedAction> visitor) {
      final TracedAction action = flow.visit(visitor);
      return new TraceMapCapturingFlowExecution<>(
          KeyCheckingFlowExecution.forAction(action, flow.getRequiredKeys(), flow.getProvidedKey()),
          action.getTraceMap()
      );
    }

    private final FlowExecution<T> execution;
    private final TraceMap traceMap;

    private TraceMapCapturingFlowExecution(final FlowExecution<T> execution, final TraceMap traceMap) {
      this.execution = execution;
      this.traceMap = traceMap;
    }

    @Override
    public TraceMap getTraceMap() {
      return traceMap;
    }

    @Override
    public T run(final UUID flowId, final Scratchpad initialScratchpad) {
      return execution.run(flowId, initialScratchpad);
    }

    @Override
    public Runnable asAsync(final UUID flowId, final FlowResultCallback<T> callback, final Scratchpad initialScratchpad) {
      return execution.asAsync(flowId, callback, initialScratchpad);
    }
  }


}
