package com.codepoetics.fluvius.flows;

import com.codepoetics.fluvius.api.*;
import com.codepoetics.fluvius.api.scratchpad.Key;
import com.codepoetics.fluvius.exceptions.IllegalBranchOutputKeyException;
import com.codepoetics.fluvius.preconditions.Preconditions;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

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

        public <V> ConditionalValue<V> toConditionalAction(FlowVisitor<V> visitor) {
            return new RealConditionalValue<>(visitor.visitCondition(condition), ifTrue.visit(visitor));
        }
    }

    private static final class RealConditionalValue<V> implements ConditionalValue<V> {
        private final Condition condition;
        private final V value;

        private RealConditionalValue(Condition condition, V value) {
            this.condition = condition;
            this.value = value;
        }

        @Override
        public Condition getCondition() {
            return condition;
        }

        @Override
        public V getValue() {
            return value;
        }
    }

    public static <T> Flow<T> create(Flow<T> defaultFlow, Condition condition, Flow<T> ifTrue) {
        Preconditions.checkNotNull("defaultFlow", defaultFlow);
        Preconditions.checkNotNull("condition", condition);
        Preconditions.checkNotNull("ifTrue", ifTrue);

        Map<String, ConditionalFlow<T>> branches = new LinkedHashMap<>();
        branches.put(condition.getDescription(), new ConditionalFlow<>(condition, ifTrue));
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

    private BranchFlow(Set<Key<?>> inputKeys, Key<T> outputKey, Flow<T> defaultFlow, Map<String, ConditionalFlow<T>> branches) {
        super(inputKeys, outputKey);
        this.defaultFlow = defaultFlow;
        this.branches = branches;
    }

    @Override
    public <V> V visit(FlowVisitor<V> visitor) {
        Map<String, ConditionalValue<V>> branchActions = new LinkedHashMap<>();
        for (final ConditionalFlow<T> conditionalFlow : branches.values()) {
            branchActions.put(
                    conditionalFlow.getCondition().getDescription(),
                    conditionalFlow.toConditionalAction(visitor));
        }
        return visitor.visitBranch(defaultFlow.visit(visitor), branchActions, getRequiredKeys(), getProvidedKey());
    }

    @Override
    public Flow<T> orIf(Condition condition, Flow<T> ifTrue) {
        Map<String, ConditionalFlow<T>> branches = new LinkedHashMap<>(this.branches);
        branches.put(condition.getDescription(), new ConditionalFlow<>(condition, ifTrue));
        return create(defaultFlow, branches);
    }


}
