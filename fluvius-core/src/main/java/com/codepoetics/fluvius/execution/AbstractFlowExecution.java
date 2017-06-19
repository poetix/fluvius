package com.codepoetics.fluvius.execution;

import com.codepoetics.fluvius.api.FlowExecution;
import com.codepoetics.fluvius.api.FlowResultCallback;
import com.codepoetics.fluvius.api.scratchpad.KeyValue;
import com.codepoetics.fluvius.api.scratchpad.Scratchpad;
import com.codepoetics.fluvius.scratchpad.Scratchpads;

import java.util.UUID;

public abstract class AbstractFlowExecution<T> implements FlowExecution<T> {

  @Override
  public T run(final Scratchpad initialScratchpad) throws Exception {
    return run(UUID.randomUUID(), initialScratchpad);
  }

  @Override
  public T run(final KeyValue... initialKeyValues) throws Exception {
    return run(UUID.randomUUID(), initialKeyValues);
  }

  @Override
  public T run(final UUID flowId, final KeyValue... initialKeyValues) throws Exception {
    return run(flowId, Scratchpads.create(initialKeyValues));
  }

  @Override
  public Runnable asAsync(final FlowResultCallback<T> callback, final Scratchpad initialScratchpad) {
    return asAsync(UUID.randomUUID(), callback, initialScratchpad);
  }

  @Override
  public Runnable asAsync(final FlowResultCallback<T> callback, final KeyValue...initialKeyValues) {
    return asAsync(UUID.randomUUID(), callback, Scratchpads.create(initialKeyValues));
  }

  @Override
  public Runnable asAsync(final UUID flowId, final FlowResultCallback<T> callback, final KeyValue...initialKeyValues) {
    return asAsync(flowId, callback, Scratchpads.create(initialKeyValues));
  }
}
