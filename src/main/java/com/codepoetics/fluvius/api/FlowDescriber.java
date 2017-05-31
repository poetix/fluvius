package com.codepoetics.fluvius.api;

import java.util.List;
import java.util.Map;

public interface FlowDescriber<SELF extends FlowDescriber<SELF>> {
    SELF describeSingle(String name);
    SELF describeSequence(List<? extends DescribableFlow> flows);
    SELF describeBranch(DescribableFlow defaultFlow, Map<String, DescribableFlow> describableBranches);
}
