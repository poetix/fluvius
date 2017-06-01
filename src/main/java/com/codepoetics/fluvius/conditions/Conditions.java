package com.codepoetics.fluvius.conditions;

import com.codepoetics.fluvius.api.Condition;
import com.codepoetics.fluvius.api.functional.ScratchpadPredicate;
import com.codepoetics.fluvius.api.functional.ValuePredicate;
import com.codepoetics.fluvius.api.scratchpad.Key;
import com.codepoetics.fluvius.api.scratchpad.Scratchpad;
import com.codepoetics.fluvius.predicates.Predicates;

public final class Conditions {

    private Conditions() {
    }

    private static Condition fromPredicate(String description, ScratchpadPredicate predicate) {
        return new PredicateCondition(description, predicate);
    }

    private static final class PredicateCondition implements Condition {
        private final String description;
        private final ScratchpadPredicate predicate;

        private PredicateCondition(String description, ScratchpadPredicate predicate) {
            this.description = description;
            this.predicate = predicate;
        }

        @Override
        public String getDescription() {
            return description;
        }

        @Override
        public boolean test(Scratchpad scratchpad) {
            return predicate.test(scratchpad);
        }
    }

    public static <T> Condition keyEquals(Key<T> key, T expected) {
        return fromPredicate(key.getName() + " = " + expected, Predicates.keyEquals(key, expected));
    }

    public static <T> Condition keyMatches(Key<T> key, String description, ValuePredicate<T> predicate) {
        return fromPredicate(key.getName() + " " + description, Predicates.keyMatches(key, predicate));
    }
}
