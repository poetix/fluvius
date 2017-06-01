package com.codepoetics.fluvius.api.functional;

public interface Extractor<I, O> {
    O extract(I input);
}
