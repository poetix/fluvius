package com.codepoetics.fluvius.visitors;

import com.codepoetics.fluvius.api.*;
import com.codepoetics.fluvius.api.logging.FlowLogger;
import com.codepoetics.fluvius.api.scratchpad.Key;
import com.codepoetics.fluvius.api.scratchpad.Scratchpad;
import com.codepoetics.fluvius.logging.Loggers;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Utility class for working with FlowVisitors.
 */
public final class Visitors {

  private Visitors() {
  }

  /**
   * Obtain the default FlowVisitor, which constructs an Action which can be executed.
   *
   * @return The default FlowVisitor.
   */
  public static FlowVisitor<Action> getDefault() {
    return new DefaultFlowVisitor();
  }

  /**
   * Wrap a FlowVisitor, decorating it with mutation-forbidding behaviour.
   * <p>
   * This will have some performance impact, but may be useful in hunting down obscure bugs due to illicit modification
   * of already-recorded values.
   * </p>
   * @param wrapped The wrapped FlowVisitor.
   * @return The mutation-checking FlowVisitor.
   */
  public static <V> FlowVisitor<V> mutationChecking(final FlowVisitor<V> wrapped) {
    return new MutationCheckingVisitor<>(wrapped);
  }

  /**
   * Wrap a FlowVisitor which constructs an Action which can be executed, decorating it with logging behaviour.
   *
   * @param wrapped The wrapped FlowVisitor.
   * @param logger  The FlowLogger to use to log Flow and Condition execution.
   * @return The logging FlowVisitor.
   */
  public static FlowVisitor<Action> logging(final FlowVisitor<Action> wrapped, final FlowLogger logger) {
    return new LoggingFlowVisitor(wrapped, logger);
  }

  /**
   * Wrap a FlowVisitor which constructs an Action which can be executed, decorating it with logging behaviour that writes messages to the console.
   *
   * @param wrapped The wrapped FlowVisitor.
   * @return The logging FlowVisitor.
   */
  public static FlowVisitor<Action> logging(final FlowVisitor<Action> wrapped) {
    return new LoggingFlowVisitor(wrapped, Loggers.getConsoleLogger());
  }

  private static final class LoggingFlowVisitor implements FlowVisitor<Action> {
    private final FlowVisitor<Action> innerVisitor;
    private final FlowLogger flowLogger;

    private LoggingFlowVisitor(final FlowVisitor<Action> innerVisitor, final FlowLogger flowLogger) {
      this.innerVisitor = innerVisitor;
      this.flowLogger = flowLogger;
    }

    @Override
    public <T> Action visitSingle(final Set<Key<?>> requiredKeys, final Key<T> providedKey, final Operation<T> operation) {
      return new LoggingAction(flowLogger, operation.getName(), providedKey,
          innerVisitor.visitSingle(requiredKeys, providedKey, operation));
    }

    @Override
    public <T> Action visitSequence(final Set<Key<?>> requiredKeys, final Key<T> providedKey, final List<Action> actions) {
      return innerVisitor.visitSequence(requiredKeys, providedKey, actions);
    }

    @Override
    public <T> Action visitBranch(final Set<Key<?>> requiredKeys, final Key<T> providedKey, final Action defaultAction, final List<Conditional<Action>> conditionalActions) {
      return innerVisitor.visitBranch(requiredKeys, providedKey, defaultAction, conditionalActions);
    }

    @Override
    public Condition visitCondition(final Condition condition) {
      return new LoggingCondition(flowLogger, innerVisitor.visitCondition(condition));
    }
  }

  private static final class LoggingAction implements Action {

    private final FlowLogger flowLogger;
    private final String name;
    private final Key<?> outputKey;
    private final Action action;

    private LoggingAction(final FlowLogger flowLogger, final String name, final Key<?> outputKey, final Action action) {
      this.flowLogger = flowLogger;
      this.name = name;
      this.outputKey = outputKey;
      this.action = action;
    }

    @Override
    public Scratchpad run(UUID flowId, final Scratchpad scratchpad) {
      flowLogger.logOperationStarted(flowId, name, scratchpad);
      try {
        Scratchpad result = action.run(flowId, scratchpad);
        flowLogger.logOperationCompleted(flowId, name, outputKey, result.get(outputKey));
        return result;
      } catch (RuntimeException e) {
        flowLogger.logOperationException(flowId, name, e);
        throw e;
      }
    }
  }

  private static final class LoggingCondition implements Condition {

    private final FlowLogger flowLogger;
    private final Condition condition;

    private LoggingCondition(final FlowLogger flowLogger, final Condition condition) {
      this.flowLogger = flowLogger;
      this.condition = condition;
    }

    @Override
    public String getDescription() {
      return condition.getDescription();
    }

    @Override
    public boolean test(UUID flowId, final Scratchpad scratchpad) {
      flowLogger.logConditionStarted(flowId, condition.getDescription(), scratchpad);
      try {
        boolean result = condition.test(flowId, scratchpad);
        flowLogger.logConditionCompleted(flowId, condition.getDescription(), result);
        return result;
      } catch (RuntimeException e) {
        flowLogger.logConditionException(flowId, condition.getDescription(), e);
        throw e;
      }
    }
  }
}
