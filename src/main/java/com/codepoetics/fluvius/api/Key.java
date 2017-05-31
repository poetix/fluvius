package com.codepoetics.fluvius.api;

public interface Key<T> {
    String getName();
    KeyValue of(T value);
}
