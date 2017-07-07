package com.codepoetics.fluvius.api.scratchpad;

import java.lang.reflect.Type;

/**
 * A {@link Key} provider that guarantees that keys issued with the same name will be identical and have the same type.
 */
public interface KeyProvider {

  /**
   * Issue a unique {@link Key} having the given name, checking that the key has not previously been issued for a different {@link Type}.
   * @param name The name of the key to issue.
   * @param type The {@link Type} of the value addressed by the key.
   * @param <T> The type of the value addressed by the key.
   * @return The issued key.
   */
  <T> Key<T> getKey(String name, Type type);

}
