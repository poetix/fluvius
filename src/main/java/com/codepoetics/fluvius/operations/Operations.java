package com.codepoetics.fluvius.operations;

import com.codepoetics.fluvius.api.Operation;
import com.codepoetics.fluvius.api.functional.ScratchpadFunction;
import com.codepoetics.fluvius.api.scratchpad.Scratchpad;

public final class Operations {

    private Operations() {
    }

    public static <T> Operation<T> fromFunction(String name, ScratchpadFunction<T> function) {
        return new FunctionOperation<>(name, function);
    }

    private static final class FunctionOperation<T> implements Operation<T> {
        private final String name;
        private final ScratchpadFunction<T> function;

        private FunctionOperation(String name, ScratchpadFunction<T> function) {
            this.name = name;
            this.function = function;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public T run(Scratchpad scratchpad) {
            return function.apply(scratchpad);
        }
    }
}
