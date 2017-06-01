package com.codepoetics.fluvius.api.description;

import java.util.List;

public interface DescriptionWriter {
    DescriptionWriter writeSingleFlow(String name, List<String> requiredKeyNames, String providedKeyName);
    DescriptionWriter writeStartSequence(List<String> requiredKeyNames, String providedKeyName);
    DescriptionWriter writeStartSequenceItem(int sequenceIndex);
    DescriptionWriter writeEndSequenceItem();
    DescriptionWriter writeEndSequence();
    DescriptionWriter writeStartBranch(List<String> requiredKeyNames, String providedKeyName);
    DescriptionWriter writeStartBranchOption(char branchIndex, String conditionDescription);
    DescriptionWriter writeStartDefaultBranch(char branchIndex);
    DescriptionWriter writeEndBranchOption();
    DescriptionWriter writeEndBranch();
    DescriptionWriter writeDescription(FlowDescription description);
}
