package com.codepoetics.fluvius.history;

import com.codepoetics.fluvius.api.history.EventDataSerialiser;

public final class EventDataSerialisers {

  private EventDataSerialisers() {
  }

  private static final EventDataSerialiser<Object> toObject = new ToObjectSerialiser();
  private static final EventDataSerialiser<String> toString = new ToStringSerialiser();

  public static EventDataSerialiser<Object> toObjectSerialiser() {
    return toObject;
  }

  public static EventDataSerialiser<String> toStringSerialiser() {
    return toString;
  }

  private static final class ToObjectSerialiser implements EventDataSerialiser<Object> {
    @Override
    public Object serialise(Object value) {
      return value;
    }

    @Override
    public Object serialiseException(Exception exception) {
      return exception;
    }
  }

  private static final class ToStringSerialiser implements EventDataSerialiser<String> {
    @Override
    public String serialise(Object value) {
      return value.toString();
    }

    @Override
    public String serialiseException(Exception exception) {
      return exception.getMessage();
    }
  }
}
