package com.codepoetics.fluvius.json.history;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@JsonSerialize
public abstract class FlowEventView {

  private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");

  private final UUID stepId;
  private final long timestamp;

  protected FlowEventView(UUID stepId, long timestamp) {
    this.stepId = stepId;
    this.timestamp = timestamp;
  }

  @JsonProperty
  public String getStepId() {
    return stepId.toString();
  }

  @JsonProperty
  public String getTimestamp() {
    Date date = new Date();
    date.setTime(timestamp);
    return DATE_FORMAT.format(date);
  }

  @JsonProperty
  public abstract String getType();
}
