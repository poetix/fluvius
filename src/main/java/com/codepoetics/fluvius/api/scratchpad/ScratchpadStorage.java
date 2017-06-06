package com.codepoetics.fluvius.api.scratchpad;

/**
 * Used to provide write-only access to a Scratchpad's storage for a KeyValue.
 */
public interface ScratchpadStorage {

  /**
   * Write this key/value pair into a Scratchpad's storage.
   *
   * @param key   The Key to write.
   * @param value The value to write.
   * @param <T>   The type of the key/value pair to write.
   */
  <T> void put(Key<T> key, T value);

}
