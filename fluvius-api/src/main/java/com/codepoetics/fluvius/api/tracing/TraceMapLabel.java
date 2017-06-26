package com.codepoetics.fluvius.api.tracing;

import java.util.Objects;

/**
 * A label for a trace map that is the child of another trace map.
 */
public final class TraceMapLabel {

  private static final TraceMapLabel DEFAULT_BRANCH_LABEL = new TraceMapLabel(TraceMapLabelType.DEFAULT_BRANCH, "Otherwise");

  public static TraceMapLabel forSequence(int sequenceNumber) {
    return new TraceMapLabel(TraceMapLabelType.SEQUENCE_ITEM, Integer.toString(sequenceNumber));
  }

  public static TraceMapLabel forConditionalBranch(String conditionDescription) {
    return new TraceMapLabel(TraceMapLabelType.CONDITIONAL_BRANCH, conditionDescription);
  }

  public static TraceMapLabel forDefaultBranch() {
    return DEFAULT_BRANCH_LABEL;
  }

  private final TraceMapLabelType type;
  private final String description;

  private TraceMapLabel(TraceMapLabelType type, String description) {
    this.type = type;
    this.description = description;
  }

  public TraceMapLabelType getType() {
    return type;
  }

  public String getDescription() {
    return description;
  }

  @Override
  public boolean equals(Object o) {
    return this == o
        || (o instanceof TraceMapLabel
        && ((TraceMapLabel) o).type == type
        && ((TraceMapLabel) o).description.equals(description));
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, description);
  }

  @Override
  public String toString() {
    switch (type) {
      case SEQUENCE_ITEM:
        return "Sequence item: " + description;
      case CONDITIONAL_BRANCH:
        return "Conditional branch: " + description;
      default:
        return "Default branch";
    }
  }

}
