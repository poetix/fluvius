package com.codepoetics.fluvius.api.functional;

import java.io.Serializable;

/**
 * A function taking two arguments.
 *
 * @param <A>      The type of the function's first argument.
 * @param <B>      The type of the function's second argument.
 * @param <OUTPUT> The type of the function's result.
 */
public interface DoubleParameterStep<A, B, OUTPUT> extends Returning<OUTPUT>, Serializable {
  /**
   * Apply this function to the supplied inputs.
   *
   * @param first  The first input to the function.
   * @param second The second input to the function.
   * @return The output of the function.
   * @throws Exception Any exception thrown during execution of the function.
   */
  OUTPUT apply(A first, B second) throws Exception;
}
