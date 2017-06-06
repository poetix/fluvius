package com.codepoetics.fluvius.scratchpad;

import com.codepoetics.fluvius.api.scratchpad.Key;
import com.codepoetics.fluvius.api.scratchpad.KeyValue;
import com.codepoetics.fluvius.api.scratchpad.Scratchpad;
import com.codepoetics.fluvius.api.scratchpad.ScratchpadStorage;
import com.codepoetics.fluvius.preconditions.Preconditions;

import java.util.LinkedHashMap;
import java.util.Map;

final class RealScratchpad implements Scratchpad {

  static Scratchpad create(final KeyValue... keyValues) {
    return new RealScratchpad(addValuesToMap(new LinkedHashMap<Key<?>, Object>(keyValues.length), keyValues));
  }

  private static Map<Key<?>, Object> addValuesToMap(final Map<Key<?>, Object> map, final KeyValue... keyValues) {
    ScratchpadStorage storage = new ScratchpadStorage() {
      @Override
      public <T> void put(final Key<T> key, final T value) {
        map.put(key, value);
      }
    };
    for (KeyValue keyValue : keyValues) {
      keyValue.store(storage);
    }
    return map;
  }

  private final Map<Key<?>, Object> storage;

  private RealScratchpad(final Map<Key<?>, Object> storage) {
    this.storage = storage;
  }

  @Override
  public boolean containsKey(final Key<?> key) {
    return storage.containsKey(key);
  }

  @Override
  public Scratchpad with(final KeyValue... keyValues) {
    return new RealScratchpad(
        addValuesToMap(
            new LinkedHashMap<>(storage), keyValues));
  }

  @Override
  public <T> T get(final Key<T> key) {
    return Preconditions.checkNotNull("value of key " + key.getName(), (T) storage.get(key));
  }

  @Override
  public boolean equals(final Object other) {
    return this == other
        || (other instanceof RealScratchpad
            && ((RealScratchpad) other).storage.equals(storage));
  }

  @Override
  public int hashCode() {
    return storage.hashCode();
  }

  @Override
  public String toString() {
    return storage.toString();
  }

}
