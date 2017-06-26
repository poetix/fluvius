package com.codepoetics.fluvius.api.tracing;

import java.util.*;

/**
 * A map of the possible execution paths of a flow.
 */
public final class TraceMap {

  public static TraceMap ofStep(UUID stepId, Set<String> requiredKeys, String providedKey, String description) {
    return new TraceMap(stepId, requiredKeys, providedKey, description, FlowStepType.STEP, Collections.<TraceMapLabel, TraceMap>emptyMap());
  }

  public static TraceMap ofSequence(UUID stepId, Set<String> requiredKeys, String providedKey, List<TraceMap> steps) {
    Map<TraceMapLabel, TraceMap> children = new LinkedHashMap<>();
    int stepIndex = 1;
    for (TraceMap step : steps) {
      children.put(TraceMapLabel.forSequence(stepIndex++), step);
    }
    return new TraceMap(stepId, requiredKeys, providedKey, "Sequence", FlowStepType.SEQUENCE, children);
  }

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

  public UUID getStepId() {
    return stepId;
  }

  public Set<String> getRequiredKeys() {
    return requiredKeys;
  }

  public String getProvidedKey() {
    return providedKey;
  }

  public String getDescription() {
    return description;
  }

  public FlowStepType getType() {
    return type;
  }

  public Map<TraceMapLabel, TraceMap> getChildren() {
    return children;
  }

  @Override
  public boolean equals(Object o) {
    return this == o
        || (o instanceof TraceMap && equals((TraceMap) o));
  }

  private boolean equals(TraceMap o) {
    return o.stepId.equals(stepId)
        && o.requiredKeys.equals(requiredKeys)
        && o.providedKey.equals(providedKey)
        && o.description.equals(description)
        && o.type.equals(type)
        && o.children.equals(children);
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