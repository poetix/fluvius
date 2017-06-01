package com.codepoetics.fluvius.api;

import com.codepoetics.fluvius.api.scratchpad.Key;

import java.io.Serializable;
import java.util.Set;

public interface Flow<T> extends Serializable {

    Set<Key<?>> getRequiredKeys();
    Key<T> getProvidedKey();
    <V> V visit(FlowVisitor<V> visitor);

    <N> Flow<N> then(Flow<N> next);
    Flow<T> orIf(Condition condition, Flow<T> ifTrue);
}
