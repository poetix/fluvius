package com.codepoetics.fluvius.api.scratchpad;

import java.io.Serializable;

/**
 * An immutable collection of Key/value pairs.
 */
public interface Scratchpad extends Serializable {

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
   * Get the value associated with the supplied Key in this Scratchpad. A NullPointerException is thrown if the Key is
   * not present.
   *
   * @param key The Key to retrieve the value for.
   * @param <T> The type of the Key/value.
   * @return The retrieved value.
   */
  <T> T get(Key<T> key);

}
