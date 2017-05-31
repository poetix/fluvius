package com.codepoetics.fluvius.visitors;

import com.codepoetics.fluvius.api.Action;
import com.codepoetics.fluvius.api.ConditionalAction;
import com.codepoetics.fluvius.api.Scratchpad;

import java.util.Map;

final class BranchAction implements Action {

    static Action of(Action defaultAction, Map<String, ConditionalAction> branchActions) {
        return new BranchAction(defaultAction, branchActions);
    }

    BranchAction(Action defaultAction, Map<String, ConditionalAction> branchActions) {
        this.defaultAction = defaultAction;
        this.branchActions = branchActions;
    }

    private final Action defaultAction;
    private final Map<String, ConditionalAction> branchActions;

    @Override
    public Scratchpad run(Scratchpad scratchpad) {
        for (ConditionalAction conditionalAction : branchActions.values()) {
            if (conditionalAction.test(scratchpad)) {
                return conditionalAction.run(scratchpad);
            }
        }
        return defaultAction.run(scratchpad);
    }
}
