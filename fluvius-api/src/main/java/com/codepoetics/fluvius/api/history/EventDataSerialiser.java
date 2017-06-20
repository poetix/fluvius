package com.codepoetics.fluvius.api.history;

/**
 * Serialises event data (scratchpad contents, flow results, and exceptions) to a common type for storage/representation purposes.
 *
 * @param <T> The type to which event data is serialised.
 */
public interface EventDataSerialiser<T> {

  /**
   * Serialise a value written into the scratchpad or returned by flow execution to the expected type.
   *
   * @param value The value to serialise.
   * @return The serialised value.
   */
  T serialise(Object value);

  /**
   * Serialise an exception thrown during flow execution to the expected type.
   *
   * @param exception The exception to serialise.
   * @return The serialised exception.
   */
  T serialiseException(Exception exception);
}
