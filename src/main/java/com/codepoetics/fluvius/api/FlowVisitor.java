package com.codepoetics.fluvius.api;

import com.codepoetics.fluvius.api.scratchpad.Key;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface FlowVisitor<V> {

    <T> V visitSingle(Set<Key<?>> requiredKeys, Key<T> providedKey, Operation<T> operation);
    <T> V visitSequence(List<V> items, Set<Key<?>> requiredKeys, Key<T> providedKey);
    <T> V visitBranch(V defaultBranch, Map<String, ConditionalValue<V>> conditionalBranches, Set<Key<?>> requiredKeys, Key<T> providedKey);
    Condition visitCondition(Condition condition);

}
