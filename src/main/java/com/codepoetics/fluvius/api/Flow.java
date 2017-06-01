package com.codepoetics.fluvius.api;

import com.codepoetics.fluvius.api.description.DescribableFlow;
import com.codepoetics.fluvius.api.scratchpad.Key;

import java.io.Serializable;
import java.util.Set;

public interface Flow<T> extends DescribableFlow, Serializable {

    Set<Key<?>> getRequiredKeys();
    Key<T> getProvidedKey();
    <V extends FlowVisitor> Action visit(V visitor);

    <N> Flow<N> then(Flow<N> next);
    Flow<T> orIf(Condition condition, Flow<T> ifTrue);
}
