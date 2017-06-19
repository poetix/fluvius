package com.codepoetics.fluvius.json.history;

import com.codepoetics.fluvius.api.history.EventDataSerialiser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

public final class JsonEventDataSerialiser implements EventDataSerialiser<JsonNode> {

  public static EventDataSerialiser<JsonNode> using(ObjectMapper mapper) {
    return new JsonEventDataSerialiser(mapper);
  }

  private final ObjectMapper mapper;

  private JsonEventDataSerialiser(ObjectMapper mapper) {
    this.mapper = mapper;
  }

  @Override
  public JsonNode serialise(Object value) {
    return mapper.valueToTree(value);
  }

  @Override
  public JsonNode serialiseException(Exception exception) {
    return mapper.valueToTree(exception);
  }
}
