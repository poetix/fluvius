package com.codepoetics.fluvius.flows;

import com.codepoetics.fluvius.api.*;
import com.codepoetics.fluvius.api.scratchpad.Key;
import com.codepoetics.fluvius.api.scratchpad.KeyValue;
import com.codepoetics.fluvius.api.scratchpad.Scratchpad;
import com.codepoetics.fluvius.conditions.Conditions;
import com.codepoetics.fluvius.describers.PrettyPrintingDescriptionWriter;
import com.codepoetics.fluvius.execution.KeyCheckingFlowExecution;
import com.codepoetics.fluvius.scratchpad.Scratchpads;
import com.codepoetics.fluvius.tracing.TraceMaps;

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

}
