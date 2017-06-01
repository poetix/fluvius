package com.codepoetics.fluvius.visitors;

import com.codepoetics.fluvius.api.Action;
import com.codepoetics.fluvius.api.Operation;
import com.codepoetics.fluvius.api.scratchpad.Key;
import com.codepoetics.fluvius.api.scratchpad.Scratchpad;

final class SingleOperationAction<T> implements Action {

    static <T> Action of(Key<T> outputKey, Operation<T> operation) {
        return new SingleOperationAction<>(outputKey, operation);
    }

    private final Key<T> outputKey;
    private final Operation<T> operation;

    private SingleOperationAction(Key<T> outputKey, Operation<T> operation) {
        this.outputKey = outputKey;
        this.operation = operation;
    }

    @Override
    public Scratchpad run(Scratchpad scratchpad) {
        return scratchpad.with(outputKey.of(operation.run(scratchpad)));
    }
}
