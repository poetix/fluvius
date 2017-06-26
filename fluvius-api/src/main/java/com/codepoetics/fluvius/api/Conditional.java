package com.codepoetics.fluvius.api;

import com.codepoetics.fluvius.api.functional.Mapper;

/**
 * A value with an attached Condition.
 *
 * @param <V> The type of the value.
 */
public final class Conditional<V> {

  public static <V> Conditional<V> of(Condition condition, V value) {
    return new Conditional<>(condition, value);
  }

  private final V value;
  private final Condition condition;

  private Conditional(Condition condition, V value) {
    this.value = value;
    this.condition = condition;
  }

  /**
   * Get the Condition associated with the value.
   *
   * @return The Condition associated with the value.
   */
  public Condition getCondition() {
    return condition;
  }

  /**
   * Get the value.
   *
   * @return The value.
   */
  public V getValue() {
    return value;
  }

  public <V2> Conditional<V2> map(Mapper<? super V, ? extends V2> mapper) {
    return new Conditional<>(condition, mapper.apply(value));
  }
}
