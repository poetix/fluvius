package com.codepoetics.fluvius.exceptions;

import com.codepoetics.fluvius.api.scratchpad.Key;

/**
 * Thrown when an attempt is made to retrieve a value from a {@link com.codepoetics.fluvius.api.scratchpad.Scratchpad} when a failure has been recorded against the supplied {@link Key}.
 */
public class FailedKeyRetrievedException extends RuntimeException {
  public FailedKeyRetrievedException(String keyName, Throwable valueAtKey) {
    super("Attempted to retrieve a value from key '" + keyName + "', but failed with exception: " + valueAtKey, valueAtKey);
  }
}
