package com.codepoetics.fluvius.tracing;

import com.codepoetics.fluvius.api.*;
import com.codepoetics.fluvius.api.functional.F1;
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
    public TraceMap apply(final TracedAction input) {
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
  public static FlowVisitor<TracedAction> wrapping(final TraceEventListener listener, final FlowVisitor<Action> actionVisitor) {
    return new TracingFlowVisitor(listener, actionVisitor);
  }

  private final TraceEventListener listener;
  private final FlowVisitor<Action> actionVisitor;

  private TracingFlowVisitor(final TraceEventListener listener, final FlowVisitor<Action> actionVisitor) {
    this.listener = listener;
    this.actionVisitor = actionVisitor;
  }

  @Override
  public <T> TracedAction visitSingle(final Set<Key<?>> requiredKeys, final Key<T> providedKey, final Operation<T> operation) {
    final TraceMap traceMap = traceMapVisitor.visitSingle(requiredKeys, providedKey, operation);
    final Action action = new NotifyingAction(
        traceMap.getId(),
        listener,
        providedKey,
        actionVisitor.visitSingle(requiredKeys, providedKey, operation));

    return new ConcreteTracedAction(traceMap, action);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> TracedAction visitSequence(final Set<Key<?>> requiredKeys, final Key<T> providedKey, final List<TracedAction> items) {
    final TraceMap traceMap = traceMapVisitor.visitSequence(requiredKeys, providedKey, getItemTraceMaps(items));
    final Action action = new NotifyingAction(
        traceMap.getId(),
        listener,
        providedKey,
        actionVisitor.visitSequence(requiredKeys, providedKey, (List) items));

    return new ConcreteTracedAction(traceMap, action);
  }

  private List<TraceMap> getItemTraceMaps(final List<TracedAction> items) {
    final List<TraceMap> children = new ArrayList<>(items.size());
    for (final TracedAction item : items) {
      children.add(item.getTraceMap());
    }
    return children;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> TracedAction visitBranch(final Set<Key<?>> requiredKeys, final Key<T> providedKey, final TracedAction defaultBranch, final List<Conditional<TracedAction>> conditionalBranches) {
    final TraceMap traceMap = traceMapVisitor.visitBranch(
        requiredKeys,
        providedKey,
        defaultBranch.getTraceMap(),
        getConditionalTraceMaps(conditionalBranches));

    final Action action = new NotifyingAction(
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

  private List<Conditional<TraceMap>> getConditionalTraceMaps(final List<Conditional<TracedAction>> conditionalActions) {
    final List<Conditional<TraceMap>> children = new ArrayList<>(conditionalActions.size());
    for (final Conditional<TracedAction> branch : conditionalActions) {
      children.add(branch.map(toTraceMap));
    }
    return children;
  }

  @Override
  public Condition visitCondition(final Condition condition) {
    return actionVisitor.visitCondition(condition);
  }

  private static final class NotifyingAction implements Action {

    private final UUID stepId;
    private final TraceEventListener listener;
    private final Key<?> providedKey;
    private final Action action;

    private NotifyingAction(final UUID stepId, final TraceEventListener listener, final Key<?> providedKey, final Action action) {
      this.stepId = stepId;
      this.listener = listener;
      this.providedKey = providedKey;
      this.action = action;
    }

    @Override
    public Scratchpad run(final UUID flowId, final Scratchpad scratchpad) {
      listener.stepStarted(flowId, stepId, keysToNames(scratchpad.toMap()));
      final Scratchpad result = action.run(flowId, scratchpad);
      if (result.isSuccessful(providedKey)) {
        listener.stepSucceeded(flowId, stepId, result.get(providedKey));
      } else {
        listener.stepFailed(flowId, stepId, result.getFailureReason(providedKey));
      }
      return result;
    }

    private Map<String, Object> keysToNames(final Map<Key<?>, Object> scratchpadState) {
      final Map<String, Object> result = new LinkedHashMap<>();
      for (final Map.Entry<Key<?>, Object> entry : scratchpadState.entrySet()) {
        result.put(entry.getKey().getName(), entry.getValue());
      }
      return result;
    }
  }

  private static final class ConcreteTracedAction implements TracedAction {

    private final TraceMap traceMap;
    private final Action action;

    private ConcreteTracedAction(final TraceMap traceMap, final Action action) {
      this.traceMap = traceMap;
      this.action = action;
    }

    @Override
    public TraceMap getTraceMap() {
      return traceMap;
    }

    @Override
    public Scratchpad run(final UUID flowId, final Scratchpad scratchpad) {
      return action.run(flowId, scratchpad);
    }
  }
}
