package com.codepoetics.fluvius.flows;

import com.codepoetics.fluvius.api.*;

import java.util.Set;

public class SingleOperationFlow<T> extends AbstractFlow<T> {

    static <T> Flow<T> create(Set<Key<?>> requiredKeys, Key<T> providedKey, Operation<T> operation) {
        return new SingleOperationFlow<>(requiredKeys, providedKey, operation);
    }

    private final Operation<T> operation;

    private SingleOperationFlow(Set<Key<?>> requiredKeys, Key<T> providedKeys, Operation<T> operation) {
        super(requiredKeys, providedKeys);
        this.operation = operation;
    }

    @Override
    public <V extends FlowVisitor> Action visit(V visitor) {
        return visitor.visitSingle(getRequiredKeys(), getProvidedKey(), operation);
    }

    @Override
    public <D extends FlowDescriber<D>> D describe(FlowDescriber<D> describer) {
        return describer.describeSingle(operation.getName(), getRequiredKeys(), getProvidedKey());
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
