package com.codepoetics.fluvius.describers;

import com.codepoetics.fluvius.api.description.DescriptionWriter;
import com.codepoetics.fluvius.api.description.FlowDescription;

import java.util.Collection;
import java.util.List;
import java.util.Stack;

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
    public DescriptionWriter writeSingleFlow(String name, List<String> requiredKeyNames, String providedKeyName) {
        return append(name).stateRequirements(requiredKeyNames, providedKeyName);
    }

    @Override
    public DescriptionWriter writeStartSequence(List<String> requiredKeyNames, String providedKeyName) {
        return append("Sequence ").stateRequirements(requiredKeyNames, providedKeyName).append(":").indent();
    }

    @Override
    public DescriptionWriter writeStartSequenceItem(int sequenceIndex) {
        newline();
        if (!sequencePrefix.isEmpty()) {
            append(sequencePrefix.peek()).append(".");
        }

        append(sequenceIndex)
            .append(": ");

        String newPrefix = sequencePrefix.isEmpty()
                ? Integer.toString(sequenceIndex)
                : sequencePrefix.peek() + "." + sequenceIndex;

        sequencePrefix.push(newPrefix);

        return this;
    }

    @Override
    public DescriptionWriter writeEndSequenceItem() {
        sequencePrefix.pop();
        return this;
    }

    @Override
    public DescriptionWriter writeEndSequence() {
        return outdent();
    }

    @Override
    public DescriptionWriter writeStartBranch(List<String> requiredKeyNames, String providedKeyName) {
        return append("Branch ").stateRequirements(requiredKeyNames, providedKeyName).append(":").indent();
    }

    @Override
    public DescriptionWriter writeStartBranchOption(char branchIndex, String conditionDescription) {
        return writeBranchLabel(branchIndex, "If " + conditionDescription);
    }

    private DescriptionWriter writeBranchLabel(char branchIndex, String conditionDescription) {
        newline();

        if (!sequencePrefix.isEmpty()) {
            append(sequencePrefix.peek());
        }

        append(branchIndex)
            .append(") ")
            .append(conditionDescription)
            .append(": ");

        String newPrefix = sequencePrefix.isEmpty()
                ? Character.toString(branchIndex)
                : sequencePrefix.peek() + branchIndex;

        sequencePrefix.push(newPrefix);
        return this;
    }

    @Override
    public DescriptionWriter writeStartDefaultBranch(char branchIndex) {
        return writeBranchLabel(branchIndex, "Otherwise");
    }

    @Override
    public DescriptionWriter writeEndBranchOption() {
        sequencePrefix.pop();
        return this;
    }

    @Override
    public DescriptionWriter writeEndBranch() {
        return outdent();
    }

    @Override
    public DescriptionWriter writeDescription(FlowDescription description) {
        description.writeTo(this);
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

    private PrettyPrintingDescriptionWriter stateRequirements(List<String> requiredKeyNames, String providedKeyName) {
        return append("(requires ").appendList(requiredKeyNames).append(", provides ").append(providedKeyName).append(")");
    }

    @Override
    public String toString() {
        return builder.toString();
    }
}
