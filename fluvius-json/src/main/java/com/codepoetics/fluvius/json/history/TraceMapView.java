package com.codepoetics.fluvius.json.history;

import com.codepoetics.fluvius.api.tracing.TraceMap;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@JsonSerialize
public final class TraceMapView {

  public static TraceMapView from(TraceMap traceMap) {
    return new TraceMapView(
        traceMap.getStepId().toString(),
        traceMap.getDescription(),
        traceMap.getType().name(),
        traceMap.getRequiredKeys(),
        traceMap.getProvidedKey(),
        toViews(traceMap.getChildren())
    );
  }

  private static List<TraceMapView> toViews(List<TraceMap> children) {
    List<TraceMapView> result = new ArrayList<>(children.size());
    for (TraceMap traceMap : children) {
      result.add(from(traceMap));
    }
    return result;
  }

  private final String stepId;
  private final String description;
  private final String type;
  private final Set<String> requiredKeys;
  private final String providedKey;
  private final List<TraceMapView> children;

  public TraceMapView(String stepId, String description, String type, Set<String> requiredKeys, String providedKey, List<TraceMapView> children) {
    this.stepId = stepId;
    this.description = description;
    this.type = type;
    this.requiredKeys = requiredKeys;
    this.providedKey = providedKey;
    this.children = children;
  }

  @JsonProperty
  public String getStepId() {
    return stepId;
  }

  @JsonProperty
  public String getType() {
    return type;
  }

  @JsonProperty
  public String getDescription() {
    return description;
  }

  @JsonProperty
  public Set<String> getRequiredKeys() {
    return requiredKeys;
  }

  @JsonProperty
  public String getProvidedKey() {
    return providedKey;
  }

  @JsonProperty
  public List<TraceMapView> getChildren() {
    return children;
  }
}
