package com.codepoetics.fluvius.flows;

import com.codepoetics.fluvius.api.*;
import com.codepoetics.fluvius.exceptions.MissingKeysException;
import com.codepoetics.fluvius.preconditions.Preconditions;

import java.util.*;

public class SequenceFlow<T> extends AbstractFlow<T> {

    public static <T> Flow<T> create(Flow<?> first, Flow<T> last) {
        Preconditions.checkNotNull("first", first);
        Preconditions.checkNotNull("last", last);

        return create(first, Collections.<Flow<?>>emptyList(), last);
    }

    private static <T> Flow<T> create(Flow<?> first, List<Flow<?>> middle, Flow<T> last) {
        Set<Key<?>> required = getRequiredKeys(first, middle, last);
        Set<Key<?>> provided = getProvidedKeys(first, middle);

        required.removeAll(provided);

        return new SequenceFlow<>(required, first, middle, last);
    }

    private static Set<Key<?>> getRequiredKeys(Flow<?> first, List<Flow<?>> middle, Flow<?> last) {
        Set<Key<?>> required = new HashSet<>();
        required.addAll(first.getInputKeys());
        for (Flow<?> flow : middle) {
            required.addAll(flow.getInputKeys());
        }
        required.addAll(last.getInputKeys());
        return required;
    }

    private static Set<Key<?>> getProvidedKeys(Flow<?> first, List<Flow<?>> middle) {
        Set<Key<?>> provided = new HashSet<>(middle.size() + 1);
        provided.add(first.getOutputKey());
        for (Flow<?> flow : middle) {
            provided.add(flow.getOutputKey());
        }
        return provided;
    }

    private final Flow<?> first;
    private final List<Flow<?>> middle;
    private final Flow<T> last;

    private SequenceFlow(Set<Key<?>> inputKeys, Flow<?> first, List<Flow<?>> middle, Flow<T> last) {
        super(inputKeys, last.getOutputKey());
        this.first = first;
        this.middle = middle;
        this.last = last;
    }

    private List<Flow<?>> allFlows() {
        List<Flow<?>> flows = new ArrayList<>(middle.size() + 2);
        flows.add(first);
        flows.addAll(middle);
        flows.add(last);
        return flows;
    }

    @Override
    public <V extends FlowVisitor> Action visit(V visitor) {
        List<Action> actions = new ArrayList<>();
        for (Flow<?> flow: allFlows()) {
            actions.add(flow.visit(visitor));
        }
        return visitor.visitSequence(actions);
    }

    @Override
    public <D extends FlowDescriber<D>> D describe(FlowDescriber<D> describer) {
        return describer.describeSequence(allFlows());
    }

    @Override
    public <N> Flow<N> then(Flow<N> next) {
        List<Flow<?>> newMiddle = new ArrayList<>(middle);
        newMiddle.add(last);
        return create(first, newMiddle, next);
    }
}
