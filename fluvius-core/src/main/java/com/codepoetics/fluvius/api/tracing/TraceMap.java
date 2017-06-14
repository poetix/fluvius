package com.codepoetics.fluvius.api.tracing;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * A map of the trace of a flow.
 */
public interface TraceMap {

  /**
   * Get the id of the mapped flow step.
   * @return The id of the mapped flow step.
   */
  UUID getId();

  /**
   * Get the keys required by the mapped flow step.
   * @return The keys required by the mapped flow step.
   */
  Set<String> getRequiredKeys();

  /**
   * Get the key provided by the mapped flow step.
   * @return The key provided by the mapped flow step.
   */
  String getProvidedKey();

  /**
   * Get a textual description of the mapped flow step.
   * @return A textual description of the mapped flow step.
   */
  String getDescription();

  /**
   * Get the type of the mapped flow step.
   * @return The type of the mapped flow step.
   */
  FlowStepType getType();

  /**
   * Get the children of the mapped flow step.
   * @return The children of the mapped flow step.
   */
  List<TraceMap> getChildren();

}
