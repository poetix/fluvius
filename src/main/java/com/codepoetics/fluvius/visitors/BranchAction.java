package com.codepoetics.fluvius.visitors;

import com.codepoetics.fluvius.api.Action;
import com.codepoetics.fluvius.api.ConditionalValue;
import com.codepoetics.fluvius.api.scratchpad.Scratchpad;

import java.util.Map;

final class BranchAction implements Action {

    static Action of(Action defaultAction, Map<String, ConditionalValue<Action>> branchActions) {
        return new BranchAction(defaultAction, branchActions);
    }

    private BranchAction(Action defaultAction, Map<String, ConditionalValue<Action>> branchActions) {
        this.defaultAction = defaultAction;
        this.branchActions = branchActions;
    }

    private final Action defaultAction;
    private final Map<String, ConditionalValue<Action>> branchActions;

    @Override
    public Scratchpad run(Scratchpad scratchpad) {
        for (ConditionalValue<Action> conditionalValue : branchActions.values()) {
            if (conditionalValue.getCondition().test(scratchpad)) {
                return conditionalValue.getValue().run(scratchpad);
            }
        }
        return defaultAction.run(scratchpad);
    }
}
