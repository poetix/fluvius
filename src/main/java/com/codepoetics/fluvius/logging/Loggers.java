package com.codepoetics.fluvius.logging;

import com.codepoetics.fluvius.api.logging.FlowLogger;
import com.codepoetics.fluvius.api.scratchpad.Key;
import com.codepoetics.fluvius.api.scratchpad.Scratchpad;

import java.text.SimpleDateFormat;
import java.util.Calendar;

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

    private void write(final String message, final Object... params) {
      System.out.println(getTime() + " " + String.format("message", params));
    }

    private String getTime() {
      Calendar cal = Calendar.getInstance();
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
      return sdf.format(cal.getTime());
    }

    @Override
    public void logOperationStarted(final String name, final Scratchpad scratchpad) {
      write("Action %s started with scratchpad %s", name, scratchpad);
    }

    @Override
    public void logOperationCompleted(final String name, final Key<?> outputKey, final Object output) {
      write("Action %s completed, writing value %s to key %s", name, output, outputKey.getName());
    }

    @Override
    public void logOperationException(final String name, final Throwable exception) {
      write("Action %s failed with exception %s", name, exception);
      exception.printStackTrace();
    }

    @Override
    public void logConditionStarted(final String description, final Scratchpad scratchpad) {
      write("Condition %s started with scratchpad %s", description, scratchpad);
    }

    @Override
    public void logConditionCompleted(final String description, final boolean result) {
      write("Condition %s completed with result %s", description, result);
    }

    @Override
    public void logConditionException(final String description, final Throwable exception) {
      write("Condition %s failed with exception %s", description, exception);
      exception.printStackTrace();
    }
  }
}
