package com.codepoetics.fluvius.json.history;

import com.codepoetics.fluvius.api.history.FlowHistory;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;

@JsonSerialize
public final class FlowHistoryView {

  public static FlowHistoryView from(FlowHistory<JsonNode> history) {
    return new FlowHistoryView(
        history.getFlowId().toString(),
        TraceMapView.from(history.getTraceMap()),
        history.getTranslatedEventHistory(FlowEventViewTranslator.create()));
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
