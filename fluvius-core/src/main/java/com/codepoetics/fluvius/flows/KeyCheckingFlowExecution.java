package com.codepoetics.fluvius.flows;

import com.codepoetics.fluvius.api.*;
import com.codepoetics.fluvius.api.scratchpad.Key;
import com.codepoetics.fluvius.api.scratchpad.KeyValue;
import com.codepoetics.fluvius.api.scratchpad.Scratchpad;
import com.codepoetics.fluvius.exceptions.MissingKeysException;
import com.codepoetics.fluvius.execution.AbstractFlowExecution;
import com.codepoetics.fluvius.scratchpad.Scratchpads;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Flow execution that checks that all required keys are provided before executing the flow.
 * @param <T> The type of value returned by executing the flow.
 */
public final class KeyCheckingFlowExecution<T> extends AbstractFlowExecution<T> {

  /**
   * Create a key-checking flow execution using the supplied flow and visitor.
   * @param flow The flow to build flow execution for.
   * @param visitor The visitor to use to compile the flow.
   * @param <T> The type of value returned by executing the flow.
   * @return The constructed flow execution.
   */
  public static <T> FlowExecution<T> forFlow(final Flow<T> flow, final FlowVisitor<Action> visitor) {
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
  public static <T> FlowExecution<T> forAction(final Action action, final Set<Key<?>> requiredKeys, final Key<T> providedKey) {
    return new KeyCheckingFlowExecution<>(action, requiredKeys, providedKey);
  }

  private final Action action;
  private final Set<Key<?>> requiredKeys;
  private final Key<T> providedKey;

  KeyCheckingFlowExecution(final Action action, final Set<Key<?>> requiredKeys, final Key<T> providedKey) {
    this.action = action;
    this.requiredKeys = requiredKeys;
    this.providedKey = providedKey;
  }

  private Set<Key<?>> getMissingKeys(final Scratchpad initialScratchpad) {
    final Set<Key<?>> missingKeys = new HashSet<>();
    for (final Key<?> requiredKey : requiredKeys) {
      if (!initialScratchpad.containsKey(requiredKey)) {
        missingKeys.add(requiredKey);
      }
    }
    return missingKeys;
  }

  @Override
  public T run(final UUID flowId, final Scratchpad initialScratchpad) {
    final Set<Key<?>> missingKeys = getMissingKeys(initialScratchpad);

    if (!missingKeys.isEmpty()) {
      throw MissingKeysException.create(missingKeys);
    }

    final Scratchpad finalScratchpad = action.run(flowId, initialScratchpad.locked());
    return finalScratchpad.get(providedKey);
  }

  @Override
  public T run(final UUID flowId, final KeyValue... initialKeyValues) {
    return run(flowId, Scratchpads.create(initialKeyValues));
  }

  @Override
  public Runnable asAsync(final UUID flowId, final FlowResultCallback<T> callback, final Scratchpad initialScratchpad) {
    return new RunWithCallback<>(flowId, initialScratchpad, callback, this);
  }

  private static final class RunWithCallback<T> implements Runnable {
    private final UUID flowId;
    private final Scratchpad initialScratchpad;
    private final FlowResultCallback<T> callback;
    private final KeyCheckingFlowExecution<T> execution;

    private RunWithCallback(final UUID flowId, final Scratchpad initialScratchpad, final FlowResultCallback<T> callback, final KeyCheckingFlowExecution<T> execution) {
      this.flowId = flowId;
      this.initialScratchpad = initialScratchpad;
      this.callback = callback;
      this.execution = execution;
    }

    @Override
    public void run() {
      T result = null;
      try {
        result = execution.run(flowId, initialScratchpad);
      } catch (final Exception e) {
        callback.onFailure(flowId, e);
      }
      if (result != null) {
        callback.onSuccess(flowId, result);
      }
    }
  }
}
