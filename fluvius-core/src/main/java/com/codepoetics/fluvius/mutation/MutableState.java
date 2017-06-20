package com.codepoetics.fluvius.mutation;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Records the mutable state of an object, so that it can be compared before and after an operation.
 */
public final class MutableState {

  private MutableState() {
  }

  /**
   * Obtain an equality-testable representation of the mutable state of the supplied object.
   * @param object The object to extract the mutable state from.
   * @return An equality-testable representation of the mutable state of the supplied object.
   */
  public static Object of(Object object) {
    if (isScalar(object)) {
      return object;
    }
    if (object instanceof Collection) {
      return ofCollection((Collection<?>) object);
    }
    if (object instanceof Map) {
      return ofMap((Map<?, ?>) object);
    }
    if (object.getClass().isArray()) {
      return ofArray((Object[]) object);
    }
    if (definesEquals(object)) {
      return object;
    }
    if (hasGettableProperties(object)) {
      return ofBeanLike(object);
    }
    return object;
  }

  private static boolean isScalar(Object object) {
    return object == null
        || object instanceof String
        || object instanceof Character
        || object instanceof Byte
        || object instanceof Integer
        || object instanceof Short
        || object instanceof Long
        || object instanceof Float
        || object instanceof Double;
  }

  private static Object ofCollection(Collection<?> collection) {
    List<Object> contents = new ArrayList<>(collection.size());
    for (Object item : collection) {
      contents.add(MutableState.of(item));
    }
    return contents;
  }

  private static Object ofMap(Map<?, ?> map) {
    Map<Object, Object> contents = new HashMap<>(map.size());
    for (Map.Entry<?, ?> entry : map.entrySet()) {
      contents.put(MutableState.of(entry.getKey()), MutableState.of(entry.getValue()));
    }
    return contents;
  }

  private static Object ofArray(Object[] array) {
    List<Object> contents = new ArrayList<>(array.length);
    for (Object item : array) {
      contents.add(MutableState.of(item));
    }
    return contents;
  }

  private static boolean definesEquals(Object object) {
    try {
      return !object.getClass().getMethod("equals", Object.class).getDeclaringClass().equals(Object.class);
    } catch (NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
  }

  private static boolean hasGettableProperties(Object object) {
    for (PropertyDescriptor descriptor : safeGetPropertyDescriptors(object)) {
      if (descriptor.getReadMethod() != null && !belongsToObject(descriptor)) {
        return true;
      }
    }
    return false;
  }

  private static PropertyDescriptor[] safeGetPropertyDescriptors(Object object)  {
    try {
      return Introspector.getBeanInfo(object.getClass()).getPropertyDescriptors();
    } catch (IntrospectionException e) {
      throw new RuntimeException(e);
    }
  }

  private static Object ofBeanLike(Object object) {
    Map<String, Object> propertyValues = new HashMap<>();
    for (PropertyDescriptor propertyDescriptor : safeGetPropertyDescriptors(object)) {
      if (propertyDescriptor.getReadMethod() == null
          || belongsToObject(propertyDescriptor)) {
        continue;
      }
      propertyValues.put(propertyDescriptor.getName(), MutableState.of(safeReadProperty(object, propertyDescriptor)));
    }
    return propertyValues;
  }

  private static boolean belongsToObject(PropertyDescriptor propertyDescriptor) {
    return propertyDescriptor.getReadMethod().getDeclaringClass().equals(Object.class);
  }

  private static Object safeReadProperty(Object object, PropertyDescriptor propertyDescriptor)  {
    try {
      return propertyDescriptor.getReadMethod().invoke(object);
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }


}
