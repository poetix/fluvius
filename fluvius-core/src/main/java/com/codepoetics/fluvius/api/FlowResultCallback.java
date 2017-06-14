package com.codepoetics.fluvius.api;

import java.util.UUID;

public interface FlowResultCallback<T> {
  void onSuccess(UUID flowId, T result);
  void onFailure(UUID flowId, Throwable failure);
}
