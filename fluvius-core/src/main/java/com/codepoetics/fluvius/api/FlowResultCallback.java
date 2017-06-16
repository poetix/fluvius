package com.codepoetics.fluvius.api;

import java.util.UUID;

/**
 * A callback which is called when an asynchronously-executed {@link Flow} has completed.
 *
 * @param <T> The type of the result returned by the flow.
 */
public interface FlowResultCallback<T> {

  /**
   * Called if the flow completes successfully, returning a result.
   *
   * @param flowId The id of the flow that has completed.
   * @param result The result returned by the flow.
   */
  void onSuccess(UUID flowId, T result);

  /**
   * Called if the flow terminates exceptionally.
   *
   * @param flowId The id of the flow that has failed.
   * @param failure The exception that was thrown during flow execution.
   */
  void onFailure(UUID flowId, Throwable failure);

}
