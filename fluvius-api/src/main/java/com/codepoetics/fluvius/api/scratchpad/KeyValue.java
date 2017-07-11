package com.codepoetics.fluvius.api.scratchpad;

import java.io.Serializable;
import java.util.Objects;

/**
 * A pair of a Key and an associated value, which may be added to a Scratchpad's storage.
 */
public final class KeyValue implements Serializable {

  private final Key<?> key;
  private final Object value;

  KeyValue(Key<?> key, Object value) {
    this.key = key;
    this.value = value;
  }

  /**
   * Store this key/value pair in the supplied ScratchpadStorage.
   *
   * @param storage The ScratchpadStorage to write this key/value pair into.
   */
  public void store(ScratchpadStorage storage) {
    if (value instanceof Throwable) {
      storage.storeFailure(key, (Throwable) value);
    } else {
      storage.storeSuccess((Key) key, value);
    }
  }

  @Override
  public boolean equals(Object other) {
    return this == other
        || (other instanceof KeyValue
          && ((KeyValue) other).key.equals(key)
          && ((KeyValue) other).value.equals(value));
  }

  @Override
  public int hashCode() {
    return Objects.hash(key, value);
  }

  @Override
  public String toString() {
    return key + ": " + value;
  }

}
