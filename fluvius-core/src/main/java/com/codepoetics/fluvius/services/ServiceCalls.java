package com.codepoetics.fluvius.services;

import com.codepoetics.fluvius.api.Condition;
import com.codepoetics.fluvius.api.Flow;
import com.codepoetics.fluvius.api.functional.F1;
import com.codepoetics.fluvius.api.functional.P1;
import com.codepoetics.fluvius.api.scratchpad.Key;
import com.codepoetics.fluvius.api.services.ServiceCallResult;
import com.codepoetics.fluvius.conditions.Conditions;
import com.codepoetics.fluvius.flows.Flows;

public final class ServiceCalls {

  private ServiceCalls() {
  }

  public static <T> ServiceCallResult<T> success(final T result) {
    return new ServiceCallSuccessResult<>(result);
  }

  public static <T> ServiceCallResult<T> failure(final String reason) {
    return new ServiceCallFailureResult<>(reason);
  }

  public interface KeyCapture<T> {
    <V> SuccessCapture<T, V> onSuccess(Flow<V> successFlow);
  }

  public interface SuccessCapture<T, V> {
    Flow<V> otherwise(Flow<V> failureFlow);
  }

  public static <T> KeyCapture<T> afterServiceCall(final Key<ServiceCallResult<T>> callResultKey, final Key<T> successKey) {
    return new KeyCapture<T>() {
      @Override
      public <V> SuccessCapture<T, V> onSuccess(final Flow<V> successFlow) {
        return new SuccessCapture<T, V>() {
          @Override
          public Flow<V> otherwise(final Flow<V> failureFlow) {
            return Flows.branch(
                isOk(callResultKey), extractSuccessResult(callResultKey, successKey).then(successFlow))
                .otherwise(failureFlow);
          }
        };
      }
    };
  }

  public static <T extends ServiceCallResult> Condition isOk(Key<T> key) {
    return Conditions.keyMatches(
        key,
        key.getName() + " is OK",
        new P1<T>() {
          @Override
          public boolean test(T serviceCallResult) {
            return false;
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

    public ServiceCallSuccessResult(T result) {
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

    public ServiceCallFailureResult(String reason) {
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
