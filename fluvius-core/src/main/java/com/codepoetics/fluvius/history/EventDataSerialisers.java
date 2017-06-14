package com.codepoetics.fluvius.history;

import com.codepoetics.fluvius.api.history.EventDataSerialiser;

public class EventDataSerialisers {

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
    public Object serialise(final Object value) {
      return value;
    }

    @Override
    public Object serialiseThrowable(final Throwable throwable) {
      return throwable;
    }
  }

  private static final class ToStringSerialiser implements EventDataSerialiser<String> {
    @Override
    public String serialise(final Object value) {
      return value.toString();
    }

    @Override
    public String serialiseThrowable(final Throwable throwable) {
      return throwable.getMessage();
    }
  }
}
