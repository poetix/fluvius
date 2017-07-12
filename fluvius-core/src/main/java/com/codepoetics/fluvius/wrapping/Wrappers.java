package com.codepoetics.fluvius.wrapping;

import com.codepoetics.fluvius.api.compilation.FlowCompiler;
import com.codepoetics.fluvius.api.scratchpad.KeyProvider;
import com.codepoetics.fluvius.api.wrapping.FlowExecutionProxyFactory;
import com.codepoetics.fluvius.api.wrapping.FlowWrapperFactory;

/**
 * Utility class providing implementations of {@link com.codepoetics.fluvius.api.wrapping.FlowWrapperFactory} and {@link com.codepoetics.fluvius.api.wrapping.FlowExecutionProxyFactory}.
 */
public final class Wrappers {

  private Wrappers() {
  }

  /**
   * Obtain a {@link FlowWrapperFactory} which issues keys using the supplied {@link KeyProvider}.
   *
   * @param keyProvider The key provider to issue keys with.
   * @return The constructed flow wrapper factory.
   */
  public static FlowWrapperFactory createWrapperFactory(KeyProvider keyProvider) {
    return new ReflectingFlowWrapperFactory(keyProvider);
  }

  /**
   * Obtain a {@link FlowExecutionProxyFactory} which compiles {@link com.codepoetics.fluvius.api.Flow}s using the supplied {@link FlowCompiler} and issues keys using the supplied {@link KeyProvider}.
   *
   * @param flowCompiler The compiler to compile flows with.
   * @param keyProvider The key provider to issue keys with.
   * @return The constructed flow execution proxy factory.
   */
  public static FlowExecutionProxyFactory createProxyFactory(FlowCompiler flowCompiler, KeyProvider keyProvider) {
    return new CompilingFlowExecutionProxyFactory(flowCompiler, keyProvider);
  }
}
