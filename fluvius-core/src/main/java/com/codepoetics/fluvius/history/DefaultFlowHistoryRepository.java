package com.codepoetics.fluvius.history;

import com.codepoetics.fluvius.api.history.*;
import com.codepoetics.fluvius.api.tracing.TraceMap;

import java.util.ArrayList;
import java.util.List;
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
    return new ConcreteFlowHistory<>(flowId, traceMapRepository.getTraceMap(flowId), eventRepository.getEvents(flowId));
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

  private static final class ConcreteFlowHistory<T> implements FlowHistory<T> {

    private final UUID flowId;
    private final TraceMap traceMap;
    private final List<FlowEvent<T>> flowEvents;

    private ConcreteFlowHistory(UUID flowId, TraceMap traceMap, List<FlowEvent<T>> flowEvents) {
      this.flowId = flowId;
      this.traceMap = traceMap;
      this.flowEvents = flowEvents;
    }

    @Override
    public UUID getFlowId() {
      return flowId;
    }

    @Override
    public TraceMap getTraceMap() {
      return traceMap;
    }

    @Override
    public List<FlowEvent<T>> getEventHistory() {
      return flowEvents;
    }

    @Override
    public <V> List<V> getTranslatedEventHistory(FlowEventTranslator<T, V> translator) {
      List<V> result = new ArrayList<>(flowEvents.size());
      for (FlowEvent<T> event : flowEvents) {
        result.add(event.translate(translator));
      }
      return result;
    }

    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("Flow history for ").append(flowId).append("\nTrace map: ").append(traceMap).append("\nHistory:");
      for (FlowEvent<T> event : flowEvents) {
        sb.append("\n").append(event);
      }
      return sb.toString();
    }
  }
}
