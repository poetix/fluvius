package com.codepoetics.fluvius.visitors;

import com.codepoetics.fluvius.api.*;
import com.codepoetics.fluvius.api.scratchpad.Key;

import java.util.List;
import java.util.Set;
import java.util.UUID;

final class DefaultFlowVisitor implements FlowVisitor<Action> {

  @Override
  public <T> Action visitSingle(UUID stepId, Set<Key<?>> requiredKeys, Key<T> providedKey, Operation<T> operation) {
    return SingleOperationAction.of(providedKey, operation);
  }

  @Override
  public <T> Action visitSequence(UUID stepId, Set<Key<?>> requiredKeys, Key<T> providedKey, List<Action> actions) {
    return SequenceAction.of(actions);
  }

  @Override
  public <T> Action visitBranch(UUID stepId, Set<Key<?>> requiredKeys, Key<T> providedKey, Action defaultAction, List<Conditional<Action>> conditionalActions) {
    return BranchAction.of(defaultAction, conditionalActions);
  }

  @Override
  public Condition visitCondition(Condition condition) {
    return condition;
  }
}
