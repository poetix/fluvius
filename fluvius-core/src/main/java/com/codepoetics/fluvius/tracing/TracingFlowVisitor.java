package com.codepoetics.fluvius.tracing;

import com.codepoetics.fluvius.api.*;
import com.codepoetics.fluvius.api.scratchpad.Key;
import com.codepoetics.fluvius.api.scratchpad.Scratchpad;
import com.codepoetics.fluvius.api.tracing.TraceEventListener;
import com.codepoetics.fluvius.api.tracing.TraceMap;

import java.util.*;

/**
 * A {@link FlowVisitor} that creates a {@link TraceMap} and notifies a {@link TraceEventListener} of step execution.
 */
public final class TracingFlowVisitor implements FlowVisitor<Action> {

  /**
   * Create a new tracing FlowVisitor wrapping the supplied listener and visitor.
   *
   * @param listener      The trace event listener to wrap.
   * @param actionVisitor The flow visitor to wrap.
   * @return The constructed flow visitor.
   */
  public static FlowVisitor<Action> wrapping(TraceEventListener listener, FlowVisitor<Action> actionVisitor) {
    return new TracingFlowVisitor(listener, actionVisitor);
  }

  private final TraceEventListener listener;
  private final FlowVisitor<Action> innerVisitor;

  private TracingFlowVisitor(TraceEventListener listener, FlowVisitor<Action> innerVisitor) {
    this.listener = listener;
    this.innerVisitor = innerVisitor;
  }

  @Override
  public <T> Action visitSingle(UUID stepId, Set<Key<?>> requiredKeys, Key<T> providedKey, Operation<T> operation) {
    return new NotifyingAction(
        stepId,
        listener,
        providedKey,
        innerVisitor.visitSingle(stepId, requiredKeys, providedKey, operation));
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> Action visitSequence(UUID stepId, Set<Key<?>> requiredKeys, Key<T> providedKey, List<Action> items) {
    return new NotifyingAction(
        stepId,
        listener,
        providedKey,
        innerVisitor.visitSequence(stepId, requiredKeys, providedKey, (List) items));
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> Action visitBranch(UUID stepId, Set<Key<?>> requiredKeys, Key<T> providedKey, Action defaultBranch, List<Conditional<Action>> conditionalBranches) {
    return new NotifyingAction(
        stepId,
        listener,
        providedKey,
        innerVisitor.visitBranch(
            stepId,
            requiredKeys,
            providedKey,
            defaultBranch,
            conditionalBranches));
  }

  @Override
  public Condition visitCondition(Condition condition) {
    return innerVisitor.visitCondition(condition);
  }

  private static final class NotifyingAction implements Action {

    private final UUID stepId;
    private final TraceEventListener listener;
    private final Key<?> providedKey;
    private final Action action;

    private NotifyingAction(UUID stepId, TraceEventListener listener, Key<?> providedKey, Action action) {
      this.stepId = stepId;
      this.listener = listener;
      this.providedKey = providedKey;
      this.action = action;
    }

    @Override
    public Scratchpad run(UUID flowId, Scratchpad scratchpad) {
      listener.stepStarted(flowId, stepId, keysToNames(scratchpad.toMap()));
      Scratchpad result = action.run(flowId, scratchpad);
      if (result.isSuccessful(providedKey)) {
        listener.stepSucceeded(flowId, stepId, result.get(providedKey));
      } else {
        listener.stepFailed(flowId, stepId, result.getFailureReason(providedKey));
      }
      return result;
    }

    private Map<String, Object> keysToNames(Map<Key<?>, Object> scratchpadState) {
      Map<String, Object> result = new LinkedHashMap<>();
      for (Map.Entry<Key<?>, Object> entry : scratchpadState.entrySet()) {
        result.put(entry.getKey().getName(), entry.getValue());
      }
      return result;
    }
  }
}
