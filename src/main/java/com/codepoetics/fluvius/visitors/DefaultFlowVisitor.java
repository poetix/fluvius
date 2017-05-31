package com.codepoetics.fluvius.visitors;

import com.codepoetics.fluvius.api.*;

import java.util.List;
import java.util.Map;

final class DefaultFlowVisitor implements FlowVisitor {

    @Override
    public <T> Action visitSingle(Key<T> outputKey, Operation<T> operation) {
        return SingleOperationAction.of(outputKey, operation);
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
