package com.codepoetics.fluvius.tracing;

import com.codepoetics.fluvius.api.*;
import com.codepoetics.fluvius.api.functional.Mapper;
import com.codepoetics.fluvius.api.scratchpad.Key;
import com.codepoetics.fluvius.api.scratchpad.Scratchpad;
import com.codepoetics.fluvius.api.tracing.TraceEventListener;
import com.codepoetics.fluvius.api.tracing.TraceMap;
import com.codepoetics.fluvius.api.tracing.TracedAction;

import java.util.*;

/**
 * A {@link FlowVisitor} that creates a {@link TraceMap} and notifies a {@link TraceEventListener} of step execution.
 */
public final class TracingFlowVisitor implements FlowVisitor<TracedAction> {

  private static final FlowVisitor<TraceMap> traceMapVisitor = new TraceMapFlowVisitor();
  private static final Mapper<TracedAction, TraceMap> toTraceMap = new Mapper<TracedAction, TraceMap>() {
    @Override
    public TraceMap apply(TracedAction input) {
      return input.getTraceMap();
    }
  };

  /**
   * Create a new tracing FlowVisitor wrapping the supplied listener and visitor.
   *
   * @param listener      The trace event listener to wrap.
   * @param actionVisitor The flow visitor to wrap.
   * @return The constructed flow visitor.
   */
  public static FlowVisitor<TracedAction> wrapping(TraceEventListener listener, FlowVisitor<Action> actionVisitor) {
    return new TracingFlowVisitor(listener, actionVisitor);
  }

  private final TraceEventListener listener;
  private final FlowVisitor<Action> actionVisitor;

  private TracingFlowVisitor(TraceEventListener listener, FlowVisitor<Action> actionVisitor) {
    this.listener = listener;
    this.actionVisitor = actionVisitor;
  }

  @Override
  public <T> TracedAction visitSingle(UUID stepId, Set<Key<?>> requiredKeys, Key<T> providedKey, Operation<T> operation) {
    TraceMap traceMap = traceMapVisitor.visitSingle(stepId, requiredKeys, providedKey, operation);
    Action action = new NotifyingAction(
        traceMap.getStepId(),
        listener,
        providedKey,
        actionVisitor.visitSingle(stepId, requiredKeys, providedKey, operation));

    return new ConcreteTracedAction(traceMap, action);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> TracedAction visitSequence(UUID stepId, Set<Key<?>> requiredKeys, Key<T> providedKey, List<TracedAction> items) {
    TraceMap traceMap = traceMapVisitor.visitSequence(stepId, requiredKeys, providedKey, getItemTraceMaps(items));
    Action action = new NotifyingAction(
        traceMap.getStepId(),
        listener,
        providedKey,
        actionVisitor.visitSequence(stepId, requiredKeys, providedKey, (List) items));

    return new ConcreteTracedAction(traceMap, action);
  }

  private List<TraceMap> getItemTraceMaps(List<TracedAction> items) {
    List<TraceMap> children = new ArrayList<>(items.size());
    for (TracedAction item : items) {
      children.add(item.getTraceMap());
    }
    return children;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> TracedAction visitBranch(UUID stepId, Set<Key<?>> requiredKeys, Key<T> providedKey, TracedAction defaultBranch, List<Conditional<TracedAction>> conditionalBranches) {
    TraceMap traceMap = traceMapVisitor.visitBranch(
        stepId,
        requiredKeys,
        providedKey,
        defaultBranch.getTraceMap(),
        getConditionalTraceMaps(conditionalBranches));

    Action action = new NotifyingAction(
        traceMap.getStepId(),
        listener,
        providedKey,
        actionVisitor.visitBranch(
            stepId,
            requiredKeys,
            providedKey,
            defaultBranch,
            (List) conditionalBranches));

    return new ConcreteTracedAction(traceMap, action);
  }

  private List<Conditional<TraceMap>> getConditionalTraceMaps(List<Conditional<TracedAction>> conditionalActions) {
    List<Conditional<TraceMap>> children = new ArrayList<>(conditionalActions.size());
    for (Conditional<TracedAction> branch : conditionalActions) {
      children.add(branch.map(toTraceMap));
    }
    return children;
  }

  @Override
  public Condition visitCondition(Condition condition) {
    return actionVisitor.visitCondition(condition);
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

  private static final class ConcreteTracedAction implements TracedAction {

    private final TraceMap traceMap;
    private final Action action;

    private ConcreteTracedAction(TraceMap traceMap, Action action) {
      this.traceMap = traceMap;
      this.action = action;
    }

    @Override
    public TraceMap getTraceMap() {
      return traceMap;
    }

    @Override
    public Scratchpad run(UUID flowId, Scratchpad scratchpad) {
      return action.run(flowId, scratchpad);
    }
  }
}
