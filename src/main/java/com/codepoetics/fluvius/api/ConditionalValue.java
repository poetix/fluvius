package com.codepoetics.fluvius.api;

public interface ConditionalValue<V> {
    Condition getCondition();
    V getValue();
}
