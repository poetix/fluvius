package com.codepoetics.fluvius.api.functional;

import java.io.Serializable;

public interface F1<I, O> extends Serializable {
    O apply(I input);
}
