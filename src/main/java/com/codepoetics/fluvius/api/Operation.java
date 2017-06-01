package com.codepoetics.fluvius.api;

import com.codepoetics.fluvius.api.scratchpad.Scratchpad;

public interface Operation<T> {
    String getName();
    T run(Scratchpad scratchpad);
}
