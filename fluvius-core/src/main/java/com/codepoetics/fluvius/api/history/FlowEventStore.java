package com.codepoetics.fluvius.api.history;

import java.util.List;
import java.util.UUID;

/**
 * A store of flow events.
 *
 * @param <T> The type to which the flow events' data is serialised.
 */
public interface FlowEventStore<T> {

  /**
   * Store a flow event.
   *
   * @param event The event to store.
   */
  void storeEvent(FlowEvent<T> event);

  /**
   * Retrieve all of the events associated with a particular flow.
   *
   * @param flowId The id of the flow to retrieve events for.
   * @return The events stored for the requested flow.
   */
  List<FlowEvent<T>> retrieveEvents(UUID flowId);

}
