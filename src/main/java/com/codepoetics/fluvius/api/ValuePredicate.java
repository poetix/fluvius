package com.codepoetics.fluvius.api;

public interface ValuePredicate<T> {
    boolean test(T value);
}
