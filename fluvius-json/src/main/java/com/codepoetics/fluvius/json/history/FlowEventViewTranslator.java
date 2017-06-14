package com.codepoetics.fluvius.json.history;

import com.codepoetics.fluvius.api.history.FlowEventTranslator;
import com.codepoetics.fluvius.api.history.StepFailedEvent;
import com.codepoetics.fluvius.api.history.StepStartedEvent;
import com.codepoetics.fluvius.api.history.StepSucceededEvent;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Map;
import java.util.UUID;

public final class FlowEventViewTranslator implements FlowEventTranslator<JsonNode, FlowEventView> {

  public static FlowEventViewTranslator create() {
    return new FlowEventViewTranslator();
  }

  @Override
  public FlowEventView translateStepStartedEvent(StepStartedEvent<JsonNode> event) {
    return new StepStartedEventView(event.getStepId(), event.getTimestamp(), event.getScratchpadState());
  }

  @Override
  public FlowEventView translateStepSucceededEvent(StepSucceededEvent<JsonNode> event) {
    return new StepSucceededEventView(event.getStepId(), event.getTimestamp(), event.getResult());
  }

  @Override
  public FlowEventView translateStepFailedEvent(StepFailedEvent<JsonNode> event) {
    return new StepFailedEventView(event.getStepId(), event.getTimestamp(), event.getReason());
  }

  public static final class StepStartedEventView extends FlowEventView {

    private final Map<String, JsonNode> scratchpadState;

    private StepStartedEventView(UUID stepId, long timestamp, Map<String, JsonNode> scratchpadState) {
      super(stepId, timestamp);

      this.scratchpadState = scratchpadState;
    }

    @JsonProperty
    public Map<String, JsonNode> getScratchpadState() {
      return scratchpadState;
    }

    @Override
    public String getType() {
      return "STARTED";
    }
  }

  public static final class StepSucceededEventView extends FlowEventView {

    private final JsonNode result;

    private StepSucceededEventView(UUID stepId, long timestamp, JsonNode result) {
      super(stepId, timestamp);

      this.result = result;
    }

    @JsonProperty
    public JsonNode getResult() {
      return result;
    }

    @Override
    public String getType() {
      return "SUCCEEDED";
    }
  }

  public static final class StepFailedEventView extends FlowEventView {

    private final JsonNode reason;

    private StepFailedEventView(UUID stepId, long timestamp, JsonNode reason) {
      super(stepId, timestamp);

      this.reason = reason;
    }

    @JsonProperty
    public JsonNode getReason() {
      return reason;
    }

    @Override
    public String getType() {
      return "FAILED";
    }
  }

}
