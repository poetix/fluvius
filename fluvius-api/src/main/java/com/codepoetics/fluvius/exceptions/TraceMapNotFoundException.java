package com.codepoetics.fluvius.exceptions;

import java.util.UUID;

public final class TraceMapNotFoundException extends RuntimeException {

  public static TraceMapNotFoundException forFlow(UUID flowId) {
    return new TraceMapNotFoundException(String.format("Trace map not found for flow %s", flowId));
  }

  private TraceMapNotFoundException(String message) {
    super(message);
  }
}
