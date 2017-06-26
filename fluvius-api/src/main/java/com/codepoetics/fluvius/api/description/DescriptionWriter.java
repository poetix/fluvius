package com.codepoetics.fluvius.api.description;

import java.util.Collection;
import java.util.UUID;

/**
 * An object to which a FlowDescription can give a detailed description of a Flow.
 */
public interface DescriptionWriter {
  DescriptionWriter writeSingleFlow(UUID stepId, Collection<String> requiredKeyNames, String providedKeyName, String name);

  DescriptionWriter writeStartSequence(UUID stepId, Collection<String> requiredKeyNames, String providedKeyName);

  DescriptionWriter writeStartSequenceItem(String sequenceLabel);

  DescriptionWriter writeEndSequenceItem();

  DescriptionWriter writeEndSequence();

  DescriptionWriter writeStartBranch(UUID stepId, Collection<String> requiredKeyNames, String providedKeyName);

  DescriptionWriter writeStartBranchOption(char branchIndex, String conditionDescription);

  DescriptionWriter writeStartDefaultBranch(char branchIndex);

  DescriptionWriter writeEndBranchOption();

  DescriptionWriter writeEndBranch();
}
