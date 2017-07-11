package com.codepoetics.fluvius.api.tracing;

import com.codepoetics.fluvius.api.Flow;

import java.util.*;

/**
 * A map of the possible execution paths of a flow.
 */
public final class TraceMap {

  /**
   * Create the TraceMap for a single step.
   * @param stepId The unique ID of the step.
   * @param requiredKeys The keys required by the step.
   * @param providedKey The key provided by the step.
   * @param description A description of the step.
   * @return The constructed TraceMap.
   */
  public static TraceMap ofStep(UUID stepId, Set<String> requiredKeys, String providedKey, String description) {
    return new TraceMap(stepId, requiredKeys, providedKey, description, FlowStepType.STEP, Collections.<TraceMapLabel, TraceMap>emptyMap());
  }

  /**
   * Create the TraceMap for a sequence of steps.
   * @param stepId The unique ID of the sequence.
   * @param requiredKeys The keys required by the sequence.
   * @param providedKey The key provided by the sequence.
   * @param steps The TraceMaps of all of the steps in the sequence.
   * @return The constructed TraceMap.
   */
  public static TraceMap ofSequence(UUID stepId, Set<String> requiredKeys, String providedKey, List<TraceMap> steps) {
    Map<TraceMapLabel, TraceMap> children = new LinkedHashMap<>();
    int stepIndex = 1;
    for (TraceMap step : steps) {
      children.put(TraceMapLabel.forSequenceMember(stepIndex++), step);
    }
    return new TraceMap(stepId, requiredKeys, providedKey, "Sequence", FlowStepType.SEQUENCE, children);
  }

  /**
   * Create the TraceMap for a branching flow.
   * @param stepId The unique ID of the branching flow.
   * @param requiredKeys The keys required by the branching flow.
   * @param providedKey The key provided by every branch in the flow.
   * @param defaultTraceMap The TraceMap of the default branch.
   * @param conditionalTraceMaps The TraceMaps of each of the conditional branches.
   * @return The constructed TraceMap.
   */
  public static TraceMap ofBranch(UUID stepId, Set<String> requiredKeys, String providedKey, TraceMap defaultTraceMap, Map<String, TraceMap> conditionalTraceMaps) {
    Map<TraceMapLabel, TraceMap> children = new LinkedHashMap<>();
    for (Map.Entry<String, TraceMap> entry : conditionalTraceMaps.entrySet()) {
      children.put(TraceMapLabel.forConditionalBranch(entry.getKey()), entry.getValue());
    }
    children.put(TraceMapLabel.forDefaultBranch(), defaultTraceMap);
    return new TraceMap(stepId, requiredKeys, providedKey, "Branch", FlowStepType.BRANCH, children);
  }

  private final UUID stepId;
  private final Set<String> requiredKeys;
  private final String providedKey;
  private final String description;
  private final FlowStepType type;
  private final Map<TraceMapLabel, TraceMap> children;

  private TraceMap(UUID stepId, Set<String> requiredKeys, String providedKey, String description, FlowStepType type, Map<TraceMapLabel, TraceMap> children) {
    this.stepId = stepId;
    this.requiredKeys = requiredKeys;
    this.providedKey = providedKey;
    this.description = description;
    this.type = type;
    this.children = children;
  }

  /**
   * Get the unique ID of the {@link com.codepoetics.fluvius.api.Flow} for which this is the TraceMap.
   * @return The unique ID of the {@link com.codepoetics.fluvius.api.Flow} for which this is the TraceMap.
   */
  public UUID getStepId() {
    return stepId;
  }

  /**
   * Get the names of the required keys of the {@link com.codepoetics.fluvius.api.Flow} for which this is the TraceMap.
   * @return The names of the required keys of the {@link com.codepoetics.fluvius.api.Flow} for which this is the TraceMap.
   */
  public Set<String> getRequiredKeys() {
    return requiredKeys;
  }

  /**
   * Get the name of the key provided by the {@link Flow} for which this is the TraceMap.
   * @return The name of the key provided by the {@link Flow} for which this is the TraceMap.
   */
  public String getProvidedKey() {
    return providedKey;
  }

  /**
   * Get the description of the {@link Flow} for which this is the TraceMap.
   * @return The description of the {@link Flow} for which this is the TraceMap.
   */
  public String getDescription() {
    return description;
  }

  /**
   * Get the type of the {@link Flow} for which this is the TraceMap.
   * @return The type of the {@link Flow} for which this is the TraceMap.
   */
  public FlowStepType getType() {
    return type;
  }

  /**
   * Get the children, if any, of the {@link Flow} for which this is the TraceMap.
   * @return The children, if any, of the {@link Flow} for which this is the TraceMap.
   */
  public Map<TraceMapLabel, TraceMap> getChildren() {
    return children;
  }

  @Override
  public boolean equals(Object other) {
    return this == other
        || (other instanceof TraceMap && equals((TraceMap) other));
  }

  private boolean equals(TraceMap other) {
    return other.stepId.equals(stepId)
        && other.requiredKeys.equals(requiredKeys)
        && other.providedKey.equals(providedKey)
        && other.description.equals(description)
        && other.type.equals(type)
        && other.children.equals(children);
  }

  @Override
  public int hashCode() {
    return Objects.hash(stepId, requiredKeys, providedKey, description, type, children);
  }

  @Override
  public String toString() {
    return String.format("%s: %s (requires %s, provides %s) | %s", stepId, description, requiredKeys, providedKey, children);
  }
}