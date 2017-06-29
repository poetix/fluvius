package com.codepoetics.fluvius.tracing;

import com.codepoetics.fluvius.api.Flow;
import com.codepoetics.fluvius.api.description.DescriptionWriter;
import com.codepoetics.fluvius.api.tracing.TraceMap;
import com.codepoetics.fluvius.api.tracing.TraceMapLabel;
import com.codepoetics.fluvius.api.tracing.TraceMapLabelType;

import java.util.Map;

/**
 * Utility class for obtaining and describing the {@link com.codepoetics.fluvius.api.tracing.TraceMap} of a {@link Flow}
 */
public final class TraceMaps {

  private static final TraceMapFlowVisitor visitor = new TraceMapFlowVisitor();

  private TraceMaps() {
  }

  /**
   * Generate the {@link TraceMap} for the provided {@link Flow}
   *
   * @param flow The flow to obtain the trace map for.
   * @return The generated trace map.
   */
  public static TraceMap getTraceMap(Flow<?> flow) {
    return flow.visit(visitor);
  }

  /**
   * Describe the provided {@link TraceMap}, using the provided {@link DescriptionWriter}
   *
   * @param traceMap The trace map to describe.
   * @param descriptionWriter The description writer to use to write the description.
   */
  public static void describe(TraceMap traceMap, DescriptionWriter descriptionWriter) {
    switch (traceMap.getType()) {
      case STEP:
        describeSingle(traceMap, descriptionWriter);
        return;
      case SEQUENCE:
        describeSequence(traceMap, descriptionWriter);
        return;
      default:
        describeBranch(traceMap, descriptionWriter);
    }
  }

  private static void describeSingle(TraceMap traceMap, DescriptionWriter descriptionWriter) {
    descriptionWriter.writeSingleFlow(
        traceMap.getStepId(),
        traceMap.getRequiredKeys(),
        traceMap.getProvidedKey(),
        traceMap.getDescription());
  }

  private static void describeSequence(TraceMap traceMap, DescriptionWriter descriptionWriter) {
    descriptionWriter.writeStartSequence(traceMap.getStepId(), traceMap.getRequiredKeys(), traceMap.getProvidedKey());

    for (Map.Entry<TraceMapLabel, TraceMap> entry : traceMap.getChildren().entrySet()) {
      descriptionWriter.writeStartSequenceItem(entry.getKey().getDescription());
      describe(entry.getValue(), descriptionWriter);
      descriptionWriter.writeEndSequenceItem();
    }

    descriptionWriter.writeEndSequence();
  }

  private static void describeBranch(TraceMap traceMap, DescriptionWriter descriptionWriter) {
    descriptionWriter.writeStartBranch(traceMap.getStepId(), traceMap.getRequiredKeys(), traceMap.getProvidedKey());

    char branchIndex = 'a';
    for (Map.Entry<TraceMapLabel, TraceMap> entry : traceMap.getChildren().entrySet()) {
      if (entry.getKey().getType() == TraceMapLabelType.DEFAULT_BRANCH) {
        descriptionWriter.writeStartDefaultBranch(branchIndex);
      } else {
        descriptionWriter.writeStartBranchOption(branchIndex, entry.getKey().getDescription());
      }
      describe(entry.getValue(), descriptionWriter);
      descriptionWriter.writeEndBranchOption();
      branchIndex++;
    }

    descriptionWriter.writeEndBranch();
  }

}
