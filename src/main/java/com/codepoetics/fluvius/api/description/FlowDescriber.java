package com.codepoetics.fluvius.api.description;

import com.codepoetics.fluvius.api.scratchpad.Key;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface FlowDescriber<SELF extends FlowDescriber<SELF>> {
    SELF describeSingle(String name, Set<Key<?>> requiredKeys, Key<?> providedKey);
    SELF describeSequence(List<? extends DescribableFlow> flows, Set<Key<?>> requiredKeys, Key<?> providedKey);
    SELF describeBranch(DescribableFlow defaultFlow, Map<String, DescribableFlow> describableBranches, Set<Key<?>> requiredKeys, Key<?> providedKey);
}
