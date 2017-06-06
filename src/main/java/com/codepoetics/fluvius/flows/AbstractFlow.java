package com.codepoetics.fluvius.flows;

import com.codepoetics.fluvius.api.Condition;
import com.codepoetics.fluvius.api.Flow;
import com.codepoetics.fluvius.api.scratchpad.Key;

import java.util.HashSet;
import java.util.Set;

abstract class AbstractFlow<T> implements Flow<T> {

  private final Set<Key<?>> requiredKeys;
  private final Key<T> providedKey;

  AbstractFlow(final Set<Key<?>> requiredKeys, final Key<T> providedKey) {
    this.requiredKeys = requiredKeys;
    this.providedKey = providedKey;
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

}
