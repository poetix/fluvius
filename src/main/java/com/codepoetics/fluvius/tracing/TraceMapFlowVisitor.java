package com.codepoetics.fluvius.tracing;

import com.codepoetics.fluvius.api.Condition;
import com.codepoetics.fluvius.api.Conditional;
import com.codepoetics.fluvius.api.FlowVisitor;
import com.codepoetics.fluvius.api.Operation;
import com.codepoetics.fluvius.api.scratchpad.Key;
import com.codepoetics.fluvius.api.tracing.TraceMap;

import java.util.*;

final class TraceMapFlowVisitor implements FlowVisitor<TraceMap> {

  @Override
  public <T> TraceMap visitSingle(Set<Key<?>> requiredKeys, Key<T> providedKey, Operation<T> operation) {
    return new ConcreteTraceMap(UUID.randomUUID(), requiredKeys, providedKey, operation.getName(), Collections.<TraceMap>emptyList());
  }

  @Override
  public <T> TraceMap visitSequence(Set<Key<?>> requiredKeys, Key<T> providedKey, List<TraceMap> items) {
    return new ConcreteTraceMap(UUID.randomUUID(), requiredKeys, providedKey, "Sequence", items);
  }

  @Override
  public <T> TraceMap visitBranch(Set<Key<?>> requiredKeys, Key<T> providedKey, TraceMap defaultBranch, List<Conditional<TraceMap>> conditionalBranches) {
    List<TraceMap> children = new ArrayList<>(conditionalBranches.size() + 1);
    for (Conditional<TraceMap> conditional : conditionalBranches) {
      TraceMap conditionalTraceMap = conditional.getValue();
      children.add(new ConcreteTraceMap(
          conditionalTraceMap.getId(),
          conditionalTraceMap.getRequiredKeys(),
          conditionalTraceMap.getProvidedKey(),
          "If " + conditional.getCondition().getDescription() + ": " + conditionalTraceMap.getDescription(),
          conditionalTraceMap.getChildren()));
    }
    children.add(new ConcreteTraceMap(
        defaultBranch.getId(),
        defaultBranch.getRequiredKeys(),
        defaultBranch.getProvidedKey(),
        "Otherwise: " + defaultBranch.getDescription(),
        defaultBranch.getChildren()
    ));
    return new ConcreteTraceMap(UUID.randomUUID(), requiredKeys, providedKey, "Branch", children);
  }

  @Override
  public Condition visitCondition(Condition condition) {
    return null;
  }

  private static final class ConcreteTraceMap implements TraceMap {

    private final UUID id;
    private final Set<Key<?>> requiredKeys;
    private final Key<?> providedKey;
    private final String description;
    private final List<TraceMap> children;

    private ConcreteTraceMap(UUID id, Set<Key<?>> requiredKeys, Key<?> providedKey, String description, List<TraceMap> children) {
      this.id = id;
      this.requiredKeys = requiredKeys;
      this.providedKey = providedKey;
      this.description = description;
      this.children = children;
    }

    @Override
    public UUID getId() {
      return id;
    }

    @Override
    public Set<Key<?>> getRequiredKeys() {
      return requiredKeys;
    }

    @Override
    public Key<?> getProvidedKey() {
      return providedKey;
    }

    @Override
    public String getDescription() {
      return description;
    }

    @Override
    public List<TraceMap> getChildren() {
      return children;
    }

    @Override
    public String toString() {
      return String.format("%s: %s (requires %s, provides %s) | %s", id, description, requiredKeys, providedKey, children);
    }
  }
}
