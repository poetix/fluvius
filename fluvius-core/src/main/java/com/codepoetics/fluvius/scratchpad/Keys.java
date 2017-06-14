package com.codepoetics.fluvius.scratchpad;

import static com.codepoetics.fluvius.preconditions.Preconditions.checkNotNull;

import com.codepoetics.fluvius.api.scratchpad.Key;
import com.codepoetics.fluvius.api.scratchpad.KeyValue;
import com.codepoetics.fluvius.api.scratchpad.ScratchpadStorage;

import java.util.Objects;
import java.util.UUID;


/**
 * Utility class for creating Keys.
 */
public final class Keys {

  private Keys() {
  }

  /**
   * Create a Key with the given name.
   *
   * @param name The name of the Key to create.
   * @param <T>  The type of the Key to create.
   * @return The created Key.
   */
  public static <T> Key<T> named(final String name) {
    return new RealKey<>(checkNotNull("name", name), UUID.randomUUID());
  }

  private static final class RealKey<T> implements Key<T> {

    private final String name;
    private final UUID id;

    private RealKey(final String name, final UUID id) {
      this.name = name;
      this.id = id;
    }

    @Override
    public String getName() {
      return name;
    }

    @Override
    public KeyValue of(final T value) {
      return new RealKeyValue(this, checkNotNull("value", value));
    }

    @Override
    public boolean equals(final Object other) {
      return other == this
          || (other instanceof RealKey
              && ((RealKey) other).id.equals(id));
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

  private static final class RealKeyValue implements KeyValue {

    private final Key<?> key;
    private final Object value;

    private RealKeyValue(final Key<?> key, final Object value) {
      this.key = key;
      this.value = value;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void store(final ScratchpadStorage storage) {
      storage.put((Key<Object>) key, value);
    }

    @Override
    public String toString() {
      return key + ": " + value;
    }
  }
}
