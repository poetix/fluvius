package com.codepoetics.fluvius.api.functional;

import java.io.Serializable;

/**
 * A predicate which can be used to test a value.
 *
 * @param <T> The type of the value tested by this predicate.
 */
public interface P1<T> extends Serializable {
  /**
   * Apply this predicate to the supplied value.
   *
   * @param value The value to test.
   * @return True if the value matches this predicate, false otherwise.
   */
  boolean test(T value);
}
