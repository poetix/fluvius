package com.codepoetics.fluvius.wrapping;

import com.codepoetics.fluvius.api.Flow;
import com.codepoetics.fluvius.api.Operation;
import com.codepoetics.fluvius.api.functional.Returning;
import com.codepoetics.fluvius.api.functional.ScratchpadFunction;
import com.codepoetics.fluvius.api.scratchpad.Key;
import com.codepoetics.fluvius.api.scratchpad.KeyProvider;
import com.codepoetics.fluvius.api.wrapping.FlowWrapperFactory;
import com.codepoetics.fluvius.flows.Flows;
import com.codepoetics.fluvius.operations.Operations;

import java.lang.reflect.Method;

final class ReflectingFlowWrapperFactory implements FlowWrapperFactory {

  private final KeyProvider keyProvider;

  ReflectingFlowWrapperFactory(KeyProvider keyProvider) {
    this.keyProvider = keyProvider;
  }

  @Override
  public <OUTPUT, T extends Returning<OUTPUT>> Flow<OUTPUT> flowFor(T function) {
    Class<?> functionClass = function.getClass();

    Method stepMethod = Reflection.getStepMethod(functionClass);
    String name = Reflection.getOperationName(stepMethod);

    Key<?>[] inputKeys = Reflection.getParameterKeys(stepMethod, keyProvider);
    Key<OUTPUT> outputKey = Reflection.getOutputKey(stepMethod, keyProvider);

    ScratchpadFunction<OUTPUT> scratchpadFunction = new MethodDispatchingScratchpadFunction<>(inputKeys, stepMethod, function);

    return Flows.from(inputKeys).to(outputKey).using(Operations.fromFunction(name, scratchpadFunction));
  }
}
