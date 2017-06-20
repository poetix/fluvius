package com.codepoetics.fluvius.describers;

import com.codepoetics.fluvius.api.description.DescriptionWriter;
import com.codepoetics.fluvius.api.description.FlowDescription;

import java.util.List;
import java.util.UUID;

final class SingleFlowDescription implements FlowDescription {

  private final UUID stepId;
  private final String name;
  private final List<String> requiredKeyNames;
  private final String providedKeyName;

  SingleFlowDescription(UUID stepId, String name, List<String> requiredKeyNames, String providedKeyName) {
    this.stepId = stepId;
    this.name = name;
    this.requiredKeyNames = requiredKeyNames;
    this.providedKeyName = providedKeyName;
  }

  @Override
  public void writeTo(DescriptionWriter descriptionWriter) {
    descriptionWriter.writeSingleFlow(stepId, requiredKeyNames, providedKeyName, name);
  }
}
