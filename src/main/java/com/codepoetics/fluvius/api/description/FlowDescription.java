package com.codepoetics.fluvius.api.description;

/**
 * A description of a Flow, that knows how to write itself using a DescriptionWriter.
 */
public interface FlowDescription {
  /**
   * Write this FlowDescription to the supplied DescriptionWriter.
   *
   * @param descriptionWriter The DescriptionWriter that will receive this FlowDescription's description of a Flow.
   */
  void writeTo(DescriptionWriter descriptionWriter);
}
