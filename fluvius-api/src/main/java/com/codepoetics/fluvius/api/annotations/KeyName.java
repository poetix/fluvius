package com.codepoetics.fluvius.api.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to annotate parameters in a {@link StepMethod} which are bound to keys in a flow's {@link com.codepoetics.fluvius.api.scratchpad.Scratchpad}.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface KeyName {
  /**
   * The name of the key to which this parameter is bound.
   * @return The name of the key to which this parameter is bound.
   */
  String value();

}
