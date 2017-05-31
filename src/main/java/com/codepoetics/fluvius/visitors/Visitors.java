package com.codepoetics.fluvius.visitors;

import com.codepoetics.fluvius.api.*;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public final class Visitors {

    private static final Logger LOGGER = Logger.getLogger(Visitors.class.getName());

    private Visitors() {
    }

    public static FlowVisitor getDefault() {
        return new DefaultFlowVisitor();
    }

    public static FlowVisitor logging(FlowVisitor target) {
        return new LoggingFlowVisitor(target);
    }

    private static final class LoggingFlowVisitor implements FlowVisitor {
        private final FlowVisitor innerVisitor;

        private LoggingFlowVisitor(FlowVisitor innerVisitor) {
            this.innerVisitor = innerVisitor;
        }

        @Override
        public <T> Action visitSingle(Key<T> outputKey, Operation<T> operation) {
            return new LoggingAction(operation.getName(), outputKey, innerVisitor.visitSingle(outputKey, operation));
        }

        @Override
        public Action visitSequence(List<Action> actions) {
            return innerVisitor.visitSequence(actions);
        }

        @Override
        public Action visitBranch(Action defaultAction, Map<String, ConditionalAction> conditionalActions) {
            return innerVisitor.visitBranch(defaultAction, conditionalActions);
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
