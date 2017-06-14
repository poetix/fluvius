package com.codepoetics.fluvius.api.history;

import java.util.Map;

public interface StepStartedEvent<T> extends FlowEvent<T> {
  Map<String, T> getScratchpadState();
}
