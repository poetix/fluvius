package com.codepoetics.fluvius.wrapping;

import com.codepoetics.fluvius.api.functional.ScratchpadFunction;
import com.codepoetics.fluvius.api.scratchpad.Key;
import com.codepoetics.fluvius.api.scratchpad.Scratchpad;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

final class MethodDispatchingScratchpadFunction<OUTPUT> implements ScratchpadFunction<OUTPUT> {
  private final Key<?>[] inputKeys;
  private final Method stepMethod;
  private final Object target;

  MethodDispatchingScratchpadFunction(Key<?>[] inputKeys, Method stepMethod, Object target) {
    this.inputKeys = inputKeys;
    this.stepMethod = stepMethod;
    this.target = target;
  }

  @SuppressWarnings("unchecked")
  @Override
  public OUTPUT apply(Scratchpad input) throws Exception {
    Object[] inputValues = new Object[inputKeys.length];
    Class<?>[] parameterTypes = stepMethod.getParameterTypes();

    for (int i = 0; i < inputKeys.length; i++) {
      inputValues[i] = (parameterTypes[i].equals(Exception.class))
          ? input.getFailureReason(inputKeys[i])
          : input.get(inputKeys[i]);
    }

    try {
      return (OUTPUT) stepMethod.invoke(target, inputValues);
    } catch (InvocationTargetException e) {
      if (e.getCause() instanceof  Exception) {
        throw Exception.class.cast(e.getCause());
      } else {
        throw e;
      }
    }
  }
}
