package com.codepoetics.fluvius.tracing;

import com.codepoetics.fluvius.api.*;
import com.codepoetics.fluvius.api.scratchpad.Key;
import com.codepoetics.fluvius.api.tracing.FlowStepType;
import com.codepoetics.fluvius.api.tracing.TraceMap;

import java.util.*;

final class TraceMapFlowVisitor implements FlowVisitor<TraceMap> {

  @Override
  public <T> TraceMap visitSingle(final UUID stepId, final Set<Key<?>> requiredKeys, final Key<T> providedKey, final Operation<T> operation) {
    return new ConcreteTraceMap(stepId, toKeyNames(requiredKeys), providedKey.getName(), operation.getName(), FlowStepType.STEP, Collections.<TraceMap>emptyList());
  }

  @Override
  public <T> TraceMap visitSequence(final UUID stepId, final Set<Key<?>> requiredKeys, final Key<T> providedKey, final List<TraceMap> items) {
    return new ConcreteTraceMap(stepId, toKeyNames(requiredKeys), providedKey.getName(), "Sequence", FlowStepType.SEQUENCE, items);
  }

  @Override
  public <T> TraceMap visitBranch(final UUID stepId, final Set<Key<?>> requiredKeys, final Key<T> providedKey, final TraceMap defaultBranch, final List<Conditional<TraceMap>> conditionalBranches) {
    final List<TraceMap> children = new ArrayList<>(conditionalBranches.size() + 1);
    for (final Conditional<TraceMap> conditional : conditionalBranches) {
      final TraceMap conditionalTraceMap = conditional.getValue();
      children.add(conditionalChild(conditional, conditionalTraceMap));
    }
    children.add(defaultChild(defaultBranch));
    return new ConcreteTraceMap(stepId, toKeyNames(requiredKeys), providedKey.getName(), "Branch", FlowStepType.BRANCH, children);
  }

  private TraceMap defaultChild(TraceMap defaultBranch) {
    return redescribe(defaultBranch,
        "Otherwise: " + defaultBranch.getDescription());
  }

  private TraceMap conditionalChild(Conditional<TraceMap> conditional, TraceMap conditionalTraceMap) {
    return redescribe(conditionalTraceMap,
        "If " + conditional.getCondition().getDescription() + ": " + conditionalTraceMap.getDescription());
  }

  private TraceMap redescribe(TraceMap traceMap, String newDescription) {
    return new ConcreteTraceMap(
        traceMap.getStepId(),
        traceMap.getRequiredKeys(),
        traceMap.getProvidedKey(),
        newDescription,
        traceMap.getType(),
        traceMap.getChildren());
  }

  private Set<String> toKeyNames(final Set<Key<?>> keys) {
    final Set<String> result = new HashSet<>(keys.size());
    for (final Key<?> key : keys) {
      result.add(key.getName());
    }
    return result;
  }

  @Override
  public Condition visitCondition(final Condition condition) {
    return null;
  }

  private static final class ConcreteTraceMap implements TraceMap {

    private final UUID stepId;
    private final Set<String> requiredKeys;
    private final String providedKey;
    private final String description;
    private final FlowStepType type;
    private final List<TraceMap> children;

    private ConcreteTraceMap(final UUID stepId, final Set<String> requiredKeys, final String providedKey, final String description, FlowStepType type, final List<TraceMap> children) {
      this.stepId = stepId;
      this.requiredKeys = requiredKeys;
      this.providedKey = providedKey;
      this.description = description;
      this.type = type;
      this.children = children;
    }

    @Override
    public UUID getStepId() {
      return stepId;
    }

    @Override
    public Set<String> getRequiredKeys() {
      return requiredKeys;
    }

    @Override
    public String getProvidedKey() {
      return providedKey;
    }

    @Override
    public String getDescription() {
      return description;
    }

    @Override
    public FlowStepType getType() {
      return type;
    }

    @Override
    public List<TraceMap> getChildren() {
      return children;
    }

    @Override
    public String toString() {
      return String.format("%s: %s (requires %s, provides %s) | %s", stepId, description, requiredKeys, providedKey, children);
    }
  }
}
