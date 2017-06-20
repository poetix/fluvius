package com.codepoetics.fluvius.flows;

import com.codepoetics.fluvius.api.Condition;
import com.codepoetics.fluvius.api.Flow;
import com.codepoetics.fluvius.api.FlowVisitor;
import com.codepoetics.fluvius.api.Operation;
import com.codepoetics.fluvius.api.scratchpad.Key;

import java.util.Set;
import java.util.UUID;

final class SingleOperationFlow<T> extends AbstractFlow<T> {

  static <T> Flow<T> create(Set<Key<?>> requiredKeys, Key<T> providedKey, Operation<T> operation) {
    return new SingleOperationFlow<>(UUID.randomUUID(), requiredKeys, providedKey, operation);
  }

  private final Operation<T> operation;

  private SingleOperationFlow(UUID stepId, Set<Key<?>> requiredKeys, Key<T> providedKeys, Operation<T> operation) {
    super(stepId, requiredKeys, providedKeys);
    this.operation = operation;
  }

  @Override
  public <V> V visit(FlowVisitor<V> visitor) {
    return visitor.visitSingle(getStepId(), getRequiredKeys(), getProvidedKey(), operation);
  }

  @Override
  public <N> Flow<N> then(Flow<N> next) {
    return SequenceFlow.create(this, next);
  }

  @Override
  public Flow<T> orIf(Condition condition, Flow<T> ifTrue) {
    return BranchFlow.create(this, condition, ifTrue);
  }
}
