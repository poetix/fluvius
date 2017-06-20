package com.codepoetics.fluvius.api.functional;

/**
 * A mapping function taking a single argument.
 *
 * @param <A>      The type of the function's argument.
 * @param <OUTPUT> The type of the function's result.
 */
public interface Mapper<A, OUTPUT> {
  /**
   * Apply this function to the supplied input.
   *
   * @param input The input to the function.
   * @return The output of the function.
   */
  OUTPUT apply(A input);
}
