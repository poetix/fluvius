package com.codepoetics.fluvius.flows;

import com.codepoetics.fluvius.api.Flow;
import com.codepoetics.fluvius.api.functional.RecoveryStep;
import com.codepoetics.fluvius.api.functional.ScratchpadFunction;
import com.codepoetics.fluvius.api.scratchpad.Key;
import com.codepoetics.fluvius.api.scratchpad.Scratchpad;
import com.codepoetics.fluvius.operations.Operations;

import java.util.Collections;

/**
 * Part of the fluent API for creating {@link Flow}s that recover from a failure.
 */
public final class RecoveryResultKeyCapture<T> {
  private final Key<?> failureKey;
  private final Key<T> recoveryResultKey;

  RecoveryResultKeyCapture(Key<?> failureKey, Key<T> recoveryResultKey) {
    this.failureKey = failureKey;
    this.recoveryResultKey = recoveryResultKey;
  }

  /**
   * Specify the recovery function to use to recover from a failure written to the captured {@link Key}.
   * @param description Description of the recovery operation.
   * @param recoveryStep The recovery function to use.
   * @return The constructed flow.
   */
  public Flow<T> using(String description, final RecoveryStep<T> recoveryStep) {
    return SingleOperationFlow.create(Collections.<Key<?>>singleton(failureKey), recoveryResultKey, Operations.fromFunction(description, new ScratchpadFunction<T>() {
      @Override
      public T apply(Scratchpad input) throws Exception {
        return recoveryStep.apply(input.getFailureReason(failureKey));
      }
    }));
  }
}
