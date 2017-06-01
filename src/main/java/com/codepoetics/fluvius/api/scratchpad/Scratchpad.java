package com.codepoetics.fluvius.api.scratchpad;

import java.io.Serializable;

public interface Scratchpad extends Serializable {

    boolean containsKey(Key<?> key);
    Scratchpad with(KeyValue...keyValues);
    <T> T get(Key<T> key);

}
