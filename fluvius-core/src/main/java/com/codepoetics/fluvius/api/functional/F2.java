package com.codepoetics.fluvius.api.functional;

import java.io.Serializable;

/**
 * A function taking two arguments.
 *
 * @param <A>      The type of the function's first argument.
 * @param <B>      The type of the function's second argument.
 * @param <OUTPUT> The type of the function's result.
 */
public interface F2<A, B, OUTPUT> extends Serializable {
  /**
   * Apply this function to the supplied inputs.
   *
   * @param first  The first input to the function.
   * @param second The second input to the function.
   * @return The output of the function.
   */
  OUTPUT apply(A first, B second);
}