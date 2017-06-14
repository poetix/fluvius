package com.codepoetics.fluvius.api.description;

import java.util.List;

/**
 * An object to which a FlowDescription can give a detailed description of a Flow.
 */
public interface DescriptionWriter {
  DescriptionWriter writeSingleFlow(List<String> requiredKeyNames, String providedKeyName, String name);

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
