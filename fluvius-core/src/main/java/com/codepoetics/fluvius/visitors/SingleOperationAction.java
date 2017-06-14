package com.codepoetics.fluvius.visitors;

import com.codepoetics.fluvius.api.Action;
import com.codepoetics.fluvius.api.Operation;
import com.codepoetics.fluvius.api.scratchpad.Key;
import com.codepoetics.fluvius.api.scratchpad.Scratchpad;

import java.util.UUID;

final class SingleOperationAction<T> implements Action {

  static <T> Action of(final Key<T> outputKey, final Operation<T> operation) {
    return new SingleOperationAction<>(outputKey, operation);
  }

  private final Key<T> outputKey;
  private final Operation<T> operation;

  private SingleOperationAction(final Key<T> outputKey, final Operation<T> operation) {
    this.outputKey = outputKey;
    this.operation = operation;
  }

  @Override
  public Scratchpad run(final UUID flowId, final Scratchpad scratchpad) {
    return scratchpad.with(outputKey.of(operation.run(scratchpad)));
  }
}
