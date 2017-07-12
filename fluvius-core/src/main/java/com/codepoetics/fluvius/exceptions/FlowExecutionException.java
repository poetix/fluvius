package com.codepoetics.fluvius.exceptions;

import com.codepoetics.fluvius.api.Flow;

/**
 * An exception thrown during {@link Flow} execution.
 */
public class FlowExecutionException extends RuntimeException {
  public FlowExecutionException(Exception cause) {
    super(cause);
  }
}
