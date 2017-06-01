package com.codepoetics.fluvius.api.scratchpad;

public interface Key<T> {
    String getName();
    KeyValue of(T value);
}
