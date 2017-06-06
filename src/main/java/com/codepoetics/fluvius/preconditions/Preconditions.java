package com.codepoetics.fluvius.preconditions;

/**
 * Utility class for checking arguments, especially to enforce non-nullity.
 */
public final class Preconditions {

  private Preconditions() {
  }

  /**
   * Throws an informative exception if the supplied value is null
   *
   * @param name  The name of the variable being tested.
   * @param value The value of the variable being tested.
   * @param <T>   The type of the variable being tested.
   * @return The (guaranteed non-null) value of the variable being tested.
   */
  public static <T> T checkNotNull(final String name, final T value) {
    if (name == null) {
      throw new NullPointerException("name must not be null");
    }
    if (value == null) {
      throw new NullPointerException(name + " must not be null");
    }
    return value;
  }
}
