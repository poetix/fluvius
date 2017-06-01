package com.codepoetics.fluvius.flows;

import com.codepoetics.fluvius.api.Condition;
import com.codepoetics.fluvius.api.Flow;
import com.codepoetics.fluvius.api.scratchpad.Key;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractFlow<T> implements Flow<T> {

    private final Set<Key<?>> requiredKeys;
    private final Key<T> providedKey;

    AbstractFlow(Set<Key<?>> requiredKeys, Key<T> providedKey) {
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
    public <N> Flow<N> then(Flow<N> next) {
        return SequenceFlow.create(this, next);
    }

    @Override
    public Flow<T> orIf(Condition condition, Flow<T> ifTrue) {
        return BranchFlow.create(this, condition, ifTrue);
    }

}
