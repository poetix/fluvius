package com.codepoetics.fluvius.wrapping;

import com.codepoetics.fluvius.api.Operation;
import com.codepoetics.fluvius.api.functional.Returning;
import com.codepoetics.fluvius.api.functional.ScratchpadFunction;
import com.codepoetics.fluvius.api.scratchpad.Key;
import com.codepoetics.fluvius.api.scratchpad.KeyProvider;
import com.codepoetics.fluvius.operations.Operations;

import java.lang.reflect.Method;

final class FunctionInfo<T> {

  static <OUTPUT, T extends Returning<OUTPUT>> FunctionInfo<OUTPUT> forFunction(T function, KeyProvider keyProvider) {
    Class<?> functionClass = function.getClass();

    String name = Reflection.getOperationName(functionClass);
    Method stepMethod = Reflection.getStepMethod(functionClass);

    Key<?>[] inputKeys = Reflection.getParameterKeys(stepMethod, keyProvider);
    Key<OUTPUT> outputKey = Reflection.getOutputKey(stepMethod, keyProvider);

    ScratchpadFunction<OUTPUT> scratchpadFunction = new MethodDispatchingScratchpadFunction<>(inputKeys, stepMethod, function);

    return new FunctionInfo<>(inputKeys, outputKey, name, scratchpadFunction);
  }


  private final Key<?>[] inputKeys;
  private final Key<T> outputKey;
  private final String name;
  private final ScratchpadFunction<T> scratchpadFunction;

  private FunctionInfo(Key<?>[] inputKeys, Key<T> outputKey, String name, ScratchpadFunction<T> scratchpadFunction) {
    this.inputKeys = inputKeys;
    this.outputKey = outputKey;
    this.name = name;
    this.scratchpadFunction = scratchpadFunction;
  }

  public Key<?>[] getInputKeys() {
    return inputKeys;
  }

  public Key<T> getOutputKey() {
    return outputKey;
  }

  public Operation<T> createOperation() {
    return Operations.fromFunction(name, scratchpadFunction);
  }

}
