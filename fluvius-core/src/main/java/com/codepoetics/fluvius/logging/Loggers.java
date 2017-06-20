package com.codepoetics.fluvius.logging;

import com.codepoetics.fluvius.api.logging.FlowLogger;
import com.codepoetics.fluvius.api.scratchpad.Key;
import com.codepoetics.fluvius.api.scratchpad.Scratchpad;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

/**
 * Utility class providing a FlowLogger which writes messages to the console.
 */
public final class Loggers {

  private Loggers() {
  }

  private static final FlowLogger CONSOLE = new ConsoleLogger();

  public static FlowLogger getConsoleLogger() {
    return CONSOLE;
  }

  private static final class ConsoleLogger implements FlowLogger {

    private void write(UUID flowId, String message, Object... params) {
      System.out.println(getTime() + "/" + flowId + " " + String.format(message, params));
    }

    private String getTime() {
      Calendar cal = Calendar.getInstance();
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
      return sdf.format(cal.getTime());
    }

    @Override
    public void logOperationStarted(UUID flowId, String name, Scratchpad scratchpad) {
      write(flowId, "Operation '%s' started with scratchpad %s", name, scratchpad);
    }

    @Override
    public void logOperationCompleted(UUID flowId, String name, Key<?> outputKey, Object output) {
      write(flowId, "Operation '%s' completed, writing value %s to key %s", name, output, outputKey.getName());
    }

    @Override
    public void logOperationException(UUID flowId, String name, Throwable exception) {
      write(flowId, "Operation '%s' failed with exception %s", name, exception);
      exception.printStackTrace();
    }

    @Override
    public void logConditionStarted(UUID flowId, String description, Scratchpad scratchpad) {
      write(flowId, "Condition '%s' started with scratchpad %s", description, scratchpad);
    }

    @Override
    public void logConditionCompleted(UUID flowId, String description, boolean result) {
      write(flowId, "Condition '%s' completed with result %s", description, result);
    }

    @Override
    public void logConditionException(UUID flowId, String description, Throwable exception) {
      write(flowId, "Condition '%s' failed with exception %s", description, exception);
      exception.printStackTrace();
    }
  }
}
