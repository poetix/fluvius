package com.codepoetics.fluvius.flows;

import com.codepoetics.fluvius.api.Action;
import com.codepoetics.fluvius.api.Condition;
import com.codepoetics.fluvius.api.Flow;
import com.codepoetics.fluvius.api.FlowVisitor;
import com.codepoetics.fluvius.api.scratchpad.Key;
import com.codepoetics.fluvius.api.scratchpad.KeyValue;
import com.codepoetics.fluvius.api.scratchpad.Scratchpad;
import com.codepoetics.fluvius.describers.FlowDescriber;
import com.codepoetics.fluvius.describers.PrettyPrintingDescriptionWriter;
import com.codepoetics.fluvius.exceptions.MissingKeysException;
import com.codepoetics.fluvius.scratchpad.Scratchpads;

import java.util.HashSet;
import java.util.Set;

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
    PrettyPrintingDescriptionWriter descriptionWriter = PrettyPrintingDescriptionWriter.create();
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

  /**
   * Run the supplied Flow, using the given FlowVisitor to construct an execution plan, providing it with the given initial Scratchpad.
   *
   * @param flow              The Flow to run.
   * @param flowVisitor       The FlowVisitor to use to construct an execution plan.
   * @param initialScratchpad The Scratchpad to start with.
   * @param <T>               The type of the Flow result.
   * @return The result of running the Flow.
   */
  public static <T> T run(final Flow<T> flow, final FlowVisitor<Action> flowVisitor, final Scratchpad initialScratchpad) {
    Set<Key<?>> missingKeys = getMissingKeys(flow, initialScratchpad);

    if (!missingKeys.isEmpty()) {
      throw MissingKeysException.create(missingKeys);
    }

    Action action = flow.visit(flowVisitor);
    Scratchpad finalScratchpad = action.run(initialScratchpad);

    return finalScratchpad.get(flow.getProvidedKey());
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
  public static <T> T run(final Flow<T> flow, final FlowVisitor<Action> flowVisitor, final KeyValue... keyValues) {
    return run(flow, flowVisitor, Scratchpads.create(keyValues));
  }

  private static <T> Set<Key<?>> getMissingKeys(final Flow<T> flow, final Scratchpad initialScratchpad) {
    Set<Key<?>> missingKeys = new HashSet<>();
    for (Key<?> inputKey : flow.getRequiredKeys()) {
      if (!initialScratchpad.containsKey(inputKey)) {
        missingKeys.add(inputKey);
      }
    }
    return missingKeys;
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

}
