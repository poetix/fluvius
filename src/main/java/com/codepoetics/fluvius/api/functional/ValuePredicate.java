package com.codepoetics.fluvius.api.functional;

public interface ValuePredicate<T> {
    boolean test(T value);
}
