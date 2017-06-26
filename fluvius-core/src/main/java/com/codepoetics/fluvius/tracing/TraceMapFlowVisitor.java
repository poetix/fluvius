package com.codepoetics.fluvius.tracing;

import com.codepoetics.fluvius.api.*;
import com.codepoetics.fluvius.api.scratchpad.Key;
import com.codepoetics.fluvius.api.tracing.FlowStepType;
import com.codepoetics.fluvius.api.tracing.TraceMap;
import com.codepoetics.fluvius.api.tracing.TraceMapLabel;

import java.util.*;

final class TraceMapFlowVisitor implements FlowVisitor<TraceMap> {

  @Override
  public <T> TraceMap visitSingle(UUID stepId, Set<Key<?>> requiredKeys, Key<T> providedKey, Operation<T> operation) {
    return TraceMap.ofStep(stepId, toKeyNames(requiredKeys), providedKey.getName(), operation.getName());
  }

  @Override
  public <T> TraceMap visitSequence(UUID stepId, Set<Key<?>> requiredKeys, Key<T> providedKey, List<TraceMap> items) {
    return TraceMap.ofSequence(stepId, toKeyNames(requiredKeys), providedKey.getName(), items);
  }

  @Override
  public <T> TraceMap visitBranch(UUID stepId, Set<Key<?>> requiredKeys, Key<T> providedKey, TraceMap defaultBranch, List<Conditional<TraceMap>> conditionalBranches) {
    Map<String, TraceMap> children = new LinkedHashMap<>(conditionalBranches.size() + 1);

    for (Conditional<TraceMap> conditional : conditionalBranches) {
      children.put(
          conditional.getCondition().getDescription(),
          conditional.getValue());
    }

    return TraceMap.ofBranch(stepId, toKeyNames(requiredKeys), providedKey.getName(), defaultBranch, children);
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
    return condition;
  }
}
