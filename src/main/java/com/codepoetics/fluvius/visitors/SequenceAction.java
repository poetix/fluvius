package com.codepoetics.fluvius.visitors;

import com.codepoetics.fluvius.api.Action;
import com.codepoetics.fluvius.api.Scratchpad;

import java.util.List;

final class SequenceAction implements Action {

    static Action of(List<Action> actions) {
        return new SequenceAction(actions);
    }

    private final List<Action> actions;

    private SequenceAction(List<Action> actions) {
        this.actions = actions;
    }

    @Override
    public Scratchpad run(Scratchpad scratchpad) {
        Scratchpad result = scratchpad;
        for (Action action : actions) {
            result = action.run(result);
        }
        return result;
    }
}
