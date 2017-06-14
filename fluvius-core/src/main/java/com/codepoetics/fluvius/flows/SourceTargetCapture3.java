package com.codepoetics.fluvius.flows;

import com.codepoetics.fluvius.api.Flow;
import com.codepoetics.fluvius.api.functional.F3;
import com.codepoetics.fluvius.api.scratchpad.Key;

/**
 * The stage in the fluent API where we have captured the target key and three source keys of a Flow.
 *
 * @param <A>      The type of the first source key.
 * @param <B>      The type of the second source key.
 * @param <C>      The type of the third source key.
 * @param <OUTPUT> The type of the target key.
 */
public final class SourceTargetCapture3<A, B, C, OUTPUT> {
  private final Key<A> sourceA;
  private final Key<B> sourceB;
  private final Key<C> sourceC;
  private final Key<OUTPUT> target;

  SourceTargetCapture3(final Key<A> sourceA, final Key<B> sourceB, final Key<C> sourceC, final Key<OUTPUT> target) {
    this.sourceA = sourceA;
    this.sourceB = sourceB;
    this.sourceC = sourceC;
    this.target = target;
  }

  /**
   * Create a Flow from the source keys to the target key, using the given name and function.
   *
   * @param name The name of the Flow.
   * @param f3   The function to use to transform the source values to the target value.
   * @return The constructed Flow.
   */
  public Flow<OUTPUT> using(final String name, final F3<A, B, C, OUTPUT> f3) {
    return Fluent.inputKeysCapture(sourceA, sourceB, sourceC).to(target).using(
        name,
        Extractors.make(sourceA, sourceB, sourceC, f3));
  }

  /**
   * Create a Flow from the source keys to the target key, using the given function.
   * The flow is automatically named based on the key names.
   *
   * @param f3 The function to use to transform the source values to the target value.
   * @return The constructed Flow.
   */
  public Flow<OUTPUT> using(final F3<A, B, C, OUTPUT> f3) {
    return using("Obtain " + target.getName()
            + " from " + sourceA.getName()
            + ", " + sourceB.getName()
            + " and " + sourceC.getName(),
        f3);
  }
}
