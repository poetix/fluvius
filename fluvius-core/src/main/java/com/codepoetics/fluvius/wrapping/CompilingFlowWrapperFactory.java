package com.codepoetics.fluvius.wrapping;

import com.codepoetics.fluvius.api.Flow;
import com.codepoetics.fluvius.api.compilation.FlowCompiler;
import com.codepoetics.fluvius.api.functional.Returning;
import com.codepoetics.fluvius.api.scratchpad.KeyProvider;
import com.codepoetics.fluvius.api.wrapping.FlowWrapperFactory;
import com.codepoetics.fluvius.flows.Flows;
import com.codepoetics.fluvius.scratchpad.Keys;

import java.lang.reflect.Proxy;

public final class CompilingFlowWrapperFactory implements FlowWrapperFactory {

  public static FlowWrapperFactory with(FlowCompiler flowCompiler) {
    return with(flowCompiler, Keys.createProvider());
  }

  public static FlowWrapperFactory with(FlowCompiler flowCompiler, KeyProvider keyProvider) {
    return new CompilingFlowWrapperFactory(flowCompiler, keyProvider);
  }

  private final FlowCompiler compiler;
  private final KeyProvider keyProvider;

  private CompilingFlowWrapperFactory(FlowCompiler compiler, KeyProvider keyProvider) {
    this.compiler = compiler;
    this.keyProvider = keyProvider;
  }

  @Override
  public <OUTPUT, T extends Returning<OUTPUT>> Flow<OUTPUT> flowFor(T function) {
    FunctionInfo<OUTPUT> functionInfo = FunctionInfo.forFunction(function, keyProvider);

    return Flows.from(functionInfo.getInputKeys())
        .to(functionInfo.getOutputKey())
        .using(functionInfo.createOperation());
  }

  @Override
  public <OUTPUT, T extends Returning<OUTPUT>> T proxyFor(Class<T> functionClass, Flow<OUTPUT> flow) {
    return functionClass.cast(Proxy.newProxyInstance(
        functionClass.getClassLoader(),
        new Class<?>[] { functionClass},
        new FlowRunningInvocationHandler(compiler.compile(flow), keyProvider)));
  }

}
