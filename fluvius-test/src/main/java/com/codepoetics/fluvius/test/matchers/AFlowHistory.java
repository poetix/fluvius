package com.codepoetics.fluvius.test.matchers;

import com.codepoetics.fluvius.api.history.FlowEvent;
import com.codepoetics.fluvius.api.history.FlowHistory;
import com.codepoetics.fluvius.api.tracing.TraceMap;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;

public final class AFlowHistory<T> extends BasePropertyMatcher<FlowHistory<T>> {

  public static <T> AFlowHistory<T> withAnyFlowId() {
    return withFlowId(Matchers.any(UUID.class));
  }

  public static <T> AFlowHistory<T> withFlowId(UUID expected) {
    return withFlowId(equalTo(expected));
  }

  public static <T> AFlowHistory<T> withFlowId(Matcher<? super UUID> flowIdMatcher) {
    return new AFlowHistory<>(flowIdMatcher);
  }

  private final Matcher<? super UUID> flowIdMatcher;
  private Matcher<? super TraceMap> traceMapMatcher;
  private List<Matcher<? super FlowEvent<T>>> eventMatchers;

  private AFlowHistory(Matcher<? super UUID> flowIdMatcher) {
    super("FlowHistory");
    this.flowIdMatcher = flowIdMatcher;
  }

  public AFlowHistory<T> withTraceMap(Matcher<? super TraceMap> traceMapMatcher) {
    this.traceMapMatcher = traceMapMatcher;
    return this;
  }

  @SafeVarargs
  final public AFlowHistory<T> withEventHistory(Matcher<? super FlowEvent<T>>...eventMatchers) {
    return withEventHistory(Arrays.asList(eventMatchers));
  }

  public AFlowHistory<T> withEventHistory(List<Matcher<? super FlowEvent<T>>> eventMatchers) {
    this.eventMatchers = eventMatchers;
    return this;
  }

  @Override
  protected void describeProperties(PropertyDescriber describer) {
    describer.describeProperty("flowId", flowIdMatcher)
        .describeProperty("traceMap", traceMapMatcher)
        .describeProperty("eventHistory", eventMatchers);
  }

  @Override
  protected void checkProperties(FlowHistory<T> flowHistory, PropertyMismatchDescriber describer) {
    describer.check("flowId", flowHistory.getFlowId(), flowIdMatcher)
        .check("traceMap", flowHistory.getTraceMap(), traceMapMatcher)
        .check("eventHistory", flowHistory.getEventHistory(), eventMatchers == null ? null : Matchers.contains(eventMatchers));
  }
}
