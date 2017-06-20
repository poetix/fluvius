package com.codepoetics.fluvius.exceptions;

import com.codepoetics.fluvius.api.scratchpad.Key;

import java.util.Set;

/**
 * Thrown when a Flow is executed but the supplied Scratchpad does not contain all of the keys required by the Flow.
 */
public class MissingKeysException extends RuntimeException {

  /**
   * Create a MissingKeysException.
   * @param missingKeys The Set of Keys which were missing.
   * @return The exception.
   */
  public static MissingKeysException create(final Set<Key<?>> missingKeys) {
    final StringBuilder sb = new StringBuilder();
    sb.append("Missing keys: ");
    boolean isFirst = true;

    for (final Key<?> key : missingKeys) {
      if (isFirst) {
        isFirst = false;
      } else {
        sb.append(",");
      }
      sb.append(key.getName());
    }

    return new MissingKeysException(sb.toString());
  }

  private MissingKeysException(final String message) {
    super(message);
  }

}
