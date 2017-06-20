package com.codepoetics.fluvius.api.functional;

import java.io.Serializable;

/**
 * A function taking a single argument.
 *
 * @param <A>      The type of the function's argument.
 * @param <OUTPUT> The type of the function's result.
 */
public interface F1<A, OUTPUT> extends Serializable {
  /**
   * Apply this function to the supplied input.
   *
   * @param input The input to the function.
   * @return The output of the function.\
   * @throws Exception Any exception thrown during execution of the function.
   */
  OUTPUT apply(A input) throws Exception;
}
