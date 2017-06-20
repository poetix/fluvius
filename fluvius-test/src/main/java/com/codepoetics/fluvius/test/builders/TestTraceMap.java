package com.codepoetics.fluvius.test.builders;

import com.codepoetics.fluvius.api.tracing.FlowStepType;
import com.codepoetics.fluvius.api.tracing.TraceMap;

import java.util.*;

public final class TestTraceMap implements TraceMap {

  public static TestTraceMap ofType(FlowStepType type) {
    return new TestTraceMap(type);
  }

  private final FlowStepType type;
  private UUID stepId = UUID.randomUUID();
  private Set<String> requiredKeys;
  private String providedKey;
  private String description;
  private List<TraceMap> children;

  private TestTraceMap(FlowStepType type) {
    this.type = type;
  }

  public TestTraceMap withId(UUID id) {
    this.stepId = id;
    return this;
  }

  @Override
  public UUID getStepId() {
    return stepId;
  }

  public TestTraceMap withRequiredKeys(String...requiredKeys) {
    return withRequiredKeys(new HashSet<>(Arrays.asList(requiredKeys)));
  }

  public TestTraceMap withRequiredKeys(Set<String> requiredKeys) {
    this.requiredKeys = requiredKeys;
    return this;
  }

  @Override
  public Set<String> getRequiredKeys() {
    return requiredKeys;
  }

  public TestTraceMap withProvidedKey(String providedKey) {
    this.providedKey = providedKey;
    return this;
  }

  @Override
  public String getProvidedKey() {
    return providedKey;
  }

  public TestTraceMap withDescription(String description) {
    this.description = description;
    return this;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public FlowStepType getType() {
    return type;
  }

  public TestTraceMap withChildren(TraceMap...children) {
    return withChildren(Arrays.asList(children));
  }

  public TestTraceMap withChildren(List<TraceMap> children) {
    this.children = children;
    return this;
  }

  @Override
  public List<TraceMap> getChildren() {
    return children;
  }
}
