package com.codepoetics.fluvius.flows;

import com.codepoetics.fluvius.api.*;
import com.codepoetics.fluvius.api.functional.F1;
import com.codepoetics.fluvius.api.functional.F2;
import com.codepoetics.fluvius.api.functional.ScratchpadFunction;
import com.codepoetics.fluvius.api.scratchpad.Key;
import com.codepoetics.fluvius.api.scratchpad.Scratchpad;
import com.codepoetics.fluvius.describers.FlowDescriber;
import com.codepoetics.fluvius.describers.PrettyPrintingDescriptionWriter;
import com.codepoetics.fluvius.exceptions.MissingKeysException;
import com.codepoetics.fluvius.operations.Operations;

import java.util.*;

public final class Flows {

    private Flows() {
    }

    public static String prettyPrint(Flow<?> flow) {
        PrettyPrintingDescriptionWriter descriptionWriter = PrettyPrintingDescriptionWriter.create();
        FlowDescriber.describe(flow).writeTo(descriptionWriter);
        return descriptionWriter.toString();
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
            return new SourceTargetCapture<>(source, target);
        }

        public <I1, I2> DoubleSourceTargetCapture<I1, I2, O> from(Key<I1> source1, Key<I2> source2) {
            return new DoubleSourceTargetCapture<>(source1, source2, target);
        }
    }

    public static final class SourceTargetCapture<I, O> {
        private final Key<I> source;
        private final Key<O> target;

        private SourceTargetCapture(Key<I> source, Key<O> target) {
            this.source = source;
            this.target = target;
        }

        public Flow<O> using(String name, F1<I, O> f1) {
            return from(source).to(target).using(
                    name,
                    new ExtractorFunction<>(f1, source));
        }

        public Flow<O> using(F1<I, O> f1) {
            return using("Extract " + target.getName() + " from " + source.getName(), f1);
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

        public Flow<O> using(String name, F2<I1, I2, O> extractor) {
            return from(source1, source2).to(target).using(
                    name,
                    new ExtractorFunction2<>(extractor, source1, source2));
        }
        public Flow<O> using(F2<I1, I2, O> extractor) {
            return using("Extract " + target.getName() + " from " + source1.getName() + " and " + source2.getName(), extractor);
        }
    }

    public static <O> TargetCapture<O> obtaining(Key<O> target) {
        return new TargetCapture<>(target);
    }

    public static <T> T run(Flow<T> flow, Scratchpad initialScratchpad, FlowVisitor<Action> flowVisitor) {
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
        private final F1<I, O> f1;
        private final Key<I> source;

        private ExtractorFunction(F1<I, O> f1, Key<I> source) {
            this.f1 = f1;
            this.source = source;
        }

        @Override
        public O apply(Scratchpad scratchpad) {
            return f1.apply(scratchpad.get(source));
        }
    }

    private static class ExtractorFunction2<I1, I2, O> implements ScratchpadFunction<O> {
        private final F2<I1, I2, O> extractor;
        private final Key<I1> source1;
        private final Key<I2> source2;

        private ExtractorFunction2(F2<I1, I2, O> extractor, Key<I1> source1, Key<I2> source2) {
            this.extractor = extractor;
            this.source1 = source1;
            this.source2 = source2;
        }

        @Override
        public O apply(Scratchpad scratchpad) {
            return extractor.apply(scratchpad.get(source1), scratchpad.get(source2));
        }
    }

    public static <T> BranchBuilder<T> branch(final Condition condition, final Flow<T> ifTrue) {
        return new BranchBuilder<T>() {
            private final Map<Condition, Flow<T>> branches = new LinkedHashMap<>();
            {
                branches.put(condition, ifTrue);
            }

            @Override
            public BranchBuilder<T> orIf(Condition condition, Flow<T> ifTrue) {
                branches.put(condition, ifTrue);
                return this;
            }

            @Override
            public Flow<T> otherwise(Flow<T> defaultFlow) {
                Flow<T> result = defaultFlow;
                for (Map.Entry<Condition, Flow<T>> branchEntry : branches.entrySet()) {
                    result = result.orIf(branchEntry.getKey(), branchEntry.getValue());
                }
                return result;
            }
        };
    }
}
