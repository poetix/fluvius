package com.codepoetics.fluvius.flows;

import com.codepoetics.fluvius.api.Condition;
import com.codepoetics.fluvius.api.Flow;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Provides a Fluent API for constructing Branching flows, deferring specification of the default branch to the final step.
 *
 * @param <T> The type of value returned by the Flow constructed by this BranchBuilder.
 */
public final class BranchBuilder<T> {

  static <T> BranchBuilder<T> startingWith(Condition condition, Flow<T> ifTrue) {
    Map<Condition, Flow<T>> branches = new LinkedHashMap<>();
    branches.put(condition, ifTrue);
    return new BranchBuilder<>(branches);
  }

  private final Map<Condition, Flow<T>> branches;

  private BranchBuilder(Map<Condition, Flow<T>> branches) {
    this.branches = branches;
  }

  /**
   * Adds a conditional branch to the BranchBuilder, using the supplied Condition and Flow to execute if the Condition is met.
   *
   * @param condition The Condition to test for this branch.
   * @param ifTrue    The Flow to execute if the Condition it met.
   * @return This BranchBuilder, ready to accept further branches.
   */
  public BranchBuilder<T> orIf(Condition condition, Flow<T> ifTrue) {
    branches.put(condition, ifTrue);
    return this;
  }

  /**
   * Completes the construction of the branching Flow by specifying the default Flow to execute if none of its conditions are met.
   *
   * @param defaultFlow The Flow to execute if none of the branch conditions are met.
   * @return The constructed Flow.
   */
  public Flow<T> otherwise(Flow<T> defaultFlow) {
    Flow<T> result = defaultFlow;
    for (Map.Entry<Condition, Flow<T>> branchEntry : branches.entrySet()) {
      result = result.orIf(branchEntry.getKey(), branchEntry.getValue());
    }
    return result;
  }
}
