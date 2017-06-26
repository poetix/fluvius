package com.codepoetics.fluvius.describers;

import com.codepoetics.fluvius.api.description.DescriptionWriter;

import java.util.Collection;
import java.util.Stack;
import java.util.UUID;

/**
 * A DescriptionWriter that pretty-prints the description of a Flow.
 */
public final class PrettyPrintingDescriptionWriter implements DescriptionWriter {

  public static PrettyPrintingDescriptionWriter create() {
    return new PrettyPrintingDescriptionWriter();
  }

  private final StringBuilder builder = new StringBuilder();
  private int indentLevel = 0;
  private final Stack<String> sequencePrefix = new Stack<>();

  private PrettyPrintingDescriptionWriter() {
  }

  @Override
  public DescriptionWriter writeSingleFlow(UUID stepId, Collection<String> requiredKeyNames, String providedKeyName, String name) {
    return append(name).append(" ").stateRequirements(requiredKeyNames, providedKeyName);
  }

  @Override
  public DescriptionWriter writeStartSequence(UUID stepId, Collection<String> requiredKeyNames, String providedKeyName) {
    return append("Sequence ")
        .stateRequirements(requiredKeyNames, providedKeyName)
        .append(":")
        .indent();
  }

  @Override
  public DescriptionWriter writeStartSequenceItem(String sequenceIndex) {
    return newline()
        .appendSequencePrefix()
        .append(sequenceIndex)
        .append(": ")
        .pushSequencePrefix(sequenceIndex);
  }

  @Override
  public DescriptionWriter writeEndSequenceItem() {
    return popSequencePrefix();
  }

  @Override
  public DescriptionWriter writeEndSequence() {
    return outdent();
  }

  @Override
  public DescriptionWriter writeStartBranch(UUID stepId, Collection<String> requiredKeyNames, String providedKeyName) {
    return append("Branch ")
        .stateRequirements(requiredKeyNames, providedKeyName)
        .append(":");
        //.indent();
  }

  @Override
  public DescriptionWriter writeStartBranchOption(char branchIndex, String conditionDescription) {
    return writeBranchLabel(branchIndex, "If " + conditionDescription);
  }

  private PrettyPrintingDescriptionWriter writeBranchLabel(char branchIndex, String conditionDescription) {
    return newline()
        .appendBranchPrefix()
        .append(branchIndex)
        .append(") ")
        .append(conditionDescription)
        .append(": ")
        .pushBranchIndex(branchIndex);
  }

  @Override
  public DescriptionWriter writeStartDefaultBranch(char branchIndex) {
    return writeBranchLabel(branchIndex, "Otherwise");
  }

  @Override
  public DescriptionWriter writeEndBranchOption() {
    return popSequencePrefix();
  }

  @Override
  public DescriptionWriter writeEndBranch() {
    return this; // outdent();
  }

  private PrettyPrintingDescriptionWriter appendBranchPrefix() {
    if (!sequencePrefix.isEmpty()) {
      return append(sequencePrefix.peek());
    }
    return this;
  }

  private PrettyPrintingDescriptionWriter appendSequencePrefix() {
    if (!sequencePrefix.isEmpty()) {
      return append(sequencePrefix.peek()).append(".");
    }
    return this;
  }

  private PrettyPrintingDescriptionWriter append(Object value) {
    builder.append(value);
    return this;
  }

  private PrettyPrintingDescriptionWriter appendList(Collection<?> values) {
    append("[");
    boolean isFirst = true;
    for (Object value : values) {
      if (isFirst) {
        isFirst = false;
      } else {
        append(",");
      }
      append(value);
    }
    return append("]");
  }

  private PrettyPrintingDescriptionWriter newline() {
    append("\n");
    for (int i = 0; i < indentLevel; i++) {
      append("\t");
    }
    return this;
  }

  private PrettyPrintingDescriptionWriter indent() {
    indentLevel += 1;
    return this;
  }

  private PrettyPrintingDescriptionWriter outdent() {
    indentLevel -= 1;
    return this;
  }

  private PrettyPrintingDescriptionWriter stateRequirements(Collection<String> requiredKeyNames, String providedKeyName) {
    return append("(requires ")
        .appendList(requiredKeyNames)
        .append(", provides ")
        .append(providedKeyName).append(")");
  }

  private PrettyPrintingDescriptionWriter pushSequencePrefix(String sequenceIndex) {
    sequencePrefix.push(sequencePrefix.isEmpty()
        ? sequenceIndex
        : sequencePrefix.peek() + "." + sequenceIndex);
    return this;
  }

  private PrettyPrintingDescriptionWriter pushBranchIndex(char branchIndex) {
    sequencePrefix.push(sequencePrefix.isEmpty()
        ? Character.toString(branchIndex)
        : sequencePrefix.peek() + branchIndex);
    return this;
  }

  private PrettyPrintingDescriptionWriter popSequencePrefix() {
    sequencePrefix.pop();
    return this;
  }

  @Override
  public String toString() {
    return builder.toString();
  }
}
