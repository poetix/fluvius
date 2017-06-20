package com.codepoetics.fluvius.tracing;

import com.codepoetics.fluvius.api.*;
import com.codepoetics.fluvius.api.scratchpad.Key;
import com.codepoetics.fluvius.api.tracing.FlowStepType;
import com.codepoetics.fluvius.api.tracing.TraceMap;

import java.util.*;

final class TraceMapFlowVisitor implements FlowVisitor<TraceMap> {

  @Override
  public <T> TraceMap visitSingle(UUID stepId, Set<Key<?>> requiredKeys, Key<T> providedKey, Operation<T> operation) {
    return new ConcreteTraceMap(stepId, toKeyNames(requiredKeys), providedKey.getName(), operation.getName(), FlowStepType.STEP, Collections.<TraceMap>emptyList());
  }

  @Override
  public <T> TraceMap visitSequence(UUID stepId, Set<Key<?>> requiredKeys, Key<T> providedKey, List<TraceMap> items) {
    return new ConcreteTraceMap(stepId, toKeyNames(requiredKeys), providedKey.getName(), "Sequence", FlowStepType.SEQUENCE, items);
  }

  @Override
  public <T> TraceMap visitBranch(UUID stepId, Set<Key<?>> requiredKeys, Key<T> providedKey, TraceMap defaultBranch, List<Conditional<TraceMap>> conditionalBranches) {
    List<TraceMap> children = new ArrayList<>(conditionalBranches.size() + 1);
    for (Conditional<TraceMap> conditional : conditionalBranches) {
      TraceMap conditionalTraceMap = conditional.getValue();
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

  private Set<String> toKeyNames(Set<Key<?>> keys) {
    Set<String> result = new HashSet<>(keys.size());
    for (Key<?> key : keys) {
      result.add(key.getName());
    }
    return result;
  }

  @Override
  public Condition visitCondition(Condition condition) {
    return null;
  }

  private static final class ConcreteTraceMap implements TraceMap {

    private final UUID stepId;
    private final Set<String> requiredKeys;
    private final String providedKey;
    private final String description;
    private final FlowStepType type;
    private final List<TraceMap> children;

    private ConcreteTraceMap(UUID stepId, Set<String> requiredKeys, String providedKey, String description, FlowStepType type, List<TraceMap> children) {
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
