package com.codepoetics.fluvius.api;

public interface Condition {
    String getDescription();
    boolean test(Scratchpad scratchpad);
}
