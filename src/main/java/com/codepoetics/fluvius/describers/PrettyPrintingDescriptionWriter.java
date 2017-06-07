package com.codepoetics.fluvius.describers;

import com.codepoetics.fluvius.api.description.DescriptionWriter;
import com.codepoetics.fluvius.api.description.FlowDescription;

import java.util.Collection;
import java.util.List;
import java.util.Stack;

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
  public DescriptionWriter writeSingleFlow(final List<String> requiredKeyNames, final String providedKeyName, final String name) {
    return append(name).append(" ").stateRequirements(requiredKeyNames, providedKeyName);
  }

  @Override
  public DescriptionWriter writeStartSequence(final List<String> requiredKeyNames, final String providedKeyName) {
    return append("Sequence ")
        .stateRequirements(requiredKeyNames, providedKeyName)
        .append(":")
        .indent();
  }

  @Override
  public DescriptionWriter writeStartSequenceItem(final int sequenceIndex) {
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
  public DescriptionWriter writeStartBranch(final List<String> requiredKeyNames, final String providedKeyName) {
    return append("Branch ")
        .stateRequirements(requiredKeyNames, providedKeyName)
        .append(":")
        .indent();
  }

  @Override
  public DescriptionWriter writeStartBranchOption(final char branchIndex, final String conditionDescription) {
    return writeBranchLabel(branchIndex, "If " + conditionDescription);
  }

  private PrettyPrintingDescriptionWriter writeBranchLabel(final char branchIndex, final String conditionDescription) {
    return newline()
        .appendBranchPrefix()
        .append(branchIndex)
        .append(") ")
        .append(conditionDescription)
        .append(": ")
        .pushBranchIndex(branchIndex);
  }

  @Override
  public DescriptionWriter writeStartDefaultBranch(final char branchIndex) {
    return writeBranchLabel(branchIndex, "Otherwise");
  }

  @Override
  public DescriptionWriter writeEndBranchOption() {
    return popSequencePrefix();
  }

  @Override
  public DescriptionWriter writeEndBranch() {
    return outdent();
  }

  @Override
  public DescriptionWriter writeDescription(final FlowDescription description) {
    description.writeTo(this);
    return this;
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

  private PrettyPrintingDescriptionWriter append(final Object value) {
    builder.append(value);
    return this;
  }

  private PrettyPrintingDescriptionWriter appendList(final Collection<?> values) {
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

  private PrettyPrintingDescriptionWriter stateRequirements(final List<String> requiredKeyNames, final String providedKeyName) {
    return append("(requires ")
        .appendList(requiredKeyNames)
        .append(", provides ")
        .append(providedKeyName).append(")");
  }

  private PrettyPrintingDescriptionWriter pushSequencePrefix(final int sequenceIndex) {
    sequencePrefix.push(sequencePrefix.isEmpty()
        ? Integer.toString(sequenceIndex)
        : sequencePrefix.peek() + "." + sequenceIndex);
    return this;
  }

  private PrettyPrintingDescriptionWriter pushBranchIndex(final char branchIndex) {
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
