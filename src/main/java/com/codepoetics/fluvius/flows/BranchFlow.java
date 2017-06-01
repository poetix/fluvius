package com.codepoetics.fluvius.flows;

import com.codepoetics.fluvius.api.*;
import com.codepoetics.fluvius.exceptions.IllegalBranchOutputKeyException;
import com.codepoetics.fluvius.preconditions.Preconditions;

import java.util.*;

public class BranchFlow<T> extends AbstractFlow<T> {

    private static final class ConditionalFlow<T> {
        private final Condition condition;
        private final Flow<T> ifTrue;

        private ConditionalFlow(Condition condition, Flow<T> ifTrue) {
            this.condition = condition;
            this.ifTrue = ifTrue;
        }

        public Condition getCondition() {
            return condition;
        }

        public Flow<T> getIfTrue() {
            return ifTrue;
        }

        public ConditionalAction toConditionalAction(FlowVisitor visitor) {
            return new RealConditionalAction(visitor.visitCondition(condition), ifTrue.visit(visitor));
        }
    }

    private static final class RealConditionalAction implements ConditionalAction {
        private final Condition condition;
        private final Action action;

        private RealConditionalAction(Condition condition, Action action) {
            this.condition = condition;
            this.action = action;
        }

        @Override
        public String getDescription() {
            return condition.getDescription();
        }

        @Override
        public boolean test(Scratchpad scratchpad) {
            return condition.test(scratchpad);
        }

        @Override
        public Scratchpad run(Scratchpad scratchpad) {
            return action.run(scratchpad);
        }
    }

    public static <T> Flow<T> create(Flow<T> defaultFlow, Condition condition, Flow<T> ifTrue) {
        Preconditions.checkNotNull("defaultFlow", defaultFlow);
        Preconditions.checkNotNull("condition", condition);
        Preconditions.checkNotNull("ifTrue", ifTrue);

        Map<String, ConditionalFlow<T>> branches = new LinkedHashMap<>();
        branches.put(condition.getDescription(), new ConditionalFlow<T>(condition, ifTrue));
        return create(defaultFlow, branches);
    }

    private static <T> Flow<T> create(Flow<T> defaultFlow, Map<String, ConditionalFlow<T>> branches) {
        Key<T> defaultOutputKey = defaultFlow.getProvidedKey();
        Set<Key<?>> requiredKeys = defaultFlow.getRequiredKeys();

        for (ConditionalFlow<T> conditionalFlow : branches.values()) {
            Key<T> conditionalOutputKey = conditionalFlow.getIfTrue().getProvidedKey();
            if (!conditionalOutputKey.equals(defaultOutputKey)) {
                throw new IllegalBranchOutputKeyException(
                        defaultOutputKey,
                        conditionalFlow.getCondition().getDescription(),
                        conditionalOutputKey);
            }
            requiredKeys.addAll(conditionalFlow.getIfTrue().getRequiredKeys());
        }

        return new BranchFlow<>(requiredKeys, defaultOutputKey, defaultFlow, branches);
    }

    private final Flow<T> defaultFlow;
    private final Map<String, ConditionalFlow<T>> branches;

    public BranchFlow(Set<Key<?>> inputKeys, Key<T> outputKey, Flow<T> defaultFlow, Map<String, ConditionalFlow<T>> branches) {
        super(inputKeys, outputKey);
        this.defaultFlow = defaultFlow;
        this.branches = branches;
    }

    @Override
    public <V extends FlowVisitor> Action visit(V visitor) {
        Map<String, ConditionalAction> branchActions = new LinkedHashMap<>();
        for (final ConditionalFlow<T> conditionalFlow : branches.values()) {
            branchActions.put(
                    conditionalFlow.getCondition().getDescription(),
                    conditionalFlow.toConditionalAction(visitor));
        }
        return visitor.visitBranch(defaultFlow.visit(visitor), branchActions);
    }

    @Override
    public <D extends FlowDescriber<D>> D describe(FlowDescriber<D> describer) {
        Map<String, DescribableFlow> describableBranches = new LinkedHashMap<>();
        for (ConditionalFlow<T> branchFlow : branches.values()) {
            describableBranches.put(branchFlow.getCondition().getDescription(), branchFlow.getIfTrue());
        }
        return describer.describeBranch(defaultFlow, describableBranches, getRequiredKeys(), getProvidedKey());
    }

    @Override
    public Flow<T> orIf(Condition condition, Flow<T> ifTrue) {
        Map<String, ConditionalFlow<T>> branches = new LinkedHashMap<>(this.branches);
        branches.put(condition.getDescription(), new ConditionalFlow<T>(condition, ifTrue));
        return create(defaultFlow, branches);
    }


}
