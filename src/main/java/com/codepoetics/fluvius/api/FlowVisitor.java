package com.codepoetics.fluvius.api;

import java.util.List;
import java.util.Map;

public interface FlowVisitor {

    <T> Action visitSingle(Key<T> outputKey, Operation<T> operation);
    Action visitSequence(List<Action> actions);
    Action visitBranch(Action defaultAction, Map<String, ConditionalAction> conditionalActions);
    Condition visitCondition(Condition condition);

}
