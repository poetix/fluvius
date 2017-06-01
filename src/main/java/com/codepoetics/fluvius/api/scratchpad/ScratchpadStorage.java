package com.codepoetics.fluvius.api.scratchpad;

import java.io.Serializable;

public interface ScratchpadStorage extends Serializable {

    boolean containsKey(Key<?> key);
    void put(Key<?> key, Object value);
    <T> T get(Key<T> key);
    ScratchpadStorage copy();

}
