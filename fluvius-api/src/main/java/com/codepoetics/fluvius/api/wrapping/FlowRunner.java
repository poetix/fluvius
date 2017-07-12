package com.codepoetics.fluvius.api.wrapping;

import com.codepoetics.fluvius.api.FlowResultCallback;

import java.util.UUID;

public interface FlowRunner<T> {

  T run();

  T run(UUID flowId);

  Runnable asAsync(FlowResultCallback<T> callback);

  Runnable asAsync(UUID flowId, FlowResultCallback<T> callback);
}
