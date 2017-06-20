package com.codepoetics.fluvius.flows;

import com.codepoetics.fluvius.api.scratchpad.Key;

/**
 * Part of the fluent API for defining flows that recover from failures.
 */
public final class FailureKeyCapture {
  private final Key<?> failureKey;

  FailureKeyCapture(Key<?> failureKey) {
    this.failureKey = failureKey;
  }

  public <T> RecoveryResultKeyCapture<T> to(Key<T> recoveryResultKey) {
    return new RecoveryResultKeyCapture<>(failureKey, recoveryResultKey);
  }
}
