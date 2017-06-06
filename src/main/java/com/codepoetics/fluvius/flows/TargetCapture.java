package com.codepoetics.fluvius.flows;

import com.codepoetics.fluvius.api.scratchpad.Key;

/**
 * The stage in the fluent API where we have captured the output Key to which a Flow will write its result.
 *
 * @param <OUTPUT> The type of the output key.
 */
public final class TargetCapture<OUTPUT> {
  private final Key<OUTPUT> target;

  TargetCapture(final Key<OUTPUT> target) {
    this.target = target;
  }

  /**
   * Capture the single sourceA Key from which the Flow will take its input.
   *
   * @param sourceA The sourceA Key from which the Flow will take its input.
   * @param <A>     The type of the sourceA Key.
   * @return The next stage in the Fluent API.
   */
  public <A> SourceTargetCapture1<A, OUTPUT> from(final Key<A> sourceA) {
    return new SourceTargetCapture1<>(sourceA, target);
  }

  /**
   * Capture the two sourceA Keys from which the Flow will take its input.
   *
   * @param sourceA The first sourceA Key from which the Flow will take its input.
   * @param sourceB The second sourceA Key from which the Flow will take its input.
   * @param <A>     The type of the first sourceA Key.
   * @param <B>     The type of the second sourceA Key.
   * @return The next stage in the Fluent API.
   */
  public <A, B> SourceTargetCapture2<A, B, OUTPUT> from(final Key<A> sourceA, final Key<B> sourceB) {
    return new SourceTargetCapture2<>(sourceA, sourceB, target);
  }

  /**
   * Capture the three sourceA Keys from which the Flow will take its input.
   *
   * @param source1 The first sourceA Key from which the flow will take its input.
   * @param source2 The second sourceA Key from which the flow will take its input.
   * @param source3 The third sourceA Key from which the flow will take its input.
   * @param <A>     The type of the first sourceA Key.
   * @param <B>     The type of the second sourceA Key.
   * @param <C>     The type of the third sourceA Key.
   * @return The next stage in the Fluent API.
   */
  public <A, B, C> SourceTargetCapture3<A, B, C, OUTPUT> from(final Key<A> source1, final Key<B> source2, final Key<C> source3) {
    return new SourceTargetCapture3<>(source1, source2, source3, target);
  }
}
