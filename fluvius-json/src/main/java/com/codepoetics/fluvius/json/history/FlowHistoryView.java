package com.codepoetics.fluvius.json.history;

import com.codepoetics.fluvius.api.history.FlowEvent;
import com.codepoetics.fluvius.api.history.FlowEventTranslator;
import com.codepoetics.fluvius.api.tracing.TraceMap;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@JsonSerialize
public final class FlowHistoryView {

  private static final FlowEventTranslator<JsonNode, FlowEventView> eventTranslator = FlowEventViewTranslator.create();

  public static FlowHistoryView from(UUID flowId, TraceMap traceMap, List<FlowEvent<JsonNode>> flowEvents) {

    return new FlowHistoryView(
        flowId.toString(),
        TraceMapView.from(traceMap),
        translateEvents(flowEvents));
  }

  private static List<FlowEventView> translateEvents(List<FlowEvent<JsonNode>> flowEvents) {
    List<FlowEventView> eventViews = new ArrayList<>(flowEvents.size());

    for (FlowEvent<JsonNode> event : flowEvents) {
      eventViews.add(event.translate(eventTranslator));
    }
    return eventViews;
  }

  private final String flowId;
  private final TraceMapView traceMapView;
  private final List<FlowEventView> eventHistory;

  private FlowHistoryView(String flowId, TraceMapView traceMapView, List<FlowEventView> eventHistory) {
    this.flowId = flowId;
    this.traceMapView = traceMapView;
    this.eventHistory = eventHistory;
  }

  @JsonProperty
  public String getFlowId() {
    return flowId;
  }

  @JsonProperty
  public TraceMapView getTraceMap() {
    return traceMapView;
  }

  @JsonProperty
  public List<FlowEventView> getEventHistory() {
    return eventHistory;
  }
}
