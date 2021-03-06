package com.codepoetics.fluvius.flows;

import com.codepoetics.fluvius.api.Flow;
import com.codepoetics.fluvius.api.functional.DoubleParameterStep;
import com.codepoetics.fluvius.api.scratchpad.Key;

/**
 * The stage in the fluent API where we have captured the target key and two source keys of a Flow.
 *
 * @param <A>      The type of the first source key.
 * @param <B>      The type of the second source key.
 * @param <OUTPUT> The type of the target key.
 */
public final class SourceTargetCapture2<A, B, OUTPUT> {
  private final Key<A> sourceA;
  private final Key<B> sourceB;
  private final Key<OUTPUT> target;

  SourceTargetCapture2(Key<A> sourceA, Key<B> sourceB, Key<OUTPUT> target) {
    this.sourceA = sourceA;
    this.sourceB = sourceB;
    this.target = target;
  }

  /**
   * Create a Flow from the source keys to the target key, using the given name and function.
   *
   * @param name The name of the Flow.
   * @param doubleParameterStep   The function to use to transform the source values to the target value.
   * @return The constructed Flow.
   */
  public Flow<OUTPUT> using(String name, DoubleParameterStep<A, B, OUTPUT> doubleParameterStep) {
    return Fluent.inputKeysCapture(sourceA, sourceB).to(target).using(
        name,
        Extractors.make(sourceA, sourceB, doubleParameterStep));
  }

  /**
   * Create a Flow from the source keys to the target key, using the given function.
   * The flow is automatically named based on the key names.
   *
   * @param doubleParameterStep The function to use to transform the source values to the target value.
   * @return The constructed Flow.
   */
  public Flow<OUTPUT> using(DoubleParameterStep<A, B, OUTPUT> doubleParameterStep) {
    return using("Obtain " + target.getName()
            + " from " + sourceA.getName()
            + " and " + sourceB.getName(),
        doubleParameterStep);
  }
}
