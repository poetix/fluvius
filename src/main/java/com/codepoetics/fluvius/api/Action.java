package com.codepoetics.fluvius.api;

import com.codepoetics.fluvius.api.scratchpad.Scratchpad;

public interface Action {
    Scratchpad run(Scratchpad scratchpad);
}
