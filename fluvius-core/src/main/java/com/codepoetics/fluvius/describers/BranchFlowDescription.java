package com.codepoetics.fluvius.describers;

import com.codepoetics.fluvius.api.description.DescriptionWriter;
import com.codepoetics.fluvius.api.description.FlowDescription;

import java.util.List;
import java.util.Map;
import java.util.UUID;

final class BranchFlowDescription implements FlowDescription {

  private final UUID stepId;
  private final List<String> requiredKeyNames;
  private final String providedKeyName;
  private final FlowDescription defaultBranchDescription;
  private final Map<String, FlowDescription> branchDescriptions;

  BranchFlowDescription(UUID stepId, final List<String> requiredKeyNames, final String providedKeyName, final FlowDescription defaultBranchDescription, final Map<String, FlowDescription> branchDescriptions) {
    this.stepId = stepId;
    this.requiredKeyNames = requiredKeyNames;
    this.providedKeyName = providedKeyName;
    this.defaultBranchDescription = defaultBranchDescription;
    this.branchDescriptions = branchDescriptions;
  }

  @Override
  public void writeTo(final DescriptionWriter descriptionWriter) {
    descriptionWriter.writeStartBranch(stepId, requiredKeyNames, providedKeyName);
    char branchIndex = 'a';
    for (final Map.Entry<String, FlowDescription> branchEntry : branchDescriptions.entrySet()) {
      descriptionWriter.writeStartBranchOption(branchIndex++, branchEntry.getKey())
          .writeDescription(branchEntry.getValue())
          .writeEndBranchOption();
    }
    descriptionWriter.writeStartDefaultBranch(branchIndex)
        .writeDescription(defaultBranchDescription)
        .writeEndBranchOption()
        .writeEndBranch();
  }
}
