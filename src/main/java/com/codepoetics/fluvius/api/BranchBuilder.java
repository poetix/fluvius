package com.codepoetics.fluvius.api;

public interface BranchBuilder<T> {
    BranchBuilder<T> orIf(Condition condition, Flow<T> ifTrue);
    Flow<T> otherwise(Flow<T> defaultFlow);
}
