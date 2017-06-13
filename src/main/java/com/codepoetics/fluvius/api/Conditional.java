package com.codepoetics.fluvius.api;

import com.codepoetics.fluvius.api.functional.F1;

/**
 * A value with an attached Condition.
 *
 * @param <V> The type of the value.
 */
public interface Conditional<V> {
  /**
   * Get the Condition associated with the value.
   *
   * @return The Condition associated with the value.
   */
  Condition getCondition();

  /**
   * Get the value.
   *
   * @return The value.
   */
  V getValue();

  <V2> Conditional<V2> map(F1<? super V, ? extends V2> mapper);
}
