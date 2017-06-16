package com.codepoetics.fluvius.api.services;

/**
 * The result of making a call to an external service, which may fail.
 *
 * @param <T> The type of the value returned by the external service.
 */
public interface ServiceCallResult<T> {

  /**
   * Whether the service call succeeded or not.
   *
   * @return True if the external service call succeeded, false otherwise.
   */
  boolean succeeded();

  /**
   * Get the result returned by the external service.
   *
   * @return The result returned by the external service.
   * @throws UnsupportedOperationException If called on a result that was not successful.
   */
  T result();

  /**
   * Get a string representation of the reason why the external call failed.
   *
   * @return The reason why the external call failed.
   * @throws UnsupportedOperationException If called on a result that did not fail.
   */
  String failureReason();
}
