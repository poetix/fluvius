package com.codepoetics.fluvius.scratchpad;

import com.codepoetics.fluvius.api.scratchpad.KeyValue;
import com.codepoetics.fluvius.api.scratchpad.Scratchpad;

public final class Scratchpads {

    private Scratchpads() {
    }

    public static Scratchpad create(KeyValue...keyValues) {
        return RealScratchpad.create(keyValues);
    }
}
