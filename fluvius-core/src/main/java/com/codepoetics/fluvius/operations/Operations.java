package com.codepoetics.fluvius.operations;

import com.codepoetics.fluvius.api.Operation;
import com.codepoetics.fluvius.api.functional.ScratchpadFunction;
import com.codepoetics.fluvius.api.scratchpad.Scratchpad;

/**
 * Utility class for working with Operations.
 */
public final class Operations {

  private Operations() {
  }

  /**
   * Construct an Operation from the supplied name and ScratchpadFunction.
   *
   * @param name     The name to give the Operation.
   * @param function The ScratchpadFunction executed by the Operation.
   * @param <T>      The type of the value returned by the Operation.
   * @return The constructed Operation.
   */
  public static <T> Operation<T> fromFunction(final String name, final ScratchpadFunction<T> function) {
    return new FunctionOperation<>(name, function);
  }

  private static final class FunctionOperation<T> implements Operation<T> {
    private final String name;
    private final ScratchpadFunction<T> function;

    private FunctionOperation(final String name, final ScratchpadFunction<T> function) {
      this.name = name;
      this.function = function;
    }

    @Override
    public String getName() {
      return name;
    }

    @Override
    public T run(final Scratchpad scratchpad) throws Exception {
      return function.apply(scratchpad);
    }
  }
}
