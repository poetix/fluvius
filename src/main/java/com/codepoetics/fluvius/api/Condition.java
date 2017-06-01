package com.codepoetics.fluvius.api;

import com.codepoetics.fluvius.api.scratchpad.Scratchpad;

import java.io.Serializable;

public interface Condition extends Serializable {
    String getDescription();
    boolean test(Scratchpad scratchpad);
}
