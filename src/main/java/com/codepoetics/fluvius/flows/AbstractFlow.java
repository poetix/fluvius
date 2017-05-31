package com.codepoetics.fluvius.flows;

import com.codepoetics.fluvius.api.*;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractFlow<T> implements Flow<T> {

    private final Set<Key<?>> inputKeys;
    private final Key<T> outputKey;

    protected AbstractFlow(Set<Key<?>> inputKeys, Key<T> outputKey) {
        this.inputKeys = inputKeys;
        this.outputKey = outputKey;
    }

    @Override
    public final Set<Key<?>> getInputKeys() {
        return new HashSet<>(inputKeys);
    }

    @Override
    public final Key<T> getOutputKey() {
        return outputKey;
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
