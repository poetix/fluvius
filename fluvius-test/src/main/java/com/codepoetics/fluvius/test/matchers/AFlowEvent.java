package com.codepoetics.fluvius.test.matchers;

import com.codepoetics.fluvius.api.history.*;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

import java.util.Map;
import java.util.UUID;

public final class AFlowEvent<T> extends BasePropertyMatcher<FlowEvent<T>> {

  public static <T> AFlowEvent<T> stepStarted() {
    return new AFlowEvent<>(StepStartedEvent.class);
  }

  public static <T> AFlowEvent<T> stepStarted(Map<String, T> scratchpadState) {
    return stepStarted(Matchers.equalTo(scratchpadState));
  }

  public static <T> AFlowEvent<T> stepStarted(Matcher<? super Map<String, T>> scratchpadStateMatcher) {
    AFlowEvent<T> flowEventMatcher = stepStarted();
    flowEventMatcher.scratchpadStateMatcher = scratchpadStateMatcher;
    return flowEventMatcher;
  }

  public static <T> AFlowEvent<T> stepSucceeded() {
    return new AFlowEvent<>(StepSucceededEvent.class);
  }

  public static <T> AFlowEvent<T> stepSucceeded(T result) {
    return stepSucceeded(Matchers.equalTo(result));
  }

  public static <T> AFlowEvent<T> stepSucceeded(Matcher<? super T> resultMatcher) {
    AFlowEvent<T> flowEventMatcher = stepSucceeded();
    flowEventMatcher.resultMatcher = resultMatcher;
    return flowEventMatcher;
  }

  public static <T> AFlowEvent<T> stepFailed() {
    return new AFlowEvent<>(StepFailedEvent.class);
  }

  public static <T> AFlowEvent<T> stepFailed(T result) {
    return stepFailed(Matchers.equalTo(result));
  }

  public static <T> AFlowEvent<T> stepFailed(Matcher<? super T> reasonMatcher) {
    AFlowEvent<T> flowEventMatcher = stepFailed();
    flowEventMatcher.reasonMatcher = reasonMatcher;
    return flowEventMatcher;
  }

  private final Class<? extends FlowEvent> expectedEventType;
  private Matcher<? super UUID> flowIdMatcher;
  private Matcher<? super UUID> stepIdMatcher;
  private Matcher<? super Long> timestampMatcher;
  private Matcher<? super Map<String, T>> scratchpadStateMatcher;
  private Matcher<? super T> resultMatcher;
  private Matcher<? super T> reasonMatcher;

  private AFlowEvent(Class<? extends FlowEvent> expectedEventType) {
    super(expectedEventType.getSimpleName());
    this.expectedEventType = expectedEventType;
  }

  public AFlowEvent<T> withFlowId(UUID flowId) {
    return withFlowId(Matchers.equalTo(flowId));
  }

  public AFlowEvent<T> withFlowId(Matcher<? super UUID> flowIdMatcher) {
    this.flowIdMatcher = flowIdMatcher;
    return this;
  }

  public AFlowEvent<T> withStepId(UUID stepId) {
    return withStepId(Matchers.equalTo(stepId));
  }

  public AFlowEvent<T> withStepId(Matcher<? super UUID> stepIdMatcher) {
    this.stepIdMatcher = stepIdMatcher;
    return this;
  }

  public AFlowEvent<T> withTimestamp(long timestamp) {
    return withTimestamp(Matchers.equalTo(timestamp));
  }

  public AFlowEvent<T> withTimestamp(Matcher<? super Long> timestampMatcher) {
    this.timestampMatcher = timestampMatcher;
    return this;
  }

  @Override
  protected void describeProperties(PropertyDescriber describer) {
    describer
        .describeProperty("flowId", flowIdMatcher)
        .describeProperty("stepId", stepIdMatcher)
        .describeProperty("timestamp", timestampMatcher)
        .describeProperty("scratchpadState", scratchpadStateMatcher)
        .describeProperty("result", resultMatcher)
        .describeProperty("reason", reasonMatcher);
  }

  @Override
  protected void checkProperties(FlowEvent<T> flowEvent, final PropertyMismatchDescriber describer) {
    describer.check("type", flowEvent, Matchers.instanceOf(expectedEventType))
        .check("flowId", flowEvent.getFlowId(), flowIdMatcher)
        .check("stepId", flowEvent.getStepId(), stepIdMatcher)
        .check("timestamp", flowEvent.getTimestamp(), timestampMatcher);

    flowEvent.translate(new FlowEventTranslator<T, PropertyMismatchDescriber>() {
      @Override
      public PropertyMismatchDescriber translateStepStartedEvent(StepStartedEvent<T> event) {
        return describer.check("scratchpadState", event.getScratchpadState(), scratchpadStateMatcher);
      }

      @Override
      public PropertyMismatchDescriber translateStepSucceededEvent(StepSucceededEvent<T> event) {
        return describer.check("result", event.getResult(), resultMatcher);
      }

      @Override
      public PropertyMismatchDescriber translateStepFailedEvent(StepFailedEvent<T> event) {
        return describer.check("reason", event.getReason(), reasonMatcher);
      }
    });
  }
}
