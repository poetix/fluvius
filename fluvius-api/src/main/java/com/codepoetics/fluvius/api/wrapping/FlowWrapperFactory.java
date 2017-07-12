package com.codepoetics.fluvius.api.wrapping;

import com.codepoetics.fluvius.api.Flow;
import com.codepoetics.fluvius.api.functional.Returning;

/**
 * A factory which converts objects with methods annotated with {@link com.codepoetics.fluvius.api.annotations.StepMethod} into {@link Flow}s.
 */
public interface FlowWrapperFactory {

  /**
   * Create a {@link Flow} from the provided function.
   * @param function The function to wrap as a single-step flow.
   * @param <OUTPUT> The type of the value returned by the function/flow.
   * @param <T> The type of the function.
   * @return The constructed flow.
   */
  <OUTPUT, T extends Returning<OUTPUT>> Flow<OUTPUT> flowFor(T function);

}
