package com.codepoetics.fluvius.api.scratchpad;

import java.io.Serializable;

/**
 * A Key identifies a value written into a Scratchpad, and is used to construct new key/value pairs to be added to a Scratchpad.
 * <p>
 *   Values may be either values of the specified type, or failures for which a {@link Throwable} reason is given.
 * </p>
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
   * @param value The value to associate with this Key.
   * @return The constructed key/value pair.
   */
  KeyValue of(T value);

  /**
   * Construct a key/value pair using this key and the supplied failure reason.
   *
   * @param reason The failure reason to associate with this key.
   * @return The constructed key/value pair
   */
  KeyValue ofFailure(Throwable reason);
}
