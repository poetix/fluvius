package com.codepoetics.fluvius.flows;

import com.codepoetics.fluvius.api.scratchpad.Key;

/**
 * Created by dominicfox on 19/06/2017.
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
