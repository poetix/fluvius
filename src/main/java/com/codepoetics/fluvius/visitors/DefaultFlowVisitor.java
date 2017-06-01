package com.codepoetics.fluvius.visitors;

import com.codepoetics.fluvius.api.*;
import com.codepoetics.fluvius.api.scratchpad.Key;

import java.util.List;
import java.util.Map;
import java.util.Set;

final class DefaultFlowVisitor implements FlowVisitor {

    @Override
    public <T> Action visitSingle(Set<Key<?>> requiredKeys, Key<T> providedKey, Operation<T> operation) {
        return SingleOperationAction.of(providedKey, operation);
    }

    @Override
    public Action visitSequence(List<Action> actions) {
        return SequenceAction.of(actions);
    }

    @Override
    public Action visitBranch(Action defaultAction, Map<String, ConditionalAction> conditionalActions) {
        return BranchAction.of(defaultAction, conditionalActions);
    }

    @Override
    public Condition visitCondition(Condition condition) {
        return condition;
    }
}
