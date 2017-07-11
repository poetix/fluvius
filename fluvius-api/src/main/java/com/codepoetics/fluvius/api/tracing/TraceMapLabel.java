package com.codepoetics.fluvius.api.tracing;

import java.util.Objects;

/**
 * A label for a trace map that is the child of another trace map.
 */
public final class TraceMapLabel {

  private static final TraceMapLabel DEFAULT_BRANCH_LABEL = new TraceMapLabel(TraceMapLabelType.DEFAULT_BRANCH, "Otherwise");

  /**
   * Create a TraceMapLabel for a member of a sequence.
   * @param sequenceNumber The index of the member in the sequence.
   * @return The constructed label.
   */
  public static TraceMapLabel forSequenceMember(int sequenceNumber) {
    return new TraceMapLabel(TraceMapLabelType.SEQUENCE_ITEM, Integer.toString(sequenceNumber));
  }

  /**
   * Create a TraceMapLabel for a conditional branch.
   * @param conditionDescription The description of the condition.
   * @return The constructed label.
   */
  public static TraceMapLabel forConditionalBranch(String conditionDescription) {
    return new TraceMapLabel(TraceMapLabelType.CONDITIONAL_BRANCH, conditionDescription);
  }

  /**
   * Create a TraceMapLabel for the default branch.
   * @return The constructed label.
   */
  public static TraceMapLabel forDefaultBranch() {
    return DEFAULT_BRANCH_LABEL;
  }

  private final TraceMapLabelType type;
  private final String description;

  private TraceMapLabel(TraceMapLabelType type, String description) {
    this.type = type;
    this.description = description;
  }

  /**
   * Get the type of this label.
   * @return The type of this label.
   */
  public TraceMapLabelType getType() {
    return type;
  }

  /**
   * Get the description of this label.
   * @return The description of this label.
   */
  public String getDescription() {
    return description;
  }

  @Override
  public boolean equals(Object other) {
    return this == other
        || (other instanceof TraceMapLabel
        && ((TraceMapLabel) other).type == type
        && ((TraceMapLabel) other).description.equals(description));
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
