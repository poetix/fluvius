package com.codepoetics.fluvius.visitors;

import com.codepoetics.fluvius.api.*;
import com.codepoetics.fluvius.api.scratchpad.Key;
import com.codepoetics.fluvius.api.scratchpad.Scratchpad;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public final class Visitors {

    private static final Logger LOGGER = Logger.getLogger(Visitors.class.getName());

    private Visitors() {
    }

    public static FlowVisitor getDefault() {
        return new DefaultFlowVisitor();
    }

    public static FlowVisitor logging(FlowVisitor<Action> target) {
        return new LoggingFlowVisitor(target);
    }

    private static final class LoggingFlowVisitor implements FlowVisitor<Action> {
        private final FlowVisitor<Action> innerVisitor;

        private LoggingFlowVisitor(FlowVisitor<Action> innerVisitor) {
            this.innerVisitor = innerVisitor;
        }

        @Override
        public <T> Action visitSingle(Set<Key<?>> requiredKeys, Key<T> providedKey, Operation<T> operation) {
            return new LoggingAction(operation.getName(), providedKey, innerVisitor.visitSingle(requiredKeys, providedKey, operation));
        }

        @Override
        public <T> Action visitSequence(List<Action> actions, Set<Key<?>> requiredKeys, Key<T> providedKey) {
            return innerVisitor.visitSequence(actions, requiredKeys, providedKey);
        }

        @Override
        public <T> Action visitBranch(Action defaultAction, Map<String, ConditionalValue<Action>> conditionalActions, Set<Key<?>> requiredKeys, Key<T> providedKey) {
            return innerVisitor.visitBranch(defaultAction, conditionalActions, requiredKeys, providedKey);
        }

        @Override
        public Condition visitCondition(Condition condition) {
            return new LoggingCondition(innerVisitor.visitCondition(condition));
        }
    }

    private static final class LoggingAction implements Action {

        private final String name;
        private final Key<?> outputKey;
        private final Action action;

        private LoggingAction(String name, Key<?> outputKey, Action action) {
            this.name = name;
            this.outputKey = outputKey;
            this.action = action;
        }

        @Override
        public Scratchpad run(Scratchpad scratchpad) {
            LOGGER.info("Running action " + name + " with scratchpad " + scratchpad);
            Scratchpad result = action.run(scratchpad);
            LOGGER.info("Action " + name + " returned result " + result.get(outputKey));
            return result;
        }
    }

    private static final class LoggingCondition implements Condition {

        private final Condition condition;

        private LoggingCondition(Condition condition) {
            this.condition = condition;
        }

        @Override
        public String getDescription() {
            return condition.getDescription();
        }

        @Override
        public boolean test(Scratchpad scratchpad) {
            LOGGER.info("Testing condition " + condition.getDescription() + " with scratchpad " + scratchpad);
            boolean result = condition.test(scratchpad);
            LOGGER.info("Condition " + condition.getDescription() + " was " + result);
            return result;
        }
    }
}
