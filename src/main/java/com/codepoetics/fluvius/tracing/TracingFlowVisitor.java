package com.codepoetics.fluvius.tracing;

import com.codepoetics.fluvius.api.*;
import com.codepoetics.fluvius.api.functional.F1;
import com.codepoetics.fluvius.api.scratchpad.Key;
import com.codepoetics.fluvius.api.scratchpad.Scratchpad;
import com.codepoetics.fluvius.api.tracing.TraceEventListener;
import com.codepoetics.fluvius.api.tracing.TraceMap;
import com.codepoetics.fluvius.api.tracing.TracedAction;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * A {@link FlowVisitor} that creates a {@link TraceMap} and notifies a {@link TraceEventListener} of step execution.
 */
public final class TracingFlowVisitor implements FlowVisitor<TracedAction> {

  private static final FlowVisitor<TraceMap> traceMapVisitor = new TraceMapFlowVisitor();
  private static final F1<TracedAction, TraceMap> toTraceMap = new F1<TracedAction, TraceMap>() {
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
  public <T> TracedAction visitSingle(Set<Key<?>> requiredKeys, Key<T> providedKey, Operation<T> operation) {
    TraceMap traceMap = traceMapVisitor.visitSingle(requiredKeys, providedKey, operation);
    Action action = new NotifyingAction(
        traceMap.getId(),
        listener,
        providedKey,
        actionVisitor.visitSingle(requiredKeys, providedKey, operation));

    return new ConcreteTracedAction(traceMap, action);
  }

  @Override
  public <T> TracedAction visitSequence(Set<Key<?>> requiredKeys, Key<T> providedKey, List<TracedAction> items) {
    TraceMap traceMap = traceMapVisitor.visitSequence(requiredKeys, providedKey, getItemTraceMaps(items));
    Action action = new NotifyingAction(
        traceMap.getId(),
        listener,
        providedKey,
        actionVisitor.visitSequence(requiredKeys, providedKey, (List) items));

    return new ConcreteTracedAction(traceMap, action);
  }

  private List<TraceMap> getItemTraceMaps(List<TracedAction> items) {
    List<TraceMap> children = new ArrayList<>(items.size());
    for (TracedAction item : items) {
      children.add(item.getTraceMap());
    }
    return children;
  }

  @Override
  public <T> TracedAction visitBranch(Set<Key<?>> requiredKeys, Key<T> providedKey, TracedAction defaultBranch, List<Conditional<TracedAction>> conditionalBranches) {
    TraceMap traceMap = traceMapVisitor.visitBranch(
        requiredKeys,
        providedKey,
        defaultBranch.getTraceMap(),
        getConditionalTraceMaps(conditionalBranches));

    Action action = new NotifyingAction(
        traceMap.getId(),
        listener,
        providedKey,
        actionVisitor.visitBranch(
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
      listener.stepStarted(flowId, stepId, scratchpad);
      try {
        Scratchpad result = action.run(flowId, scratchpad);
        listener.stepSucceeded(flowId, stepId, result.get(providedKey));
        return result;
      } catch (RuntimeException e) {
        listener.stepFailed(flowId, stepId, e);
        throw e;
      }
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
