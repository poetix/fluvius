package com.codepoetics.fluvius.flows;

import com.codepoetics.fluvius.api.Action;
import com.codepoetics.fluvius.api.Flow;
import com.codepoetics.fluvius.api.FlowExecution;
import com.codepoetics.fluvius.api.FlowVisitor;
import com.codepoetics.fluvius.api.scratchpad.Key;
import com.codepoetics.fluvius.api.scratchpad.KeyValue;
import com.codepoetics.fluvius.api.scratchpad.Scratchpad;
import com.codepoetics.fluvius.exceptions.MissingKeysException;
import com.codepoetics.fluvius.scratchpad.Scratchpads;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Flow execution that checks that all required keys are provided before executing the flow.
 * @param <T> The type of value returned by executing the flow.
 */
public final class KeyCheckingFlowExecution<T> implements FlowExecution<T> {

  /**
   * Create a key-checking flow execution using the supplied flow and visitor.
   * @param flow The flow to build flow execution for.
   * @param visitor The visitor to use to compile the flow.
   * @param <T> The type of value returned by executing the flow.
   * @return The constructed flow execution.
   */
  public static <T> FlowExecution<T> forFlow(Flow<T> flow, FlowVisitor<Action> visitor) {
    return forAction(flow.visit(visitor), flow.getRequiredKeys(), flow.getProvidedKey());
  }

  /**
   * Create a key-checking flow execution using the supplied action, required keys and provided key.
   * @param action The flow to build flow execution for.
   * @param requiredKeys The keys that must be in the {@link Scratchpad} for the flow to execute.
   * @param providedKey The keys that will be written into the {@link Scratchpad} when the flow has completed.
   * @param <T> The type of value returned by executing the flow.
   * @return The constructed flow execution.
   */
  public static <T> FlowExecution<T> forAction(Action action, Set<Key<?>> requiredKeys, Key<T> providedKey) {
    return new KeyCheckingFlowExecution<>(action, requiredKeys, providedKey);
  }

  private final Action action;
  private final Set<Key<?>> requiredKeys;
  private final Key<T> providedKey;

  KeyCheckingFlowExecution(Action action, Set<Key<?>> requiredKeys, Key<T> providedKey) {
    this.action = action;
    this.requiredKeys = requiredKeys;
    this.providedKey = providedKey;
  }

  private Set<Key<?>> getMissingKeys(Scratchpad initialScratchpad) {
    Set<Key<?>> missingKeys = new HashSet<>();
    for (Key<?> requiredKey : requiredKeys) {
      if (!initialScratchpad.containsKey(requiredKey)) {
        missingKeys.add(requiredKey);
      }
    }
    return missingKeys;
  }

  @Override
  public T run(UUID flowId, Scratchpad initialScratchpad) {
    Set<Key<?>> missingKeys = getMissingKeys(initialScratchpad);

    if (!missingKeys.isEmpty()) {
      throw MissingKeysException.create(missingKeys);
    }

    Scratchpad finalScratchpad = action.run(flowId, initialScratchpad.locked());
    return finalScratchpad.get(providedKey);
  }

  @Override
  public T run(UUID flowId, KeyValue... initialKeyValues) {
    return run(flowId, Scratchpads.create(initialKeyValues));
  }
}
