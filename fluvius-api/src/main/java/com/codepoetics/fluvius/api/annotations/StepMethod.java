package com.codepoetics.fluvius.api.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to annotate a method which defines a step to be carried out in a flow. This will be wrapped by a {@link com.codepoetics.fluvius.api.wrapping.FlowWrapperFactory} into an {@link com.codepoetics.fluvius.api.Operation} carried out by a single-step {@link com.codepoetics.fluvius.api.Flow}
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface StepMethod {
  /**
   * The name of the {@link com.codepoetics.fluvius.api.scratchpad.Scratchpad} key to which the return-value of the method will be bound.
   * @return The name of the {@link com.codepoetics.fluvius.api.scratchpad.Scratchpad} key to which the return-value of the method will be bound.
   */
  String value() default "";
}
