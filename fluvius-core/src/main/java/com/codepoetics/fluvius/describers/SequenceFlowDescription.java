package com.codepoetics.fluvius.describers;

import com.codepoetics.fluvius.api.description.DescriptionWriter;
import com.codepoetics.fluvius.api.description.FlowDescription;

import java.util.List;
import java.util.UUID;

final class SequenceFlowDescription implements FlowDescription {

  private final UUID stepId;
  private final List<String> requiredKeyNames;
  private final String providedKeyName;
  private final List<FlowDescription> itemDescriptions;

  SequenceFlowDescription(UUID stepId, List<String> requiredKeyNames, String providedKeyName, List<FlowDescription> itemDescriptions) {
    this.stepId = stepId;
    this.requiredKeyNames = requiredKeyNames;
    this.providedKeyName = providedKeyName;
    this.itemDescriptions = itemDescriptions;
  }

  @Override
  public void writeTo(DescriptionWriter descriptionWriter) {
    descriptionWriter.writeStartSequence(stepId, requiredKeyNames, providedKeyName);
    int sequenceIndex = 1;
    for (FlowDescription description : itemDescriptions) {
      descriptionWriter
          .writeStartSequenceItem(sequenceIndex++)
          .writeDescription(description)
          .writeEndSequenceItem();
    }
    descriptionWriter.writeEndSequence();
  }
}
