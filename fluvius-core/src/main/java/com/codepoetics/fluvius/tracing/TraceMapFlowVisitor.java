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
  public <T> TraceMap visitSingle(final Set<Key<?>> requiredKeys, final Key<T> providedKey, final Operation<T> operation) {
    return new ConcreteTraceMap(UUID.randomUUID(), toKeyNames(requiredKeys), providedKey.getName(), operation.getName(), Collections.<TraceMap>emptyList());
  }

  @Override
  public <T> TraceMap visitSequence(final Set<Key<?>> requiredKeys, final Key<T> providedKey, final List<TraceMap> items) {
    return new ConcreteTraceMap(UUID.randomUUID(), toKeyNames(requiredKeys), providedKey.getName(), "Sequence", items);
  }

  @Override
  public <T> TraceMap visitBranch(final Set<Key<?>> requiredKeys, final Key<T> providedKey, final TraceMap defaultBranch, final List<Conditional<TraceMap>> conditionalBranches) {
    final List<TraceMap> children = new ArrayList<>(conditionalBranches.size() + 1);
    for (final Conditional<TraceMap> conditional : conditionalBranches) {
      final TraceMap conditionalTraceMap = conditional.getValue();
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
    return new ConcreteTraceMap(UUID.randomUUID(), toKeyNames(requiredKeys), providedKey.getName(), "Branch", children);
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

    private final UUID id;
    private final Set<String> requiredKeys;
    private final String providedKey;
    private final String description;
    private final List<TraceMap> children;

    private ConcreteTraceMap(final UUID id, final Set<String> requiredKeys, final String providedKey, final String description, final List<TraceMap> children) {
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
    public List<TraceMap> getChildren() {
      return children;
    }

    @Override
    public String toString() {
      return String.format("%s: %s (requires %s, provides %s) | %s", id, description, requiredKeys, providedKey, children);
    }
  }
}
