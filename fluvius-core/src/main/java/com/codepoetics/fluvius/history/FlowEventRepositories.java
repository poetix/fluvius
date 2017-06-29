package com.codepoetics.fluvius.history;

import com.codepoetics.fluvius.api.history.*;
import com.codepoetics.fluvius.api.tracing.TraceMap;

/**
 * Utility class for creating {@link FlowEventRepository}s out of smaller pieces.
 */
public final class FlowEventRepositories {

  private FlowEventRepositories() {
  }

  /**
   * Create an in-memory {@link FlowEventRepository} using the provided {@link EventDataSerialiser} to serialise flow event data.
   *
   * @param serialiser The serialiser to use to serialise flow event data.
   * @param <T> The type to which flow event data will be serialised.
   * @return The constructed flow event repository.
   */
  public static <T> FlowEventRepository<T> createInMemory(EventDataSerialiser<T> serialiser) {
    return create(InMemoryFlowEventStore.<T>create(), serialiser);
  }


  public static <T> FlowEventRepository<T> create(
      FlowEventStore<T> eventStore,
      EventDataSerialiser<T> serialiser) {
    return FlowEventRepository.using(eventStore, serialiser);
  }

}
