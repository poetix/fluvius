package com.codepoetics.fluvius.scratchpad;

import com.codepoetics.fluvius.api.scratchpad.Key;
import com.codepoetics.fluvius.api.scratchpad.KeyValue;
import com.codepoetics.fluvius.api.scratchpad.ScratchpadStorage;
import com.codepoetics.fluvius.preconditions.Preconditions;

import java.util.Objects;
import java.util.UUID;

import static com.codepoetics.fluvius.preconditions.Preconditions.checkNotNull;

public final class Keys {

    private Keys() {
    }

    public static <T> Key<T> named(String name) {
        return new RealKey<>(checkNotNull("name", name), UUID.randomUUID());
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
            storage.put((Key<Object>) key, value);
        }

        @Override
        public String toString() {
            return key + ": " + value;
        }
    }

    private static final class RealKey<T> implements Key<T> {

        private final String name;
        private final UUID id;

        private RealKey(String name, UUID id) {
            this.name = name;
            this.id = id;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public KeyValue of(T value) {
            return new RealKeyValue(this, checkNotNull("value", value));
        }

        @Override
        public boolean equals(Object o) {
            return o == this ||
                    (o instanceof RealKey
                            && ((RealKey) o).id.equals(id));
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, id);
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
