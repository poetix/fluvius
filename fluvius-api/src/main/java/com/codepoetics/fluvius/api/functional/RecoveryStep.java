package com.codepoetics.fluvius.api.functional;

/**
 * A function used to recover from an exception
 *
 * @param <OUTPUT> The type of the function's result.
 */
public interface RecoveryStep<OUTPUT> extends SingleParameterStep<Exception, OUTPUT> {
}
