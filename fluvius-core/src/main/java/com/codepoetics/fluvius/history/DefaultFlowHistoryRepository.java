package com.codepoetics.fluvius.history;

import com.codepoetics.fluvius.api.history.*;
import com.codepoetics.fluvius.api.tracing.TraceMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DefaultFlowHistoryRepository<T> implements FlowHistoryRepository<T> {

  public static <T> FlowHistoryRepository<T> using(final FlowEventRepository<T> eventRepository, final TraceMapRepository traceMapRepository) {
    return new DefaultFlowHistoryRepository<>(eventRepository, traceMapRepository);
  }

  private final FlowEventRepository<T> eventRepository;
  private final TraceMapRepository traceMapRepository;

  private DefaultFlowHistoryRepository(final FlowEventRepository<T> eventRepository, final TraceMapRepository traceMapRepository) {
    this.eventRepository = eventRepository;
    this.traceMapRepository = traceMapRepository;
  }

  @Override
  public void storeTraceMap(final UUID flowId, final TraceMap traceMap) {
    traceMapRepository.storeTraceMap(flowId, traceMap);
  }

  @Override
  public FlowHistory<T> getFlowHistory(final UUID flowId) {
    return new ConcreteFlowHistory<>(flowId, traceMapRepository.getTraceMap(flowId), eventRepository.getEvents(flowId));
  }

  @Override
  public void stepStarted(final UUID flowId, final UUID stepId, final Map<String, Object> scratchpadState) {
    eventRepository.stepStarted(flowId, stepId, scratchpadState);
  }

  @Override
  public void stepSucceeded(final UUID flowId, final UUID stepId, final Object result) {
    eventRepository.stepSucceeded(flowId, stepId, result);
  }

  @Override
  public void stepFailed(final UUID flowId, final UUID stepId, final Throwable throwable) {
    eventRepository.stepFailed(flowId, stepId, throwable);
  }

  private static final class ConcreteFlowHistory<T> implements FlowHistory<T> {

    private final UUID flowId;
    private final TraceMap traceMap;
    private final List<FlowEvent<T>> flowEvents;

    private ConcreteFlowHistory(final UUID flowId, final TraceMap traceMap, final List<FlowEvent<T>> flowEvents) {
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
    public <V> List<V> getTranslatedEventHistory(final FlowEventTranslator<T, V> translator) {
      final List<V> result = new ArrayList<>(flowEvents.size());
      for (final FlowEvent<T> event : flowEvents) {
        result.add(event.translate(translator));
      }
      return result;
    }

    @Override
    public String toString() {
      final StringBuilder sb = new StringBuilder();
      sb.append("Flow history for ").append(flowId).append("\nTrace map: ").append(traceMap).append("\nHistory:");
      for (final FlowEvent<T> event : flowEvents) {
        sb.append("\n").append(event);
      }
      return sb.toString();
    }
  }
}
