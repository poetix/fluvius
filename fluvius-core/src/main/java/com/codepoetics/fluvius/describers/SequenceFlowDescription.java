package com.codepoetics.fluvius.describers;

import com.codepoetics.fluvius.api.description.DescriptionWriter;
import com.codepoetics.fluvius.api.description.FlowDescription;

import java.util.List;

final class SequenceFlowDescription implements FlowDescription {

  private final List<String> requiredKeyNames;
  private final String providedKeyName;
  private final List<FlowDescription> itemDescriptions;

  SequenceFlowDescription(final List<String> requiredKeyNames, final String providedKeyName, final List<FlowDescription> itemDescriptions) {
    this.requiredKeyNames = requiredKeyNames;
    this.providedKeyName = providedKeyName;
    this.itemDescriptions = itemDescriptions;
  }

  @Override
  public void writeTo(final DescriptionWriter descriptionWriter) {
    descriptionWriter.writeStartSequence(requiredKeyNames, providedKeyName);
    int sequenceIndex = 1;
    for (final FlowDescription description : itemDescriptions) {
      descriptionWriter
          .writeStartSequenceItem(sequenceIndex++)
          .writeDescription(description)
          .writeEndSequenceItem();
    }
    descriptionWriter.writeEndSequence();
  }
}
