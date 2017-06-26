package com.codepoetics.fluvius.predicates;

import com.codepoetics.fluvius.api.functional.Predicate;
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
   * Construct a ScratchpadPredicate that will be true if the value written for the specified key is a failure reason.
   * @param key The key to test.
   * @return The constructed ScratchpadPredicate.
   */
  public static ScratchpadPredicate isFailure(final Key<?> key) {
    return new ScratchpadPredicate() {
      @Override
      public boolean test(Scratchpad value) {
        return !value.isSuccessful(key);
      }
    };
  }

  /**
   * Construct a ScratchpadPredicate that will be true if the Scratchpad contains the specified key, and its value matches the supplied predicate.
   *
   * @param key       The Key to test.
   * @param predicate The predicate to apply to the key's value.
   * @param <T>       The type of the Key/value.
   * @return The constructed ScratchpadPredicate.
   */
  public static <T> ScratchpadPredicate keyMatches(Key<T> key, Predicate<T> predicate) {
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
  public static <T> ScratchpadPredicate keyEquals(Key<T> key, T expected) {
    return keyMatches(key, equalTo(expected));
  }

  /**
   * Constructs a predicate that will return true if the supplied value is equal to the expected value.
   *
   * @param expected The expected value.
   * @param <T>      The type of the expected value.
   * @return The constructed predicate.
   */
  public static <T> Predicate<T> equalTo(final T expected) {
    return new Predicate<T>() {
      @Override
      public boolean test(T value) {
        return value.equals(expected);
      }
    };
  }

  private static final class MatchingKeyValuePredicate<T> implements ScratchpadPredicate {
    private final Predicate<T> predicate;
    private final Key<T> key;

    private MatchingKeyValuePredicate(Predicate<T> predicate, Key<T> key) {
      this.predicate = predicate;
      this.key = key;
    }

    @Override
    public boolean test(Scratchpad scratchpad) {
      return predicate.test(scratchpad.get(key));
    }
  }
}
