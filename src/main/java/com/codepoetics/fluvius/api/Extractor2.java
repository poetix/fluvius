package com.codepoetics.fluvius.api;

public interface Extractor2<I1, I2, O> {
    O extract(I1 first, I2 second);
}
