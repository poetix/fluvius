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
  <T> void storeSuccess(Key<T> key, T value);

  /**
   * Write a "failure" reason into a Scratchpad's storage.
   *
   * @param key The Key to write.
   * @param reason The failure reason to write.
   */
  void storeFailure(Key<?> key, Throwable reason);

}
