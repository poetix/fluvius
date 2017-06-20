package com.codepoetics.fluvius.flows;

import com.codepoetics.fluvius.api.Flow;
import com.codepoetics.fluvius.api.Operation;
import com.codepoetics.fluvius.api.functional.ScratchpadFunction;
import com.codepoetics.fluvius.api.scratchpad.Key;
import com.codepoetics.fluvius.operations.Operations;

import java.util.Set;

/**
 * The stage in the fluent API where we have captured the input and output keys of a Flow.
 *
 * @param <T> The type of the value returned by the Flow.
 */
public final class OutputKeysCapture<T> {
  private final Set<Key<?>> inputKeys;
  private final Key<T> outputKey;

  OutputKeysCapture(Set<Key<?>> inputKeys, Key<T> outputKey) {
    this.inputKeys = inputKeys;
    this.outputKey = outputKey;
  }

  /**
   * Create a Flow which processes the captured input keys using the given Operation, and writes the result into the capture output key.
   *
   * @param operation The Operation to use to obtain the output value.
   * @return The constructed Flow.
   */
  public Flow<T> using(Operation<T> operation) {
    return SingleOperationFlow.create(inputKeys, outputKey, operation);
  }

  /**
   * Create a Flow which processes the captured input keys using the given ScratchpadFunction, and writes the result into the capture output key.
   *
   * @param name     The name of the operation.
   * @param function The ScratchpadFunction to use to obtain the output value.
   * @return The constructed Flow.
   */
  public Flow<T> using(String name, ScratchpadFunction<T> function) {
    return using(Operations.fromFunction(name, function));
  }
}
