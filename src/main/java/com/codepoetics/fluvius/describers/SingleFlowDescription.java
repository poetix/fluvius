package com.codepoetics.fluvius.describers;

import com.codepoetics.fluvius.api.description.DescriptionWriter;
import com.codepoetics.fluvius.api.description.FlowDescription;

import java.util.List;

final class SingleFlowDescription implements FlowDescription {

  private final String name;
  private final List<String> requiredKeyNames;
  private final String providedKeyName;

  SingleFlowDescription(final String name, final List<String> requiredKeyNames, final String providedKeyName) {
    this.name = name;
    this.requiredKeyNames = requiredKeyNames;
    this.providedKeyName = providedKeyName;
  }

  @Override
  public void writeTo(final DescriptionWriter descriptionWriter) {
    descriptionWriter.writeSingleFlow(requiredKeyNames, providedKeyName, name);
  }
}
