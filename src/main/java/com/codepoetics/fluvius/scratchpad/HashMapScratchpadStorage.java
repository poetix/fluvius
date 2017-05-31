package com.codepoetics.fluvius.scratchpad;

import com.codepoetics.fluvius.api.Key;
import com.codepoetics.fluvius.api.ScratchpadStorage;
import com.codepoetics.fluvius.preconditions.Preconditions;

import java.util.HashMap;
import java.util.Map;

final class HashMapScratchpadStorage implements ScratchpadStorage {

    public static ScratchpadStorage empty() {
        return new HashMapScratchpadStorage(new HashMap<Key<?>, Object>());
    }

    private final Map<Key<?>, Object> storage;

    private HashMapScratchpadStorage(Map<Key<?>, Object> storage) {
        this.storage = storage;
    }

    @Override
    public boolean containsKey(Key<?> key) {
        return storage.containsKey(key);
    }

    @Override
    public void put(Key<?> key, Object value) {
        storage.put(key, value);
    }

    @Override
    public <T> T get(Key<T> key) {
        return Preconditions.checkNotNull("value of key " + key.getName(), (T) storage.get(key));
    }

    @Override
    public ScratchpadStorage copy() {
        return new HashMapScratchpadStorage(new HashMap<>(storage));
    }

    @Override
    public String toString() {
        return storage.toString();
    }
}
