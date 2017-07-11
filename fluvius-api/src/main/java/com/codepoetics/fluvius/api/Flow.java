package com.codepoetics.fluvius.api;

import com.codepoetics.fluvius.api.functional.Predicate;
import com.codepoetics.fluvius.api.scratchpad.Key;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * A Flow which takes values from a Scratchpad, and provides a new value to be written into the Scratchpad.
 *
 * @param <T> The type of value returned by the flow.
 */
public interface Flow<T> extends Serializable {

  /**
   * Get the unique identifier of this flow.
   * @return The unique identifier of this flow.
   */
  UUID getStepId();

  /**
   * Get the required Keys that this Flow expects to find in the Scratchpad.
   *
   * @return The required Keys that this Flow expects to find in the Scratchpad.
   */
  Set<Key<?>> getRequiredKeys();

  /**
   * Get the Key that this Flow will write into the Scratchpad.
   *
   * @return The key that this Flow will write into the Scratchpad.
   */
  Key<T> getProvidedKey();

  /**
   * Convert this Flow into a value of some kind (e.g. an Action) by visiting it (and all of its sub-flows) with the supplied FlowVisitor.
   *
   * @param visitor The FlowVisitor to visit this Flow (and all of its sub-flows) with.
   * @param <V>     The type of the value constructed by the FlowVisitor.
   * @return The value constructed by the FlowVisitor.
   */
  <V> V visit(FlowVisitor<V> visitor);

  /**
   * Combine this Flow with a subsequent Flow to create a sequence of Flows.
   *
   * @param next The Flow to run after this Flow.
   * @param <N>  The type of value returned by the next Flow in the sequence (and hence the type of the new sequence of Flows).
   * @return The constructed Flow.
   */
  <N> Flow<N> then(Flow<N> next);

  /**
   * Create a branching Flow, with this Flow as the default branch and the supplied Condition and Flow as the first conditional branch.
   * If this Flow is already a branching Flow, then the supplied Condition and Flow are used to add another conditional branch to it.
   *
   * @param condition The Condition to test to determine whether to execute the supplied Flow.
   * @param ifTrue    The Flow to execute if the Condition is met.
   * @return The constructed Flow.
   */
  Flow<T> orIf(Condition condition, Flow<T> ifTrue);

  /**
   * Start creating a branching flow which branches on the result of this flow's execution.
   *
   * @return The next step in the fluent API for creating a flow which branches on the result of this flow's execution.
   */
  BranchOnResultFirstCondition<T> branchOnResult();

  /**
   * The first step in a fluent API for creating a flow which branches on the result of the previous flow's execution.
   * @param <T> The type of the previous flow's result.
   */
  interface BranchOnResultFirstCondition<T> {

    /**
     * Specify the flow to execute if the previous flow returned a failure result.
     *
     * @param failureFlow The flow to execute if the previous flow returned a failure result.
     * @param <V> The type returned by the branching flow.
     * @return The next step in the fluent API.
     */
    <V> BranchOnResultSubsequentConditions<T, V> onFailure(Flow<V> failureFlow);

    /**
     * Specify the flow to execute if the result of the previous flow matches the provided {@link Predicate}.
     *
     * @param description A description of the condition.
     * @param predicate A predicate which tests whether the condition has been met.
     * @param flow The flow to execute if the condition is met.
     * @param <V> The type returned by the branching flow.
     * @return The next step in the fluent API.
     */
    <V> BranchOnResultSubsequentConditions<T, V> onCondition(String description, Predicate<T> predicate, Flow<V> flow);
  }

  /**
   * Specify subsequent conditions, and finally the default execution, in a branching flow which branches on the previous flow's result.
   *
   * @param <T> The type of the previous flow's result.
   * @param <V> The type of the branching flow's result.
   */
  interface BranchOnResultSubsequentConditions<T, V> {

    /**
     * Specify the flow to execute if the result of the previous flow matches the provided {@link Predicate}.
     *
     * @param description A description of the condition.
     * @param predicate A predicate which tests whether the condition has been met.
     * @param flow The flow to execute if the condition is met.
     * @return The next step in the fluent API.
     */
    BranchOnResultSubsequentConditions<T, V> onCondition(String description, Predicate<T> predicate, Flow<V> flow);

    /**
     * Specify the flow to execute if no other condition is met, and exit the fluent API returning the constructed {@link Flow}.
     *
     * @param defaultFlow The flow to execute if no other condition is met.
     * @return The constructed branching {@link Flow}.
     */
    Flow<V> otherwise(Flow<V> defaultFlow);
  }

  /**
   * Append this flow (or, if this flow is a sequence, all of the flows sequenced by this flow) to the provided list of flows.
   *
   * @param previousFlows The list of flows to append flows to.
   * @return The extended list of flows;
   */
  List<Flow<?>> appendTo(List<Flow<?>> previousFlows);

}
