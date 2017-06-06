package com.codepoetics.fluvius.api.scratchpad;

import java.io.Serializable;

/**
 * A Key identifies a value written into a Scratchpad, and is used to construct new key/value pairs to be added to a Scratchpad.
 *
 * @param <T> The type of the value indexed by this Key.
 */
public interface Key<T> extends Serializable {
  /**
   * Get the name of this Key.
   *
   * @return The name of this Key.
   */
  String getName();

  /**
   * Construct a key/value pair using this Key and the supplied value.
   *
   * @param value The value to associated with this Key.
   * @return The constructed key/value pair.
   */
  KeyValue of(T value);
}
