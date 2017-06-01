package com.codepoetics.fluvius.describers;

import com.codepoetics.fluvius.api.DescribableFlow;
import com.codepoetics.fluvius.api.FlowDescriber;
import com.codepoetics.fluvius.api.Key;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PrettyPrintingFlowDescriber implements FlowDescriber<PrettyPrintingFlowDescriber> {

    public static PrettyPrintingFlowDescriber create() {
        return new PrettyPrintingFlowDescriber(new StringBuilder(), 0, "");
    }

    private final StringBuilder builder;
    private final int indentLevel;
    private final String sequencePrefix;

    private PrettyPrintingFlowDescriber(StringBuilder builder, int indentLevel, String sequencePrefix) {
        this.builder = builder;
        this.indentLevel = indentLevel;
        this.sequencePrefix = sequencePrefix;
    }

    public String getDescription() {
        return builder.toString();
    }

    private PrettyPrintingFlowDescriber append(Object value) {
        builder.append(value);
        return this;
    }

    private PrettyPrintingFlowDescriber appendList(Collection<?> values) {
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

    private PrettyPrintingFlowDescriber newline() {
        append("\n");
        for (int i = 0; i<indentLevel; i++) {
            append("\t");
        }
        return this;
    }

    private PrettyPrintingFlowDescriber label(String label) {
        return newline().append(label).append(": ");
    }
    private PrettyPrintingFlowDescriber sequenceLabel(int sequenceIndex) {
        newline().append(sequencePrefix);
        if (!sequencePrefix.isEmpty()) {
            append(".");
        }
        return append(sequenceIndex).append(": ");
    }
    private PrettyPrintingFlowDescriber branchLabel(char branchIndex, String description) {
        return newline().append(sequencePrefix).append(branchIndex).append(") ").append(description).append(": ");
    }

    private PrettyPrintingFlowDescriber indent() {
        return new PrettyPrintingFlowDescriber(builder, indentLevel + 1, sequencePrefix);
    }

    private PrettyPrintingFlowDescriber withPrefix(int sequenceIndex) {
        String newPrefix = sequencePrefix.isEmpty()
                ? Integer.toString(sequenceIndex)
                : sequencePrefix + "." + sequenceIndex;
        return new PrettyPrintingFlowDescriber(builder, indentLevel, newPrefix);
    }

    private PrettyPrintingFlowDescriber withPrefix(char branchPrefix) {
        String newPrefix = sequencePrefix.isEmpty()
                ? Character.toString(branchPrefix)
                : sequencePrefix + branchPrefix;
        return new PrettyPrintingFlowDescriber(builder, indentLevel, newPrefix);
    }

    private PrettyPrintingFlowDescriber stateRequirements(Set<Key<?>> requiredKeys, Key<?> providedKey) {
        return append(" (requires ").appendList(requiredKeys).append(", provides ").append(providedKey).append(")");
    }

    @Override
    public PrettyPrintingFlowDescriber describeSingle(String name, Set<Key<?>> requiredKeys, Key<?> providedKey) {
        return append(name).stateRequirements(requiredKeys, providedKey);
    }

    @Override
    public PrettyPrintingFlowDescriber describeSequence(List<? extends DescribableFlow> flows, Set<Key<?>> requiredKeys, Key<?> providedKey) {
        PrettyPrintingFlowDescriber inSequence = append("Sequence").stateRequirements(requiredKeys, providedKey).append(":").indent();
        int sequenceIndex = 1;
        for (DescribableFlow flow : flows) {
            inSequence.sequenceLabel(sequenceIndex);
            flow.describe(inSequence.withPrefix(sequenceIndex));
            sequenceIndex++;
        }
        return this;
    }

    @Override
    public PrettyPrintingFlowDescriber describeBranch(DescribableFlow defaultFlow, Map<String, DescribableFlow> describableBranches, Set<Key<?>> requiredKeys, Key<?> providedKey) {
        PrettyPrintingFlowDescriber inBranch = append("Branch").stateRequirements(requiredKeys, providedKey).append(":").indent();
        char branchLabel = 'a';
        for (Map.Entry<String, DescribableFlow> entry : describableBranches.entrySet()) {
            inBranch.branchLabel(branchLabel, "If " + entry.getKey());
            entry.getValue().describe(inBranch.withPrefix(branchLabel));
            branchLabel++;
        }
        inBranch.branchLabel(branchLabel, "Otherwise");
        defaultFlow.describe(inBranch.withPrefix(branchLabel));
        return this;
    }
}
