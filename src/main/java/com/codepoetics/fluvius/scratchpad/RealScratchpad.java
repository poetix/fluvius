package com.codepoetics.fluvius.scratchpad;

import com.codepoetics.fluvius.api.Key;
import com.codepoetics.fluvius.api.KeyValue;
import com.codepoetics.fluvius.api.Scratchpad;
import com.codepoetics.fluvius.api.ScratchpadStorage;

final class RealScratchpad implements Scratchpad {

    static Scratchpad using(ScratchpadStorage storage) {
        return new RealScratchpad(storage);
    }

    private final ScratchpadStorage storage;

    private RealScratchpad(ScratchpadStorage storage) {
        this.storage = storage;
    }

    @Override
    public boolean containsKey(Key<?> key) {
        return storage.containsKey(key);
    }

    @Override
    public Scratchpad with(KeyValue... keyValues) {
        ScratchpadStorage newStorage = storage.copy();
        for (KeyValue keyValue : keyValues) {
            keyValue.store(newStorage);
        }
        return new RealScratchpad(newStorage);
    }

    @Override
    public <T> T get(Key<T> key) {
        return storage.get(key);
    }

    @Override
    public String toString() {
        return storage.toString();
    }
}
