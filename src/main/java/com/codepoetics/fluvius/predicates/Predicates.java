package com.codepoetics.fluvius.predicates;

import com.codepoetics.fluvius.api.functional.P1;
import com.codepoetics.fluvius.api.functional.ScratchpadPredicate;
import com.codepoetics.fluvius.api.scratchpad.Key;
import com.codepoetics.fluvius.api.scratchpad.Scratchpad;

/**
 * Utility class for constructing predicates.
 */
public final class Predicates {

  private Predicates() {
  }

  /**
   * Construct a ScratchpadPredicate that will be true if the Scratchpad contains the specified key, and its value matches the supplied predicate.
   *
   * @param key       The Key to test.
   * @param predicate The predicate to apply to the key's value.
   * @param <T>       The type of the Key/value.
   * @return The constructed ScratchpadPredicate.
   */
  public static <T> ScratchpadPredicate keyMatches(final Key<T> key, final P1<T> predicate) {
    return new MatchingKeyValuePredicate<>(predicate, key);
  }

  /**
   * Construct a ScratchpadPredicate that will be true if the Scratchpad contains the specified key, and its value is equal to the expected value.
   *
   * @param key      The Key to test.
   * @param expected The expected value.
   * @param <T>      The type of the Key/value.
   * @return The constructed ScratchpadPredicate.
   */
  public static <T> ScratchpadPredicate keyEquals(final Key<T> key, final T expected) {
    return keyMatches(key, equalTo(expected));
  }

  /**
   * Constructs a predicate that will return true if the supplied value is equal to the expected value.
   *
   * @param expected The expected value.
   * @param <T>      The type of the expected value.
   * @return The constructed predicate.
   */
  public static <T> P1<T> equalTo(final T expected) {
    return new P1<T>() {
      @Override
      public boolean test(final T value) {
        return value.equals(expected);
      }
    };
  }

  private static final class MatchingKeyValuePredicate<T> implements ScratchpadPredicate {
    private final P1<T> predicate;
    private final Key<T> key;

    private MatchingKeyValuePredicate(final P1<T> predicate, final Key<T> key) {
      this.predicate = predicate;
      this.key = key;
    }

    @Override
    public boolean test(final Scratchpad scratchpad) {
      return predicate.test(scratchpad.get(key));
    }
  }
}
