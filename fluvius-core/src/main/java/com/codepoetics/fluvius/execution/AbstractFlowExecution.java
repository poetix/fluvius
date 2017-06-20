package com.codepoetics.fluvius.execution;

import com.codepoetics.fluvius.api.FlowExecution;
import com.codepoetics.fluvius.api.FlowResultCallback;
import com.codepoetics.fluvius.api.scratchpad.KeyValue;
import com.codepoetics.fluvius.api.scratchpad.Scratchpad;
import com.codepoetics.fluvius.scratchpad.Scratchpads;

import java.util.UUID;

public abstract class AbstractFlowExecution<T> implements FlowExecution<T> {

  @Override
  public T run(Scratchpad initialScratchpad) throws Exception {
    return run(UUID.randomUUID(), initialScratchpad);
  }

  @Override
  public T run(KeyValue... initialKeyValues) throws Exception {
    return run(UUID.randomUUID(), initialKeyValues);
  }

  @Override
  public T run(UUID flowId, KeyValue... initialKeyValues) throws Exception {
    return run(flowId, Scratchpads.create(initialKeyValues));
  }

  @Override
  public Runnable asAsync(FlowResultCallback<T> callback, Scratchpad initialScratchpad) {
    return asAsync(UUID.randomUUID(), callback, initialScratchpad);
  }

  @Override
  public Runnable asAsync(FlowResultCallback<T> callback, KeyValue...initialKeyValues) {
    return asAsync(UUID.randomUUID(), callback, Scratchpads.create(initialKeyValues));
  }

  @Override
  public Runnable asAsync(UUID flowId, FlowResultCallback<T> callback, KeyValue...initialKeyValues) {
    return asAsync(flowId, callback, Scratchpads.create(initialKeyValues));
  }
}
