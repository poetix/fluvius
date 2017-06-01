package com.codepoetics.fluvius.api;

import com.codepoetics.fluvius.api.scratchpad.Scratchpad;

public interface Condition {
    String getDescription();
    boolean test(Scratchpad scratchpad);
}
