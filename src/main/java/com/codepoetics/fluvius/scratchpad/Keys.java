package com.codepoetics.fluvius.scratchpad;

import com.codepoetics.fluvius.api.Key;
import com.codepoetics.fluvius.api.KeyValue;
import com.codepoetics.fluvius.api.ScratchpadStorage;

public final class Keys {

    private Keys() {
    }

    public static <T> Key<T> create(String name) {
        return new RealKey<T>(name);
    }

    private static final class RealKeyValue implements KeyValue {

        private final Key<?> key;
        private final Object value;

        private RealKeyValue(Key<?> key, Object value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public void store(ScratchpadStorage storage) {
            storage.put(key, value);
        }

        @Override
        public String toString() {
            return key + ": " + value;
        }
    }

    private static final class RealKey<T> implements Key<T> {

        private final String name;

        private RealKey(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public KeyValue of(T value) {
            return new RealKeyValue(this, value);
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
