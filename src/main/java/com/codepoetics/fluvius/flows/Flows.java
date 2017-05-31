package com.codepoetics.fluvius.flows;

import com.codepoetics.fluvius.api.*;
import com.codepoetics.fluvius.describers.PrettyPrintingFlowDescriber;
import com.codepoetics.fluvius.exceptions.MissingKeysException;
import com.codepoetics.fluvius.operations.Operations;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class Flows {

    private Flows() {
    }

    public static String prettyPrint(Flow<?> flow) {
        return flow.describe(PrettyPrintingFlowDescriber.create()).getDescription();
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

        public Flow<T> using(final String name, final ScratchpadFunction<T> function) {
            return using(Operations.fromFunction(name, function));
        }
    }

    public static <T> InputKeysCapture from(Key<?>...inputKeys) {
        return new InputKeysCapture(new HashSet<>(Arrays.asList(inputKeys)));
    }

    public static final class TargetCapture<O> {
        private final Key<O> target;

        private TargetCapture(Key<O> target) {
            this.target = target;
        }

        public <I> SourceTargetCapture<I, O> from(Key<I> source) {
            return new SourceTargetCapture<I, O>(source, target);
        }

        public <I1, I2> DoubleSourceTargetCapture<I1, I2, O> from(Key<I1> source1, Key<I2> source2) {
            return new DoubleSourceTargetCapture<I1, I2, O>(source1, source2, target);
        }
    }

    public static final class SourceTargetCapture<I, O> {
        private final Key<I> source;
        private final Key<O> target;

        private SourceTargetCapture(Key<I> source, Key<O> target) {
            this.source = source;
            this.target = target;
        }

        public Flow<O> using(Extractor<I, O> extractor) {
            return from(source).to(target).using(
                    "Extract " + target.getName() + " from " + source.getName(),
                    new ExtractorFunction<I, O>(extractor, source));
        }
    }

    public static final class DoubleSourceTargetCapture<I1, I2, O> {
        private final Key<I1> source1;
        private final Key<I2> source2;
        private final Key<O> target;

        public DoubleSourceTargetCapture(Key<I1> source1, Key<I2> source2, Key<O> target) {
            this.source1 = source1;
            this.source2 = source2;
            this.target = target;
        }

        public Flow<O> using(Extractor2<I1, I2, O> extractor) {
            return from(source1, source2).to(target).using(
                    "Extract " + target.getName() + " from " + source1.getName() + " and " + source2.getName(),
                    new ExtractorFunction2<I1, I2, O>(extractor, source1, source2));
        }
    }

    public static <O> TargetCapture<O> extracting(Key<O> target) {
        return new TargetCapture<>(target);
    }

    public static <T> T run(Flow<T> flow, Scratchpad initialScratchpad, FlowVisitor flowVisitor) {
        Set<Key<?>> missingKeys = new HashSet<>();
        for (Key<?> inputKey: flow.getRequiredKeys()) {
            if (!initialScratchpad.containsKey(inputKey)) {
                missingKeys.add(inputKey);
            }
        }
        if (!missingKeys.isEmpty()) {
            throw new MissingKeysException(missingKeys);
        }

        Action action = flow.visit(flowVisitor);
        Scratchpad finalScratchpad = action.run(initialScratchpad);
        return finalScratchpad.get(flow.getProvidedKey());
    }

    private static class ExtractorFunction<I, O> implements ScratchpadFunction<O> {
        private final Extractor<I, O> extractor;
        private final Key<I> source;

        private ExtractorFunction(Extractor<I, O> extractor, Key<I> source) {
            this.extractor = extractor;
            this.source = source;
        }

        @Override
        public O apply(Scratchpad scratchpad) {
            return extractor.extract(scratchpad.get(source));
        }
    }

    private static class ExtractorFunction2<I1, I2, O> implements ScratchpadFunction<O> {
        private final Extractor2<I1, I2, O> extractor;
        private final Key<I1> source1;
        private final Key<I2> source2;

        private ExtractorFunction2(Extractor2<I1, I2, O> extractor, Key<I1> source1, Key<I2> source2) {
            this.extractor = extractor;
            this.source1 = source1;
            this.source2 = source2;
        }

        @Override
        public O apply(Scratchpad scratchpad) {
            return extractor.extract(scratchpad.get(source1), scratchpad.get(source2));
        }
    }
}
