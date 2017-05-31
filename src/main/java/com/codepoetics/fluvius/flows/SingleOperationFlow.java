package com.codepoetics.fluvius.flows;

import com.codepoetics.fluvius.api.*;

import java.util.Set;

public class SingleOperationFlow<T> extends AbstractFlow<T> {

    static <T> Flow<T> create(Set<Key<?>> inputKeys, Key<T> outputKey, Operation<T> operation) {
        return new SingleOperationFlow<>(inputKeys, outputKey, operation);
    }

    private final Operation<T> operation;

    private SingleOperationFlow(Set<Key<?>> inputKeys, Key<T> outputKey, Operation<T> operation) {
        super(inputKeys, outputKey);
        this.operation = operation;
    }

    @Override
    public <V extends FlowVisitor> Action visit(V visitor) {
        return visitor.visitSingle(getOutputKey(), operation);
    }

    @Override
    public <D extends FlowDescriber<D>> D describe(FlowDescriber<D> describer) {
        return describer.describeSingle(operation.getName());
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
