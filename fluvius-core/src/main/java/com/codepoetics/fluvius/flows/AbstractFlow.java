package com.codepoetics.fluvius.flows;

import com.codepoetics.fluvius.api.Condition;
import com.codepoetics.fluvius.api.Flow;
import com.codepoetics.fluvius.api.scratchpad.Key;

import java.util.*;

abstract class AbstractFlow<T> implements Flow<T> {

  private final UUID stepId;
  private final Set<Key<?>> requiredKeys;
  private final Key<T> providedKey;

  AbstractFlow(UUID stepId, final Set<Key<?>> requiredKeys, final Key<T> providedKey) {
    this.stepId = stepId;
    this.requiredKeys = requiredKeys;
    this.providedKey = providedKey;
  }

  @Override
  public UUID getStepId() {
    return stepId;
  }

  @Override
  public final Set<Key<?>> getRequiredKeys() {
    return new HashSet<>(requiredKeys);
  }

  @Override
  public final Key<T> getProvidedKey() {
    return providedKey;
  }

  @Override
  public <N> Flow<N> then(final Flow<N> next) {
    return SequenceFlow.create(this, next);
  }

  @Override
  public Flow<T> orIf(final Condition condition, final Flow<T> ifTrue) {
    return BranchFlow.create(this, condition, ifTrue);
  }

  @Override
  public List<Flow<?>> getAllFlows() {
    List<Flow<?>> flows = new ArrayList<>(1);
    flows.add(this);
    return flows;
  }

}
