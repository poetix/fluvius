package com.codepoetics.fluvius.api.logging;

import com.codepoetics.fluvius.api.scratchpad.Key;
import com.codepoetics.fluvius.api.scratchpad.Scratchpad;

/**
 * Logs events that may occur during the execution of a Flow.
 */
public interface FlowLogger {
  /**
   * Log that the execution of an Operation has started.
   *
   * @param name       The name of the Operation.
   * @param scratchpad The Scratchpad supplied to the Operation.
   */
  void logOperationStarted(String name, Scratchpad scratchpad);

  /**
   * Log that an Operation has successfully completed.
   *
   * @param name      The name of the Operation.
   * @param outputKey The key to which the Operation's result will be written in the Scratchpad.
   * @param output    The output of the Operation.
   */
  void logOperationCompleted(String name, Key<?> outputKey, Object output);

  /**
   * Log that execution of an Operation has failed with an exception.
   *
   * @param name      The name of the Operation.
   * @param exception The exception that was thrown.
   */
  void logOperationException(String name, Throwable exception);

  /**
   * Log that the execution of  Condition has started.
   *
   * @param description The description of the Condition to be executed.
   * @param scratchpad  The Scratchpad supplied to the Condition.
   */
  void logConditionStarted(String description, Scratchpad scratchpad);

  /**
   * Log that a Condition has successfully completed.
   *
   * @param description The description of the Condition that has completed.
   * @param result      The result of the Condition.
   */
  void logConditionCompleted(String description, boolean result);

  /**
   * Log that execution of a Condition has failed with an exception.
   *
   * @param description The description of the Condition that failed.
   * @param exception   The exception that was thrown.
   */
  void logConditionException(String description, Throwable exception);
}
