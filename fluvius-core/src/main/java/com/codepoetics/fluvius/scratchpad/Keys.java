package com.codepoetics.fluvius.scratchpad;

import com.codepoetics.fluvius.api.scratchpad.Key;
import com.codepoetics.fluvius.api.scratchpad.KeyProvider;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for working with {@link Key}s.
 */
public final class Keys {

  /**
   * Create a new {@link KeyProvider}. Each new KeyProvider will ensure the uniqueness / type-consistency only of the keys it has issued.
   *
   * @return The created {@link KeyProvider}.
   */
  public static KeyProvider createProvider() {
    return new DefaultKeyProvider();
  }

  private static final class DefaultKeyProvider implements KeyProvider {

    private final Map<String, Key<?>> issuedKeys = new HashMap<>();
    private final Map<String, Type> issuedKeyTypes = new HashMap<>();

    @SuppressWarnings("unchecked")
    @Override
    public <T> Key<T> getKey(String name, Type type) {
      Type previousType = issuedKeyTypes.get(name);
      if (previousType == null) {
        Key<T> key = Key.named(name);
        issuedKeys.put(name, key);
        issuedKeyTypes.put(name, type);
        return key;
      }
      if (previousType.equals(type) || type.equals(Exception.class)) {
        return (Key<T>) issuedKeys.get(name);
      }
      throw new IllegalArgumentException(
          "Key named " + name
              + " requested with type " + type
              + ", but has already been issued by this provider with type " + previousType);
    }
  }
}
