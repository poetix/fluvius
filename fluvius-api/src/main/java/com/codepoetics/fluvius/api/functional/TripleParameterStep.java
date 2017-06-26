package com.codepoetics.fluvius.api.functional;

import java.io.Serializable;

/**
 * A function taking three arguments.
 *
 * @param <A>      The type of the function's first argument.
 * @param <B>      The type of the function's second argument.
 * @param <C>      The type of the function's third argument.
 * @param <OUTPUT> The type of the function's result.
 */
public interface TripleParameterStep<A, B, C, OUTPUT> extends Serializable {
  /**
   * Apply this function to the supplied inputs.
   *
   * @param first  The first input to the function.
   * @param second The second input to the function.
   * @param third  The third input to the function.
   * @return The output of the function.
   * @throws Exception Any exception thrown during the execution of the function.
   */
  OUTPUT apply(A first, B second, C third) throws Exception;
}
