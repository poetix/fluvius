package com.codepoetics.fluvius.api.functional;

import java.io.Serializable;

public interface P1<T> extends Serializable {
    boolean test(T value);
}
