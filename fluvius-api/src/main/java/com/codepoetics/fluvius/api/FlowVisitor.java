package com.codepoetics.fluvius.api;

import com.codepoetics.fluvius.api.scratchpad.Key;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Visits a Flow and all of its sub-flows to construct a value, such as an Action or FlowDescription, which represents something we might want to do with the Flow, e.g. execute or describe it.
 *
 * @param <V> The type of the value constructed by this FlowVisitor.
 */
public interface FlowVisitor<V> {

  /**
   * Called when this FlowVisitor visits a Flow which executes a single Operation.
   *
   * @param stepId              The unique step ID of the visited Flow.
   * @param requiredKeys The Keys required by the visited Flow.
   * @param providedKey  The Key provided by the visited Flow.
   * @param operation    The Operation executed by the visited Flow.
   * @param <T>          The type of the value returned by the Operation.
   * @return The constructed value.
   */
  <T> V visitSingle(UUID stepId, Set<Key<?>> requiredKeys, Key<T> providedKey, Operation<T> operation);

  /**
   * Called when this FlowVisitor visits a Flow which sequences two or more sub-flows.
   *
   * @param stepId              The unique step ID of the visited Flow.
   * @param requiredKeys The Keys required by the visited Flow.
   * @param providedKey  The Key provided by the visited Flow.
   * @param items        The values constructed by visiting all of the sub-flows of this Flow with this FlowVisitor.
   * @param <T>          The type of the value returned by the visited Flow.
   * @return The constructed value.
   */
  <T> V visitSequence(UUID stepId, Set<Key<?>> requiredKeys, Key<T> providedKey, List<V> items);

  /**
   * Called when this FlowVisitor visits a Flow which branches between two or more conditional sub-flows.
   *
   * @param stepId              The unique step ID of the visited Flow.
   * @param requiredKeys        The Keys required by the visited Flow.
   * @param providedKey         The Key provided by the visited Flow.
   * @param defaultBranch       The value constructed by visiting the default branch with this FlowVisitor.
   * @param conditionalBranches The values constructed by visiting the conditional branches with this FlowVisitor.
   * @param <T>                 The type of the value returned by the visited Flow.
   * @return The constructed value.
   */
  <T> V visitBranch(UUID stepId, Set<Key<?>> requiredKeys, Key<T> providedKey, V defaultBranch, List<Conditional<V>> conditionalBranches);

  /**
   * Called when this FlowVisitor visits a Condition. This may be used to decorate a Condition, e.g. with logging behaviour.
   *
   * @param condition The visited Condition.
   * @return The decorated Condition.
   */
  Condition visitCondition(Condition condition);

}
