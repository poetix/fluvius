package com.codepoetics.fluvius.api;

import com.codepoetics.fluvius.api.scratchpad.Scratchpad;

import java.io.Serializable;

public interface Action extends Serializable {
    Scratchpad run(Scratchpad scratchpad);
}
