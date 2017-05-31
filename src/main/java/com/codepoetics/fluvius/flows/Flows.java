package com.codepoetics.fluvius.flows;

import com.codepoetics.fluvius.api.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class Flows {

    private Flows() {
    }

    public static final class InputKeysCapture {
        private final Set<Key<?>> inputKeys;

        private InputKeysCapture(Set<Key<?>> inputKeys) {
            this.inputKeys = inputKeys;
        }

        public <T> OutputKeysCapture<T> to(Key<T> outputKey) {
            return new OutputKeysCapture<>(inputKeys, outputKey);
        }
    }

    public static final class OutputKeysCapture<T> {
        private final Set<Key<?>> inputKeys;
        private final Key<T> outputKey;

        public OutputKeysCapture(Set<Key<?>> inputKeys, Key<T> outputKey) {
            this.inputKeys = inputKeys;
            this.outputKey = outputKey;
        }

        public Flow<T> using(Operation<T> operation) {
            return SingleOperationFlow.create(inputKeys, outputKey, operation);
        }
    }

    public static <T> InputKeysCapture from(Key<?>...inputKeys) {
        return new InputKeysCapture(new HashSet<>(Arrays.asList(inputKeys)));
    }

    public static <T> T run(Flow<T> flow, Scratchpad initialScratchpad, FlowVisitor flowVisitor) {
        Action action = flow.visit(flowVisitor);
        Scratchpad finalScratchpad = action.run(initialScratchpad);
        return finalScratchpad.get(flow.getOutputKey());
    }
}
