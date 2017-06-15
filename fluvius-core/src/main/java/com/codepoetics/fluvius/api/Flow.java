package com.codepoetics.fluvius.api;

import com.codepoetics.fluvius.api.scratchpad.Key;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * A Flow which takes values from a Scratchpad, and provides a new value to be written into the Scratchpad.
 *
 * @param <T> The type of value returned by the flow.
 */
public interface Flow<T> extends Serializable {

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
   * Flatten this flow into a list of flows.
   * @return A list of flows containing either this flow, or (if this flow is a sequence) all of the flows in this flow.
   */
  List<Flow<?>> getAllFlows();
}
