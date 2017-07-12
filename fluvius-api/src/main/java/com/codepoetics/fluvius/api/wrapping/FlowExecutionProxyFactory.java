package com.codepoetics.fluvius.api.wrapping;

import com.codepoetics.fluvius.api.Flow;
import com.codepoetics.fluvius.api.functional.Returning;

/**
 * A factory which creates proxies for {@link Flow} execution.
 */
public interface FlowExecutionProxyFactory {

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
