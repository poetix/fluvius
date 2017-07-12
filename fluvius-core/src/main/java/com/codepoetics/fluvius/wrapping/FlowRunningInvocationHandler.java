package com.codepoetics.fluvius.wrapping;

import com.codepoetics.fluvius.api.FlowExecution;
import com.codepoetics.fluvius.api.FlowResultCallback;
import com.codepoetics.fluvius.api.scratchpad.Key;
import com.codepoetics.fluvius.api.scratchpad.KeyProvider;
import com.codepoetics.fluvius.api.scratchpad.KeyValue;
import com.codepoetics.fluvius.api.wrapping.FlowRunner;
import com.codepoetics.fluvius.exceptions.FlowExecutionException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;

final class FlowRunningInvocationHandler implements InvocationHandler {

  private final FlowExecution<?> execution;
  private final KeyProvider keyProvider;

  FlowRunningInvocationHandler(FlowExecution<?> execution, KeyProvider keyProvider) {
    this.execution = execution;
    this.keyProvider = keyProvider;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    if (method.getDeclaringClass().isAssignableFrom(FlowRunningInvocationHandler.class)) {
      return method.invoke(this, args);
    }

    KeyValue[] keyValues = new KeyValue[args.length];
    Key[] keys = Reflection.getParameterKeys(method, keyProvider);
    for (int i = 0; i < args.length; i++) {
      keyValues[i] = keys[i].of(args[i]);
    }

    if (FlowRunner.class.isAssignableFrom(method.getReturnType())) {
      return new ProxyFlowRunner<>(execution, keyValues);
    }

    return execution.run(keyValues);
  }

  private static final class ProxyFlowRunner<T> implements FlowRunner<T> {

    private final FlowExecution<T> flowExecution;
    private final KeyValue[] keyValues;

    private ProxyFlowRunner(FlowExecution<T> flowExecution, KeyValue[] keyValues) {
      this.flowExecution = flowExecution;
      this.keyValues = keyValues;
    }

    @Override
    public T run() {
      try {
        return flowExecution.run(keyValues);
      } catch (Exception e) {
        throw new FlowExecutionException(e);
      }
    }

    @Override
    public T run(UUID flowId) {
      try {
        return flowExecution.run(flowId, keyValues);
      } catch (Exception e) {
        throw new FlowExecutionException(e);
      }
    }

    @Override
    public Runnable asAsync(FlowResultCallback<T> callback) {
      return flowExecution.asAsync(callback, keyValues);
    }

    @Override
    public Runnable asAsync(UUID flowId, FlowResultCallback<T> callback) {
      return flowExecution.asAsync(flowId, callback, keyValues);
    }
  }
}
