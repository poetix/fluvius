package com.codepoetics.fluvius.api;

import java.util.Set;

public interface Flow<T> extends DescribableFlow {

    Set<Key<?>> getRequiredKeys();
    Key<T> getProvidedKey();
    <V extends FlowVisitor> Action visit(V visitor);

    <N> Flow<N> then(Flow<N> next);
    Flow<T> orIf(Condition condition, Flow<T> ifTrue);
}
