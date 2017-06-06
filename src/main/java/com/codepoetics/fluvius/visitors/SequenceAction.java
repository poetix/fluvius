package com.codepoetics.fluvius.visitors;

import com.codepoetics.fluvius.api.Action;
import com.codepoetics.fluvius.api.scratchpad.Scratchpad;

import java.util.List;

final class SequenceAction implements Action {

  static Action of(final List<Action> actions) {
    return new SequenceAction(actions);
  }

  private final List<Action> actions;

  private SequenceAction(final List<Action> actions) {
    this.actions = actions;
  }

  @Override
  public Scratchpad run(final Scratchpad scratchpad) {
    Scratchpad result = scratchpad;
    for (Action action : actions) {
      result = action.run(result);
    }
    return result;
  }
}
