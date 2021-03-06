package com.codepoetics.fluvius.visitors;

import com.codepoetics.fluvius.api.Action;
import com.codepoetics.fluvius.api.Conditional;
import com.codepoetics.fluvius.api.scratchpad.Scratchpad;

import java.util.List;
import java.util.UUID;

final class BranchAction implements Action {

  static Action of(Action defaultAction, List<Conditional<Action>> branchActions) {
    return new BranchAction(defaultAction, branchActions);
  }

  private BranchAction(Action defaultAction, List<Conditional<Action>> branchActions) {
    this.defaultAction = defaultAction;
    this.branchActions = branchActions;
  }

  private final Action defaultAction;
  private final List<Conditional<Action>> branchActions;

  @Override
  public Scratchpad run(UUID flowId, Scratchpad scratchpad) {
    for (Conditional<Action> conditional : branchActions) {
      if (conditional.getCondition().test(flowId, scratchpad)) {
        return conditional.getValue().run(flowId, scratchpad);
      }
    }
    return defaultAction.run(flowId, scratchpad);
  }
}
