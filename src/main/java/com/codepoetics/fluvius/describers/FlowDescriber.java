package com.codepoetics.fluvius.describers;

import com.codepoetics.fluvius.api.*;
import com.codepoetics.fluvius.api.description.FlowDescription;
import com.codepoetics.fluvius.api.scratchpad.Key;

import java.util.*;

public class FlowDescriber implements FlowVisitor<FlowDescription> {

    public static <T> FlowDescription describe(Flow<T> flow) {
        return flow.visit(new FlowDescriber());
    }

    private FlowDescriber() {
    }

    @Override
    public <T> FlowDescription visitSingle(Set<Key<?>> requiredKeys, Key<T> providedKey, Operation<T> operation) {
        return new SingleFlowDescription(operation.getName(), toKeyNames(requiredKeys), providedKey.getName());
    }

    @Override
    public <T> FlowDescription visitSequence(List<FlowDescription> flows, Set<Key<?>> requiredKeys, Key<T> providedKey) {
        return new SequenceFlowDescription(toKeyNames(requiredKeys), providedKey.getName(), flows);
    }

    @Override
    public <T> FlowDescription visitBranch(FlowDescription defaultBranch, Map<String, ConditionalValue<FlowDescription>> conditionalBranches, Set<Key<?>> requiredKeys, Key<T> providedKey) {
        Map<String, FlowDescription> branchDescriptions = new LinkedHashMap<>();
        for (Map.Entry<String, ConditionalValue<FlowDescription>> entry : conditionalBranches.entrySet()) {
            branchDescriptions.put(entry.getKey(), entry.getValue().getValue());
        }
        return new BranchFlowDescription(toKeyNames(requiredKeys), providedKey.getName(), defaultBranch, branchDescriptions);
    }

    @Override
    public Condition visitCondition(Condition condition) {
        return condition;
    }

    private List<String> toKeyNames(Collection<Key<?>> keys) {
        List<String> keyNames = new ArrayList<>();
        for (Key<?> key : keys) {
            keyNames.add(key.getName());
        }
        return keyNames;
    }

}
