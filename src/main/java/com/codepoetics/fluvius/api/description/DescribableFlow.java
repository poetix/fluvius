package com.codepoetics.fluvius.api.description;

public interface DescribableFlow {
    <D extends FlowDescriber<D>> D describe(FlowDescriber<D> describer);
}
