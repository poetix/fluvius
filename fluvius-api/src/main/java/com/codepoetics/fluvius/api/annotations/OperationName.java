package com.codepoetics.fluvius.api.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to annotate a class presenting a {@link StepMethod} with the name of the {@link com.codepoetics.fluvius.api.Operation} to be created by wrapping it.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface OperationName {
  /**
   * The name of the {@link com.codepoetics.fluvius.api.Operation}.
   * @return The name of the {@link com.codepoetics.fluvius.api.Operation}.
   */
  String value();
}
