package com.codepoetics.fluvius.wrapping;

import com.codepoetics.fluvius.api.annotations.KeyName;
import com.codepoetics.fluvius.api.annotations.OperationName;
import com.codepoetics.fluvius.api.annotations.StepMethod;
import com.codepoetics.fluvius.api.scratchpad.Key;
import com.codepoetics.fluvius.api.scratchpad.KeyProvider;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

final class Reflection {

  private Reflection() {
  }

  static Key[] getParameterKeys(Method method, KeyProvider keyProvider) {
    Annotation[][] parameterAnnotations = method.getParameterAnnotations();
    Type[] parameterTypes = method.getGenericParameterTypes();
    Class<?>[] parameterClasses = method.getParameterTypes();

    Key[] keys = new Key[parameterAnnotations.length];

    for (int i = 0; i < parameterAnnotations.length; i++) {
      String keyName = getKeyName(parameterAnnotations[i]);
      if (keyName == null) {
        keyName = Naming.getKeyName(parameterClasses[i].getSimpleName());
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

  static String getOperationName(Method stepMethod) {
    Class<?> declaringClass = stepMethod.getDeclaringClass();

    return declaringClass.isAnnotationPresent(OperationName.class)
      ? declaringClass.getAnnotation(OperationName.class).value()
      : Naming.getOperationName(declaringClass.getSimpleName());
  }

  static Method getStepMethod(Class<?> functionClass) {
    for (Method method : getMethods(functionClass)) {
      if (method.isAnnotationPresent(StepMethod.class)) {
        return method;
      }
    }

    throw new IllegalArgumentException("Class " + functionClass + " has no method annotated as @StepMethod");
  }

  private static List<Class<?>> getClassHierarchy(Class<?> inheritor) {
    List<Class<?>> result = new ArrayList<>();
    result.add(inheritor);

    Class<?> superclass = inheritor.getSuperclass();
    while (superclass != null) {
      result.add(superclass);
      superclass = superclass.getSuperclass();
    }

    for (Class<?> iface : inheritor.getInterfaces()) {
      result.add(iface);
    }

    return result;
  }

  private static List<Method> getMethods(Class<?> inheritor) {
    List<Method> result = new ArrayList<>();

    for (Class<?> klass : getClassHierarchy(inheritor)) {
      for (Method method : klass.getDeclaredMethods()) {
        result.add(method);
      }
    }

    return result;
  }

  static <OUTPUT> Key<OUTPUT> getOutputKey(Method stepMethod, KeyProvider keyProvider) {
    String annotatedName = stepMethod.getAnnotation(StepMethod.class).value();
    String calculatedName = annotatedName.isEmpty()
        ? Naming.getKeyName(stepMethod.getReturnType().getSimpleName())
        : annotatedName;

    return keyProvider.getKey(
        calculatedName,
        stepMethod.getGenericReturnType());
  }
}
