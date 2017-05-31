package com.codepoetics.fluvius.api;

public interface Operation<T> {
    String getName();
    T run(Scratchpad scratchpad);
}
