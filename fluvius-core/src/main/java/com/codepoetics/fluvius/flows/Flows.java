package com.codepoetics.fluvius.flows;

import com.codepoetics.fluvius.api.*;
import com.codepoetics.fluvius.api.logging.FlowLogger;
import com.codepoetics.fluvius.api.scratchpad.Key;
import com.codepoetics.fluvius.api.scratchpad.KeyValue;
import com.codepoetics.fluvius.api.scratchpad.Scratchpad;
import com.codepoetics.fluvius.api.tracing.TraceEventListener;
import com.codepoetics.fluvius.api.tracing.TracedFlowExecution;
import com.codepoetics.fluvius.conditions.Conditions;
import com.codepoetics.fluvius.describers.PrettyPrintingDescriptionWriter;
import com.codepoetics.fluvius.execution.KeyCheckingFlowExecution;
import com.codepoetics.fluvius.execution.TraceMapCapturingFlowExecution;
import com.codepoetics.fluvius.scratchpad.Scratchpads;
import com.codepoetics.fluvius.tracing.TraceMaps;
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
  public static String prettyPrint(Flow<?> flow) {
    PrettyPrintingDescriptionWriter descriptionWriter = PrettyPrintingDescriptionWriter.create();
    TraceMaps.describe(TraceMaps.getTraceMap(flow), descriptionWriter);
    return descriptionWriter.toString();
  }

  /**
   * Start defining a Flow which takes values from the given input keys.
   *
   * @param inputKeys The keys from which the Flow takes values.
   * @return The next stage of the Fluent API.
   */
  public static InputKeysCapture from(Key<?>... inputKeys) {
    return Fluent.inputKeysCapture(inputKeys);
  }

  /**
   * Start defining a Flow which will write a value to the given target key.
   *
   * @param target The key to write the Flow result to.
   * @param <O>    The type of the Flow result.
   * @return The next stage of the Fluent API.
   */
  public static <O> TargetCapture<O> obtaining(Key<O> target) {
    return Fluent.targetCapture(target);
  }

  public static <T> FlowExecution<T> compile(Flow<T> flow) {
    return compile(flow, Visitors.getDefault());
  }

  public static <T> FlowExecution<T> compile(Flow<T> flow, FlowVisitor<Action> flowVisitor) {
    return KeyCheckingFlowExecution.forFlow(flow, flowVisitor);
  }

  public static <T> FlowExecution<T> compileLogging(Flow<T> flow, FlowLogger logger) {
    return compile(flow, Visitors.logging(Visitors.getDefault(), logger));
  }

  public static <T> TracedFlowExecution<T> compileTracing(Flow<T> flow, TraceEventListener listener) {
    return compileTracing(flow, listener, Visitors.getDefault());
  }

  public static <T> TracedFlowExecution<T> compileTracing(Flow<T> flow, TraceEventListener listener, FlowVisitor<Action> flowVisitor) {
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
  public static <T> T run(Flow<T> flow, FlowVisitor<Action> flowVisitor, Scratchpad initialScratchpad) throws Exception {
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
  public static <T> T run(Flow<T> flow, FlowVisitor<Action> flowVisitor, KeyValue... keyValues) throws Exception {
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
  public static <T> BranchBuilder<T> branch(Condition condition, Flow<T> ifTrue) {
    return BranchBuilder.startingWith(condition, ifTrue);
  }

  /**
   * The second stage of a fluent API for defining branching {@link Flow}s that respond to success/failure of the previous flow.
   * @param <V> The type of the value to be returned by the branching {@link Flow} constructed using this API.
   */
  public interface SuccessCapture<V> {
    /**
     * Capture the {@link Flow} to be executed if the previous Flow failed.
     * @param failureFlow The flow to be executed if the previous Flow failed.
     * @return The constructed branching {@link Flow}.
     */
    Flow<V> otherwise(Flow<V> failureFlow);
  }

  /**
   * Construct a branching {@link Flow} that responds to the success or failure of a previous flow.
   *
   * @param key The key to which the result of the previous flow will have been written.
   * @return The next stage in the fluent API that constructs the branching {@link Flow}.
   */
  public static <V> SuccessCapture<V> onSuccess(final Key<?> key, final Flow<V> successFlow) {
    return new SuccessCapture<V>() {
      @Override
      public Flow<V> otherwise(Flow<V> failureFlow) {
        return Flows.branch(
            Conditions.keyRecordsFailure(key), failureFlow)
            .otherwise(successFlow);
      }
    };
  }

  /**
   * Construct a {@link Flow} that recovers from an exception returned by a previous flow.
   *
   * @param failureKey The key against which the failure reason was recorded.
   * @return The next state in the fluent API that constructs the recovery Flow.
   */
  public static FailureKeyCapture recoverFrom(Key<?> failureKey) {
    return new FailureKeyCapture(failureKey);
  }


}
