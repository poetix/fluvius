package com.codepoetics.fluvius.flows;

import com.codepoetics.fluvius.api.Action;
import com.codepoetics.fluvius.api.Flow;
import com.codepoetics.fluvius.api.FlowVisitor;
import com.codepoetics.fluvius.api.description.FlowDescriber;
import com.codepoetics.fluvius.api.scratchpad.Key;
import com.codepoetics.fluvius.preconditions.Preconditions;

import java.util.*;

public class SequenceFlow<T> extends AbstractFlow<T> {

    public static <T> Flow<T> create(Flow<?> first, Flow<T> last) {
        Preconditions.checkNotNull("first", first);
        Preconditions.checkNotNull("last", last);

        return create(first, Collections.<Flow<?>>emptyList(), last);
    }

    private static <T> Flow<T> create(Flow<?> first, List<Flow<?>> middle, Flow<T> last) {
        Set<Key<?>> requiredKeys = first.getRequiredKeys();
        Set<Key<?>> providedKeys = new HashSet<>();
        providedKeys.add(first.getProvidedKey());

        for (Flow<?> flow : middle) {
            Set<Key<?>> requiredKeysForStage = flow.getRequiredKeys();
            requiredKeysForStage.removeAll(providedKeys);
            requiredKeys.addAll(requiredKeysForStage);
            providedKeys.add(flow.getProvidedKey());
        }

        Set<Key<?>> requiredKeysForLast = last.getRequiredKeys();
        requiredKeysForLast.removeAll(providedKeys);
        requiredKeys.addAll(requiredKeysForLast);

        return new SequenceFlow<>(requiredKeys, first, middle, last);
    }

    private final Flow<?> first;
    private final List<Flow<?>> middle;
    private final Flow<T> last;

    private SequenceFlow(Set<Key<?>> requiredKeys, Flow<?> first, List<Flow<?>> middle, Flow<T> last) {
        super(requiredKeys, last.getProvidedKey());
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
        return describer.describeSequence(allFlows(), getRequiredKeys(), getProvidedKey());
    }

    @Override
    public <N> Flow<N> then(Flow<N> next) {
        List<Flow<?>> newMiddle = new ArrayList<>(middle);
        newMiddle.add(last);
        return create(first, newMiddle, next);
    }
}
