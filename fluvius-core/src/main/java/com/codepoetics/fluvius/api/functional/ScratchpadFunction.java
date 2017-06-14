package com.codepoetics.fluvius.api.functional;

import com.codepoetics.fluvius.api.scratchpad.Scratchpad;

/**
 * A function which takes a Scratchpad and returns a value of some type.
 *
 * @param <T> The type of the returned value.
 */
public interface ScratchpadFunction<T> extends F1<Scratchpad, T> {
}
