package com.codepoetics.fluvius.scratchpad;

import com.codepoetics.fluvius.api.scratchpad.Key;
import com.codepoetics.fluvius.api.scratchpad.KeyValue;
import com.codepoetics.fluvius.api.scratchpad.Scratchpad;
import com.codepoetics.fluvius.api.scratchpad.ScratchpadStorage;
import com.codepoetics.fluvius.exceptions.FailedKeyRetrievedException;
import com.codepoetics.fluvius.preconditions.Preconditions;

import java.util.LinkedHashMap;
import java.util.Map;

final class HashMapBackedScratchpad implements Scratchpad {

  static Scratchpad create(KeyValue... keyValues) {
    return new HashMapBackedScratchpad(false, addValuesToMap(false, new LinkedHashMap<Key<?>, Object>(keyValues.length), keyValues));
  }

  private static Map<Key<?>, Object> addValuesToMap(final boolean isLocked, final Map<Key<?>, Object> map, KeyValue... keyValues) {
    ScratchpadStorage storage = new ScratchpadStorage() {
      @Override
      public <T> void storeSuccess(Key<T> key, T value) {
        if (isLocked && map.containsKey(key)) {
          throw new IllegalArgumentException("Scratchpad is locked, cannot overwrite value for key " + key.getName());
        }
        map.put(key, value);
      }

      @Override
      public void storeFailure(Key<?> key, Throwable reason) {
        if (isLocked && map.containsKey(key)) {
          throw new IllegalArgumentException("Scratchpad is locked, cannot overwrite value for key " + key.getName());
        }
        map.put(key, reason);
      }
    };
    for (KeyValue keyValue : keyValues) {
      keyValue.store(storage);
    }
    return map;
  }

  private final boolean isLocked;
  private final Map<Key<?>, Object> storage;

  private HashMapBackedScratchpad(boolean isLocked, Map<Key<?>, Object> storage) {
    this.isLocked = isLocked;
    this.storage = storage;
  }

  @Override
  public Scratchpad locked() {
    return new HashMapBackedScratchpad(true, storage);
  }

  @Override
  public boolean containsKey(Key<?> key) {
    return storage.containsKey(key);
  }

  @Override
  public boolean isSuccessful(Key<?> key) {
    return !(Preconditions.checkNotNull("value of key " + key.getName(), storage.get(key)) instanceof Throwable);
  }

  @Override
  public Scratchpad with(KeyValue... keyValues) {
    return new HashMapBackedScratchpad(
        isLocked, addValuesToMap(
            isLocked, toMap(), keyValues));
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T get(Key<T> key) {
    Object valueAtKey = Preconditions.checkNotNull("value of key " + key.getName(), (T) storage.get(key));
    if (valueAtKey instanceof Throwable) {
      throw new FailedKeyRetrievedException(key.getName(), (Throwable) valueAtKey);
    }
    return (T) valueAtKey;
  }

  @Override
  public Exception getFailureReason(Key<?> key) {
    Object valueAtKey = Preconditions.checkNotNull("value of key " + key.getName(), storage.get(key));
    if (!(valueAtKey instanceof Throwable)) {
      throw new IllegalStateException("Attempted to retrieve failure reason for key '" + key.getName() + "', but recorded value is " + valueAtKey);
    }
    return (Exception) valueAtKey;
  }

  @Override
  public Map<Key<?>, Object> toMap() {
    return new LinkedHashMap<>(storage);
  }

  @Override
  public boolean equals(Object other) {
    return this == other
        || (other instanceof HashMapBackedScratchpad
            && ((HashMapBackedScratchpad) other).storage.equals(storage));
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
