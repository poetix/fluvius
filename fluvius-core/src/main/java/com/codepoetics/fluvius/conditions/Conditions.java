package com.codepoetics.fluvius.conditions;

import com.codepoetics.fluvius.api.Condition;
import com.codepoetics.fluvius.api.functional.P1;
import com.codepoetics.fluvius.api.functional.ScratchpadPredicate;
import com.codepoetics.fluvius.api.scratchpad.Key;
import com.codepoetics.fluvius.api.scratchpad.Scratchpad;
import com.codepoetics.fluvius.predicates.Predicates;

import java.util.UUID;

/**
 * Utility class for working with Conditions.
 */
public final class Conditions {

  private Conditions() {
  }

  /**
   * Create a Condition with the given description, which tests a Scratchpad with the given ScratchpadPredicate.
   *
   * @param description The description of the Condition.
   * @param predicate   The ScratchpadPredicate to apply to the Scratchpad.
   * @return The constructed Condition.
   */
  public static Condition fromPredicate(final String description, final ScratchpadPredicate predicate) {
    return new PredicateCondition(description, predicate);
  }

  /**
   * Create a Condition which tests whether a failure is recorded in the Scratchpad against the given {@link Key}.
   * @param key The key to test.
   * @return The constructed Condition.
   */
  public static Condition keyRecordsFailure(final Key<?> key) {
    return fromPredicate("Failure recorded for key '" + key.getName() + "'", Predicates.isFailure(key));
  }

  /**
   * Create a Condition which tests that the Scratchpad contains the expected value at the given Key.
   *
   * @param key      The Key to test.
   * @param expected The expected Key-value.
   * @param <T>      The type of the Key/value.
   * @return The constructed Condition.
   */
  public static <T> Condition keyEquals(final Key<T> key, final T expected) {
    return fromPredicate(key.getName() + " = " + expected, Predicates.keyEquals(key, expected));
  }

  /**
   * Create a Condition which tests that the Scratchpad contains a value matching the given predicate at the given Key.
   *
   * @param key         The Key to test.
   * @param description Description of the predicate to be applied to the key's value.
   * @param predicate   The predicate to use to test the key's value.
   * @param <T>         The type of the Key/value.
   * @return The constructed Condition.
   */
  public static <T> Condition keyMatches(final Key<T> key, final String description, final P1<T> predicate) {
    return fromPredicate(key.getName() + " " + description, Predicates.keyMatches(key, predicate));
  }

  private static final class PredicateCondition implements Condition {
    private final String description;
    private final ScratchpadPredicate predicate;

    private PredicateCondition(final String description, final ScratchpadPredicate predicate) {
      this.description = description;
      this.predicate = predicate;
    }

    @Override
    public String getDescription() {
      return description;
    }

    @Override
    public boolean test(final UUID flowId, final Scratchpad scratchpad) {
      return predicate.test(scratchpad);
    }
  }
}
