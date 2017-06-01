package com.codepoetics.fluvius.api.scratchpad;

import java.io.Serializable;

public interface Key<T> extends Serializable {
    String getName();
    KeyValue of(T value);
}
