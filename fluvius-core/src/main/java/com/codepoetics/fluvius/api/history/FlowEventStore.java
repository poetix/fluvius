package com.codepoetics.fluvius.api.history;

import java.util.List;
import java.util.UUID;

public interface FlowEventStore<T> {
  void storeEvent(FlowEvent<T> event);
  List<FlowEvent<T>> retrieveEvents(UUID flowID);
}
