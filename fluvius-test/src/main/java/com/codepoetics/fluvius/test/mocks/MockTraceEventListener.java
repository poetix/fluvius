package com.codepoetics.fluvius.test.mocks;

import com.codepoetics.fluvius.api.scratchpad.Key;
import com.codepoetics.fluvius.api.scratchpad.KeyValue;
import com.codepoetics.fluvius.api.scratchpad.Scratchpad;
import com.codepoetics.fluvius.api.scratchpad.ScratchpadStorage;
import com.codepoetics.fluvius.api.tracing.TraceEventListener;
import com.codepoetics.fluvius.test.matchers.AMap;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.mockito.Mockito;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.verify;

/**
 * A mock {@link TraceEventListener} with some helpful methods to verify events.
 */
public final class MockTraceEventListener implements TraceEventListener {

  private final TraceEventListener innerMock = Mockito.mock(TraceEventListener.class);
  private Matcher<UUID> flowIdMatcher = Matchers.any(UUID.class);
  private Matcher<UUID> lastStepIdMatcher = Matchers.any(UUID.class);

  public MockTraceEventListener forFlow(UUID flowId) {
    return forFlow(Matchers.equalTo(flowId));
  }

  public MockTraceEventListener forFlow(Matcher<UUID> flowIdMatcher) {
    this.flowIdMatcher = flowIdMatcher;
    return this;
  }

  @Override
  public void stepStarted(UUID flowId, UUID stepId, Map<String, Object> scratchpadState) {
    innerMock.stepStarted(flowId, stepId, scratchpadState);
  }

  @Override
  public void stepSucceeded(UUID flowId, UUID stepId, Object result) {
    innerMock.stepSucceeded(flowId, stepId, result);
  }

  @Override
  public void stepFailed(UUID flowId, UUID stepId, Exception exception) {
    innerMock.stepFailed(flowId, stepId, exception);
  }

  public MockTraceEventListener verifyStepStarted(Matcher<UUID> stepIdMatcher, KeyValue... scratchpadContents) {
    final Map<String, Object> scratchpadState = new LinkedHashMap<>();

    ScratchpadStorage storage = new ScratchpadStorage() {
      @Override
      public <T> void storeSuccess(Key<T> key, T value) {
        scratchpadState.put(key.getName(), value);
      }

      @Override
      public void storeFailure(Key<?> key, Throwable reason) {
        scratchpadState.put(key.getName(), reason);
      }
    };

    for (KeyValue keyValue : scratchpadContents) {
      keyValue.store(storage);
    }

    return verifyStepStarted(stepIdMatcher, AMap.containing(scratchpadState));
  }

  public MockTraceEventListener verifyStepStarted(Matcher<UUID> stepIdMatcher, Scratchpad scratchpad) {
    Map<String, Object> scratchpadState = new LinkedHashMap<>();
    for (Map.Entry<Key<?>, Object> entry : scratchpad.toMap().entrySet()) {
      scratchpadState.put(entry.getKey().getName(), entry.getValue());
    }
    return verifyStepStarted(stepIdMatcher, AMap.containing(scratchpadState));
  }

  public MockTraceEventListener verifyStepStarted(Matcher<UUID> stepIdMatcher, Matcher<Map<String, Object>> scratchpadState) {
    lastStepIdMatcher = stepIdMatcher;
    verify(innerMock).stepStarted(argThat(flowIdMatcher), argThat(stepIdMatcher), argThat(scratchpadState));
    return this;
  }

  public MockTraceEventListener verifyStepSucceeded(Matcher<UUID> stepIdMatcher, Object result) {
    return verifyStepSucceeded(stepIdMatcher, equalTo(result));
  }

  public MockTraceEventListener verifyStepSucceeded(Matcher<UUID> stepIdMatcher, Matcher<Object> resultMatcher) {
    verify(innerMock).stepSucceeded(argThat(flowIdMatcher), argThat(stepIdMatcher), argThat(resultMatcher));
    return this;
  }

  public MockTraceEventListener andSucceeded(Object result) {
    return verifyStepSucceeded(lastStepIdMatcher, result);
  }

  public MockTraceEventListener andSucceeded(Matcher<Object> resultMatcher) {
    return verifyStepSucceeded(lastStepIdMatcher, resultMatcher);
  }

  public MockTraceEventListener verifyStepFailed(Matcher<UUID> stepIdMatcher, Exception reason) {
    return verifyStepFailed(stepIdMatcher, Matchers.equalTo(reason));
  }

  public MockTraceEventListener verifyStepFailed(Matcher<UUID> stepIdMatcher, Matcher<Exception> reasonMatcher) {
    verify(innerMock).stepFailed(argThat(flowIdMatcher), argThat(stepIdMatcher), argThat(reasonMatcher));
    return this;
  }

  public MockTraceEventListener andFailed(Exception reason) {
    return verifyStepFailed(lastStepIdMatcher, reason);
  }

  public MockTraceEventListener andFailed(Matcher<Exception> reasonMatcher) {
    return verifyStepFailed(lastStepIdMatcher, reasonMatcher);
  }
}
