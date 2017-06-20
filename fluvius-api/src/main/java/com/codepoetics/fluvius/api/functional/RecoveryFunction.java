package com.codepoetics.fluvius.api.functional;

/**
 * A function used to recover from an exception
 *
 * @param <OUTPUT> The type of the function's result.
 */
public interface RecoveryFunction<OUTPUT> extends F1<Exception, OUTPUT> {
}
