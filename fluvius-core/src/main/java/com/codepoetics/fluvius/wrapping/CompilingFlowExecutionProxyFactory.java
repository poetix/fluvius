package com.codepoetics.fluvius.wrapping;

import com.codepoetics.fluvius.api.Flow;
import com.codepoetics.fluvius.api.compilation.FlowCompiler;
import com.codepoetics.fluvius.api.functional.Returning;
import com.codepoetics.fluvius.api.scratchpad.KeyProvider;
import com.codepoetics.fluvius.api.wrapping.FlowExecutionProxyFactory;
import com.codepoetics.fluvius.scratchpad.Keys;

import java.lang.reflect.Proxy;

final class CompilingFlowExecutionProxyFactory implements FlowExecutionProxyFactory {

  private final FlowCompiler compiler;
  private final KeyProvider keyProvider;

  CompilingFlowExecutionProxyFactory(FlowCompiler compiler, KeyProvider keyProvider) {
    this.compiler = compiler;
    this.keyProvider = keyProvider;
  }

  @Override
  public <OUTPUT, T extends Returning<OUTPUT>> T proxyFor(Class<T> functionClass, Flow<OUTPUT> flow) {
    return functionClass.cast(Proxy.newProxyInstance(
        functionClass.getClassLoader(),
        new Class<?>[] { functionClass},
        new FlowRunningInvocationHandler(compiler.compile(flow), keyProvider)));
  }

}
