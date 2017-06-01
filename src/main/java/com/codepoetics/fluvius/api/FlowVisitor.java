package com.codepoetics.fluvius.api;

import com.codepoetics.fluvius.api.scratchpad.Key;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface FlowVisitor {

    <T> Action visitSingle(Set<Key<?>> requiredKeys, Key<T> providedKey, Operation<T> operation);
    Action visitSequence(List<Action> actions);
    Action visitBranch(Action defaultAction, Map<String, ConditionalAction> conditionalActions);
    Condition visitCondition(Condition condition);

}
