package com.codepoetics.fluvius.flows;

import com.codepoetics.fluvius.api.Condition;
import com.codepoetics.fluvius.api.Flow;
import com.codepoetics.fluvius.api.FlowVisitor;
import com.codepoetics.fluvius.api.Operation;
import com.codepoetics.fluvius.api.scratchpad.Key;

import java.util.Set;

class SingleOperationFlow<T> extends AbstractFlow<T> {

  static <T> Flow<T> create(final Set<Key<?>> requiredKeys, final Key<T> providedKey, final Operation<T> operation) {
    return new SingleOperationFlow<>(requiredKeys, providedKey, operation);
  }

  private final Operation<T> operation;

  private SingleOperationFlow(final Set<Key<?>> requiredKeys, final Key<T> providedKeys, final Operation<T> operation) {
    super(requiredKeys, providedKeys);
    this.operation = operation;
  }

  @Override
  public <V> V visit(final FlowVisitor<V> visitor) {
    return visitor.visitSingle(getRequiredKeys(), getProvidedKey(), operation);
  }

  @Override
  public <N> Flow<N> then(final Flow<N> next) {
    return SequenceFlow.create(this, next);
  }

  @Override
  public Flow<T> orIf(final Condition condition, final Flow<T> ifTrue) {
    return BranchFlow.create(this, condition, ifTrue);
  }
}
