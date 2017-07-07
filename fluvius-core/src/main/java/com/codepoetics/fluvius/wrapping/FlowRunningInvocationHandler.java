package com.codepoetics.fluvius.wrapping;

import com.codepoetics.fluvius.api.FlowExecution;
import com.codepoetics.fluvius.api.scratchpad.Key;
import com.codepoetics.fluvius.api.scratchpad.KeyProvider;
import com.codepoetics.fluvius.api.scratchpad.KeyValue;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

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

    return execution.run(keyValues);
  }
}
