package com.codepoetics.fluvius.execution;

import com.codepoetics.fluvius.api.*;
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
public final class KeyCheckingFlowExecution<T> extends AbstractFlowExecution<T> {

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

  static <T> FlowExecution<T> forAction(Action action, Set<Key<?>> requiredKeys, Key<T> providedKey) {
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
  public T run(UUID flowId, Scratchpad initialScratchpad) throws Exception {
    Set<Key<?>> missingKeys = getMissingKeys(initialScratchpad);

    if (!missingKeys.isEmpty()) {
      throw MissingKeysException.create(missingKeys);
    }

    Scratchpad finalScratchpad = action.run(flowId, initialScratchpad.locked());

    if (finalScratchpad.isSuccessful(providedKey)) {
      return finalScratchpad.get(providedKey);
    } else {
      throw (finalScratchpad.getFailureReason(providedKey));
    }
  }

  @Override
  public T run(UUID flowId, KeyValue... initialKeyValues) throws Exception {
    return run(flowId, Scratchpads.create(initialKeyValues));
  }

  @Override
  public Runnable asAsync(UUID flowId, FlowResultCallback<T> callback, Scratchpad initialScratchpad) {
    return new RunWithCallback<>(flowId, initialScratchpad, callback, this);
  }

  private static final class RunWithCallback<T> implements Runnable {
    private final UUID flowId;
    private final Scratchpad initialScratchpad;
    private final FlowResultCallback<T> callback;
    private final KeyCheckingFlowExecution<T> execution;

    private RunWithCallback(UUID flowId, Scratchpad initialScratchpad, FlowResultCallback<T> callback, KeyCheckingFlowExecution<T> execution) {
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
      } catch (Exception e) {
        callback.onFailure(flowId, e);
      }
      if (result != null) {
        callback.onSuccess(flowId, result);
      }
    }
  }
}
