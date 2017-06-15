package com.codepoetics.fluvius.api.services;

public interface ServiceCallResult<T> {
  boolean succeeded();
  T result();
  String failureReason();
}
