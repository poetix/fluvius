package com.codepoetics.fluvius.api.functional;

import com.codepoetics.fluvius.api.scratchpad.Scratchpad;

public interface ScratchpadFunction<T> {
    T apply(Scratchpad scratchpad);
}
