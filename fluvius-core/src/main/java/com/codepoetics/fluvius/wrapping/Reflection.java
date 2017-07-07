package com.codepoetics.fluvius.wrapping;

import com.codepoetics.fluvius.api.annotations.KeyName;
import com.codepoetics.fluvius.api.annotations.OperationName;
import com.codepoetics.fluvius.api.annotations.StepMethod;
import com.codepoetics.fluvius.api.scratchpad.Key;
import com.codepoetics.fluvius.api.scratchpad.KeyProvider;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

final class Reflection {

  private Reflection() {
  }

  static Key[] getParameterKeys(Method method, KeyProvider keyProvider) {
    Annotation[][] parameterAnnotations = method.getParameterAnnotations();
    Type[] parameterTypes = method.getGenericParameterTypes();
    Key[] keys = new Key[parameterAnnotations.length];

    for (int i = 0; i < keys.length; i++) {
      String keyName = getKeyName(parameterAnnotations[i]);
      if (keyName == null) {
        throw new IllegalArgumentException("Parameter " + i + " of method " + method + " has no @KeyName annotation");
      }
      keys[i] = keyProvider.getKey(keyName, parameterTypes[i]);
    }

    return keys;
  }

  private static String getKeyName(Annotation[] parameterAnnotations) {
    for (Annotation annotation : parameterAnnotations) {
      if (annotation instanceof KeyName) {
        return (KeyName.class.cast(annotation).value());
      }
    }
    return null;
  }

  static String getOperationName(Class<?> functionClass) {
    if (functionClass.isAnnotationPresent(OperationName.class)) {
      return functionClass.getAnnotation(OperationName.class).value();
    }
    return functionClass.getSimpleName();
  }

  static Method getStepMethod(Class<?> functionClass) {
    Method[] declaredMethods = functionClass.getDeclaredMethods();

    for (Method declaredMethod : declaredMethods) {
      if (declaredMethod.isAnnotationPresent(StepMethod.class)) {
        return declaredMethod;
      }
    }

    throw new IllegalArgumentException("Class " + functionClass + " has no method annotated as @StepMethod");
  }

  static <OUTPUT> Key<OUTPUT> getOutputKey(Method stepMethod, KeyProvider keyProvider) {
    return keyProvider.getKey(
        stepMethod.getAnnotation(StepMethod.class).value(),
        stepMethod.getGenericReturnType());
  }
}
