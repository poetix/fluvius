package com.codepoetics.fluvius.visitors;

import com.codepoetics.fluvius.api.*;
import com.codepoetics.fluvius.api.scratchpad.Key;

import java.util.List;
import java.util.Map;
import java.util.Set;

final class DefaultFlowVisitor implements FlowVisitor<Action> {

  @Override
  public <T> Action visitSingle(final Set<Key<?>> requiredKeys, final Key<T> providedKey, final Operation<T> operation) {
    return SingleOperationAction.of(providedKey, operation);
  }

  @Override
  public <T> Action visitSequence(final Set<Key<?>> requiredKeys, final Key<T> providedKey, final List<Action> actions) {
    return SequenceAction.of(actions);
  }

  @Override
  public <T> Action visitBranch(final Set<Key<?>> requiredKeys, final Key<T> providedKey, final Action defaultAction, final Map<String, Conditional<Action>> conditionalActions) {
    return BranchAction.of(defaultAction, conditionalActions);
  }

  @Override
  public Condition visitCondition(final Condition condition) {
    return condition;
  }
}
