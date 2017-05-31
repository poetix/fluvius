package com.codepoetics.fluvius.api;

public interface ScratchpadStorage {

    boolean containsKey(Key<?> key);
    void put(Key<?> key, Object value);
    <T> T get(Key<T> key);
    ScratchpadStorage copy();

}
