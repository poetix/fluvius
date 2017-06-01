package com.codepoetics.fluvius.api.functional;

import java.io.Serializable;

public interface F2<I1, I2, O> extends Serializable {
    O apply(I1 first, I2 second);
}
