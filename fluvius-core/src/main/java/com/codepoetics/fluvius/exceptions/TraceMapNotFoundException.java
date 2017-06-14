package com.codepoetics.fluvius.exceptions;

import java.util.UUID;

public class TraceMapNotFoundException extends RuntimeException {

  public static TraceMapNotFoundException forFlow(final UUID flowId) {
    return new TraceMapNotFoundException(String.format("Trace map not found for flow %s", flowId));
  }

  private TraceMapNotFoundException(final String message) {
    super(message);
  }
}
