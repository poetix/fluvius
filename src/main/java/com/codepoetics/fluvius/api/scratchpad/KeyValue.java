package com.codepoetics.fluvius.api.scratchpad;

import java.io.Serializable;

public interface KeyValue extends Serializable {

    void store(ScratchpadStorage storage);

}
