package com.codepoetics.fluvius.api.scratchpad;

import java.io.Serializable;

public interface ScratchpadStorage extends Serializable {

    <T> void put(Key<T> key, T value);

}
