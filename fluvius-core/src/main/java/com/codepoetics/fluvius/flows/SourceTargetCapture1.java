package com.codepoetics.fluvius.flows;

import com.codepoetics.fluvius.api.Flow;
import com.codepoetics.fluvius.api.functional.SingleParameterStep;
import com.codepoetics.fluvius.api.scratchpad.Key;

/**
 * The stage in the fluent API where we have captured the target key and single source key of a Flow.
 *
 * @param <A>      The type of the source key.
 * @param <OUTPUT> The type of the target key.
 */
public final class SourceTargetCapture1<A, OUTPUT> {
  private final Key<A> sourceA;
  private final Key<OUTPUT> target;

  SourceTargetCapture1(Key<A> sourceA, Key<OUTPUT> target) {
    this.sourceA = sourceA;
    this.target = target;
  }

  /**
   * Create a Flow from the source key to the target key, using the given name and function.
   *
   * @param name The name of the Flow.
   * @param singleParameterStep   The function to use to transform the source value to the target value.
   * @return The constructed Flow.
   */
  public Flow<OUTPUT> using(String name, SingleParameterStep<A, OUTPUT> singleParameterStep) {
    return Fluent.inputKeysCapture(sourceA).to(target).using(
        name,
        Extractors.make(sourceA, singleParameterStep));
  }

  /**
   * Create a Flow from the source key to the target key, using the given function.
   * The flow is automatically named based on the key names.
   *
   * @param singleParameterStep The function to use to transform the source value to the target value.
   * @return The constructed Flow.
   */
  public Flow<OUTPUT> using(SingleParameterStep<A, OUTPUT> singleParameterStep) {
    return using("Obtain " + target.getName()
            + " from " + sourceA.getName(),
        singleParameterStep);
  }
}
