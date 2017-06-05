package com.codepoetics.fluvius.scratchpad;

import com.codepoetics.fluvius.api.scratchpad.Key;
import com.codepoetics.fluvius.api.scratchpad.KeyValue;
import com.codepoetics.fluvius.api.scratchpad.Scratchpad;
import com.codepoetics.fluvius.api.scratchpad.ScratchpadStorage;
import com.codepoetics.fluvius.preconditions.Preconditions;

import java.util.LinkedHashMap;
import java.util.Map;

final class RealScratchpad implements Scratchpad {

    static Scratchpad create(KeyValue...keyValues) {
        return new RealScratchpad(addValuesToMap(new LinkedHashMap<Key<?>, Object>(keyValues.length), keyValues));
    }

    private static Map<Key<?>, Object> addValuesToMap(final Map<Key<?>, Object> map, KeyValue...keyValues) {
        ScratchpadStorage storage = new ScratchpadStorage() {
            @Override
            public <T> void put(Key<T> key, T value) {
                map.put(key, value);
            }
        };
        for (KeyValue keyValue : keyValues) {
            keyValue.store(storage);
        }
        return map;
    }

    private final Map<Key<?>, Object> storage;

    private RealScratchpad(Map<Key<?>, Object> storage) {
        this.storage = storage;
    }

    @Override
    public boolean containsKey(Key<?> key) {
        return storage.containsKey(key);
    }

    @Override
    public Scratchpad with(KeyValue... keyValues) {
        return new RealScratchpad(
                addValuesToMap(
                        new LinkedHashMap<>(storage), keyValues));
    }

    @Override
    public <T> T get(Key<T> key) {
        return Preconditions.checkNotNull("value of key " + key.getName(), (T) storage.get(key));
    }

    @Override
    public boolean equals(Object o) {
        return this == o ||
                (o instanceof RealScratchpad
                    && ((RealScratchpad) o).storage.equals(storage));
    }

    @Override
    public int hashCode() {
        return storage.hashCode();
    }

    @Override
    public String toString() {
        return storage.toString();
    }

}
