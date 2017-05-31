package com.codepoetics.fluvius.api;

public interface DescribableFlow {
    <D extends FlowDescriber<D>> D describe(FlowDescriber<D> describer);
}
