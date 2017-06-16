package com.codepoetics.fluvius.services;

import com.codepoetics.fluvius.api.Condition;
import com.codepoetics.fluvius.api.Flow;
import com.codepoetics.fluvius.api.functional.F1;
import com.codepoetics.fluvius.api.functional.P1;
import com.codepoetics.fluvius.api.scratchpad.Key;
import com.codepoetics.fluvius.api.services.ServiceCallResult;
import com.codepoetics.fluvius.conditions.Conditions;
import com.codepoetics.fluvius.flows.Flows;

/**
 * Utility class providing methods for constructing {@link ServiceCallResult}s, and generating branching {@link Flow}s that deal with the outcome of making service calls.
 */
public final class ServiceCalls {

  private ServiceCalls() {
  }

  /**
   * Create a {@link ServiceCallResult} representing the result of a successful service call.
   *
   * @param result The result returned by the service.
   * @param <T> The type of the result returned by the service.
   * @return The constructed service call result.
   */
  public static <T> ServiceCallResult<T> success(final T result) {
    return new ServiceCallSuccessResult<>(result);
  }

  /**
   * Create a {@link ServiceCallResult} representing the result of a failed service call.
   *
   * @param reason The reason why the service call failed.
   * @param <T> The type of the result that would have been returned by the service, had it succeeded.
   * @return The constructed service call result.
   */
  public static <T> ServiceCallResult<T> failure(final String reason) {
    return new ServiceCallFailureResult<>(reason);
  }

  /**
   * The first stage of a fluent API for defining branching {@link Flow}s that respond to {@link ServiceCallResult}s.
   */
  public interface KeyCapture {
    /**
     * Capture the {@link Flow} to be executed if the service call succeeds.
     * @param successFlow The flow to be executed if the service call succeeds.
     * @param <V> The type of the value to be returned by the branching {@link Flow} constructed using this API.
     * @return The next stage in the fluent API.
     */
    <V> SuccessCapture<V> onSuccess(Flow<V> successFlow);
  }

  /**
   * The second stage of a fluent API for defining branching {@link Flow}s that respond to {@link ServiceCallResult}s.
   * @param <V> The type of the value to be returned by the branching {@link Flow} constructed using this API.
   */
  public interface SuccessCapture<V> {
    /**
     * Capture the {@link Flow} to be executed if the service call fails.
     * @param failureFlow The flow to be executed if the service call fails.
     * @return The constructed branching {@link Flow}.
     */
    Flow<V> otherwise(Flow<V> failureFlow);
  }

  /**
   * Construct a branching {@link Flow} that responds to a {@link ServiceCallResult}.
   *
   * @param callResultKey The key to which the call result will have been written.
   * @param successKey The key to which a successful call result value should be extracted.
   * @param <T> The type of the call result.
   * @return The next stage in the fluent API that constructs the branching {@link Flow}.
   */
  public static <T> KeyCapture afterServiceCall(final Key<ServiceCallResult<T>> callResultKey, final Key<T> successKey) {
    return new KeyCapture() {
      @Override
      public <V> SuccessCapture<V> onSuccess(final Flow<V> successFlow) {
        return new SuccessCapture<V>() {
          @Override
          public Flow<V> otherwise(final Flow<V> failureFlow) {
            return Flows.branch(
                isFailed(callResultKey), failureFlow)
                .otherwise(extractSuccessResult(callResultKey, successKey).then(successFlow));
          }
        };
      }
    };
  }

  private static <T> Condition isFailed(Key<ServiceCallResult<T>> key) {
    return Conditions.keyMatches(
        key,
        "is failed",
        new P1<ServiceCallResult<T>>() {
          @Override
          public boolean test(ServiceCallResult<T> serviceCallResult) {
            return !serviceCallResult.succeeded();
          }
        });
  }

  private static <T> Flow<T> extractSuccessResult(Key<ServiceCallResult<T>> resultKey, Key<T> valueKey) {
    return Flows.obtaining(valueKey).from(resultKey).using(new F1<ServiceCallResult<T>, T>() {
      @Override
      public T apply(ServiceCallResult<T> serviceCallResult) {
        return serviceCallResult.result();
      }
    });
  }

  private static class ServiceCallSuccessResult<T> implements ServiceCallResult<T> {
    private final T result;

    private ServiceCallSuccessResult(T result) {
      this.result = result;
    }

    @Override
    public boolean succeeded() {
      return true;
    }

    @Override
    public T result() {
      return result;
    }

    @Override
    public String failureReason() {
      throw new UnsupportedOperationException("Successful ServiceCallResult has no failure reason");
    }
  }

  private static class ServiceCallFailureResult<T> implements ServiceCallResult<T> {
    private final String reason;

    private ServiceCallFailureResult(String reason) {
      this.reason = reason;
    }

    @Override
    public boolean succeeded() {
      return true;
    }

    @Override
    public T result() {
      throw new UnsupportedOperationException("Failed ServiceCallResult has no result");
    }

    @Override
    public String failureReason() {
      return reason;
    }
  }
}
