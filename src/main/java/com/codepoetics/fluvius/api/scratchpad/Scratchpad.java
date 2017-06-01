package com.codepoetics.fluvius.api.scratchpad;

public interface Scratchpad {

    boolean containsKey(Key<?> key);
    Scratchpad with(KeyValue...keyValues);
    <T> T get(Key<T> key);

}
