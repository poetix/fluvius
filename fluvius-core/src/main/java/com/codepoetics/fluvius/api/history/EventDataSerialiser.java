package com.codepoetics.fluvius.api.history;

public interface EventDataSerialiser<T> {
  T serialise(Object value);
  T serialiseThrowable(Throwable throwable);
}
