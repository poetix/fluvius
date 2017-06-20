package com.codepoetics.fluvius.flows;

import com.codepoetics.fluvius.api.scratchpad.Key;

import java.util.Set;

/**
 * The stage in the Fluent API where we have captured the input keys to supply to a Flow.
 */
public final class InputKeysCapture {
  private final Set<Key<?>> inputKeys;

  InputKeysCapture(Set<Key<?>> inputKeys) {
    this.inputKeys = inputKeys;
  }

  /**
   * Capture the output key that will be written by a Flow.
   *
   * @param outputKey The output key that will be written by a Flow.
   * @param <T>       The type of the output key.
   * @return The next stage in the Fluent API.
   */
  public <T> OutputKeysCapture<T> to(Key<T> outputKey) {
    return new OutputKeysCapture<>(inputKeys, outputKey);
  }
}
