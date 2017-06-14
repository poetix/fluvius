package com.codepoetics.fluvius.api.scratchpad;

import java.io.Serializable;
import java.util.Map;

/**
 * An immutable collection of Key/value pairs.
 */
public interface Scratchpad extends Serializable {

  /**
   * Returns a "locked" version of this Scratchpad, in which written values cannot be overwritten.
   * @return The locked Scratchpad.
   */
  Scratchpad locked();

  /**
   * Tests whether this Scratchpad contains the specified Key.
   *
   * @param key The Key to test for.
   * @return True if the Key is present, false otherwise.
   */
  boolean containsKey(Key<?> key);

  /**
   * Create a copy of this Scratchpad, updated with the supplied Key/value pairs.
   *
   * @param keyValues The KeyValues to add to this Scratchpad.
   * @return The updated Scratchpad.
   */
  Scratchpad with(KeyValue... keyValues);

  /**
   * Get the value associated with the supplied Key in this Scratchpad.
   *
   * <p>A NullPointerException is thrown if the Key is not present.</p>
   *
   * @param key The Key to retrieve the value for.
   * @param <T> The type of the Key/value.
   * @return The retrieved value.
   */
  <T> T get(Key<T> key);

  /**
   * Get a representation of this Scratchpad's storage as a Map of Keys to untyped objects.
   *
   * @return The Map representing this Scratchpad's storage.
   */
  Map<Key<?>, Object> toMap();

}
