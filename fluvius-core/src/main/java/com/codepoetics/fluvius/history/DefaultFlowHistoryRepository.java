package com.codepoetics.fluvius.history;

import com.codepoetics.fluvius.api.history.*;
import com.codepoetics.fluvius.api.tracing.TraceMap;

import java.util.Map;
import java.util.UUID;

public final class DefaultFlowHistoryRepository<T> implements FlowHistoryRepository<T> {

  public static <T> FlowHistoryRepository<T> using(FlowEventRepository<T> eventRepository, TraceMapRepository traceMapRepository) {
    return new DefaultFlowHistoryRepository<>(eventRepository, traceMapRepository);
  }

  private final FlowEventRepository<T> eventRepository;
  private final TraceMapRepository traceMapRepository;

  private DefaultFlowHistoryRepository(FlowEventRepository<T> eventRepository, TraceMapRepository traceMapRepository) {
    this.eventRepository = eventRepository;
    this.traceMapRepository = traceMapRepository;
  }

  @Override
  public void storeTraceMap(UUID flowId, TraceMap traceMap) {
    traceMapRepository.storeTraceMap(flowId, traceMap);
  }

  @Override
  public FlowHistory<T> getFlowHistory(UUID flowId) {
    return FlowHistory.of(flowId, traceMapRepository.getTraceMap(flowId), eventRepository.getEvents(flowId));
  }

  @Override
  public void stepStarted(UUID flowId, UUID stepId, Map<String, Object> scratchpadState) {
    eventRepository.stepStarted(flowId, stepId, scratchpadState);
  }

  @Override
  public void stepSucceeded(UUID flowId, UUID stepId, Object result) {
    eventRepository.stepSucceeded(flowId, stepId, result);
  }

  @Override
  public void stepFailed(UUID flowId, UUID stepId, Exception exception) {
    eventRepository.stepFailed(flowId, stepId, exception);
  }

}
