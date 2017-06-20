package com.codepoetics.fluvius.test.builders;

import com.codepoetics.fluvius.api.history.FlowEvent;
import com.codepoetics.fluvius.api.history.FlowEventTranslator;
import com.codepoetics.fluvius.api.history.FlowHistory;
import com.codepoetics.fluvius.api.tracing.TraceMap;

import java.util.*;

public final class TestFlowHistory<T> implements FlowHistory<T> {

  public static <T> TestFlowHistory<T> withFlowId(UUID flowId) {
    return new TestFlowHistory<>(flowId);
  }

  private final UUID flowId;
  private TraceMap traceMap;
  private List<FlowEvent<T>> eventHistory = Collections.emptyList();

  private TestFlowHistory(UUID flowId) {
    this.flowId = flowId;
  }

  @Override
  public UUID getFlowId() {
    return flowId;
  }

  public TestFlowHistory<T> withTraceMap(TraceMap traceMap) {
    this.traceMap = traceMap;
    return this;
  }

  @Override
  public TraceMap getTraceMap() {
    return traceMap;
  }

  public TestFlowHistory<T> withEventHistory(FlowEvent<? extends T>...events) {
    return withEventHistory((List) Arrays.asList(events));
  }

  public TestFlowHistory<T> withEventHistory(List<FlowEvent<T>> events) {
    this.eventHistory = events;
    return this;
  }

  @Override
  public List<FlowEvent<T>> getEventHistory() {
    return eventHistory;
  }

  @Override
  public <V> List<V> getTranslatedEventHistory(FlowEventTranslator<T, V> translator) {
    List<V> result = new ArrayList<>(eventHistory.size());
    for (FlowEvent<T> event : eventHistory) {
      result.add(event.translate(translator));
    }
    return result;
  }
}
