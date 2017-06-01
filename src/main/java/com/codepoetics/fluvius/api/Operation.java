package com.codepoetics.fluvius.api;

import com.codepoetics.fluvius.api.scratchpad.Scratchpad;

import java.io.Serializable;

public interface Operation<T> extends Serializable {
    String getName();
    T run(Scratchpad scratchpad);
}
