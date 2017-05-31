package com.codepoetics.fluvius.api;

public interface Extractor<I, O> {
    O extract(I input);
}
