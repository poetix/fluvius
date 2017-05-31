package com.codepoetics.fluvius.scratchpad;

import com.codepoetics.fluvius.api.KeyValue;
import com.codepoetics.fluvius.api.Scratchpad;

public final class Scratchpads {

    private Scratchpads() {
    }

    public static Scratchpad create(KeyValue...keyValues) {
        return RealScratchpad.using(HashMapScratchpadStorage.empty()).with(keyValues);
    }
}
