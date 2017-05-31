package com.codepoetics.fluvius.predicates;

import com.codepoetics.fluvius.api.Key;
import com.codepoetics.fluvius.api.Scratchpad;
import com.codepoetics.fluvius.api.ScratchpadPredicate;
import com.codepoetics.fluvius.api.ValuePredicate;

public final class Predicates {

    private Predicates() {
    }

    public static <T> ScratchpadPredicate keyMatches(final Key<T> key, final ValuePredicate<T> predicate) {
        return new MatchingKeyValuePredicate<>(predicate, key);
    }

    public static <T> ScratchpadPredicate keyEquals(final Key<T> key, T expected) {
        return keyMatches(key, equalTo(expected));
    }

    public static <T> ValuePredicate<T> equalTo(final T expected) {
        return new ValuePredicate<T>() {
            @Override
            public boolean test(T value) {
                return value.equals(expected);
            }
        };
    }

    private static final class MatchingKeyValuePredicate<T> implements ScratchpadPredicate {
        private final ValuePredicate<T> predicate;
        private final Key<T> key;

        private MatchingKeyValuePredicate(ValuePredicate<T> predicate, Key<T> key) {
            this.predicate = predicate;
            this.key = key;
        }

        @Override
        public boolean test(Scratchpad scratchpad) {
            return predicate.test(scratchpad.get(key));
        }
    }
}
