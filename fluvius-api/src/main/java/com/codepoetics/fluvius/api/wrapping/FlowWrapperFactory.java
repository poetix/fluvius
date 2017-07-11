package com.codepoetics.fluvius.api.wrapping;

import com.codepoetics.fluvius.api.Flow;
import com.codepoetics.fluvius.api.functional.Returning;

/**
 * A factory which converts objects with methods annotated with {@link com.codepoetics.fluvius.api.annotations.StepMethod} into {@link Flow}s, and creates proxies for flow execution.
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

  /**
   * Create a proxy of the provided class for executing the provided {@link Flow}.
   * @param functionClass The class to create a proxy for.
   * @param flow The flow to wrap.
   * @param <OUTPUT> The type of the value returned by the function / flow.
   * @param <T> The type of the class to proxy.
   * @return The constructed proxy.
   */
  <OUTPUT, T extends Returning<OUTPUT>> T proxyFor(Class<T> functionClass, Flow<OUTPUT> flow);

}
