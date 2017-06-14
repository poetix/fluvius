package com.codepoetics.fluvius.api.history;

import java.util.UUID;

public interface FlowEvent<T> {
  UUID getFlowId();
  UUID getStepId();
  long getTimestamp();
  <V> V translate(FlowEventTranslator<T, V> translator);
}
