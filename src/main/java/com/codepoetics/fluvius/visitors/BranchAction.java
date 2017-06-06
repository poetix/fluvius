package com.codepoetics.fluvius.visitors;

import com.codepoetics.fluvius.api.Action;
import com.codepoetics.fluvius.api.Conditional;
import com.codepoetics.fluvius.api.scratchpad.Scratchpad;

import java.util.Map;

final class BranchAction implements Action {

  static Action of(final Action defaultAction, final Map<String, Conditional<Action>> branchActions) {
    return new BranchAction(defaultAction, branchActions);
  }

  private BranchAction(final Action defaultAction, final Map<String, Conditional<Action>> branchActions) {
    this.defaultAction = defaultAction;
    this.branchActions = branchActions;
  }

  private final Action defaultAction;
  private final Map<String, Conditional<Action>> branchActions;

  @Override
  public Scratchpad run(final Scratchpad scratchpad) {
    for (Conditional<Action> conditional : branchActions.values()) {
      if (conditional.getCondition().test(scratchpad)) {
        return conditional.getValue().run(scratchpad);
      }
    }
    return defaultAction.run(scratchpad);
  }
}
