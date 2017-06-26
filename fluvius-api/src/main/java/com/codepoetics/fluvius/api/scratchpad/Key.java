package com.codepoetics.fluvius.api.scratchpad;

import com.codepoetics.fluvius.preconditions.Preconditions;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * A Key identifies a value written into a Scratchpad, and is used to construct new key/value pairs to be added to a Scratchpad.
 * <p>
 *   Values may be either values of the specified type, or failures for which a {@link Throwable} reason is given.
 * </p>
 * @param <T> The type of the value indexed by this Key.
 */
public final class Key<T> implements Serializable {

  /**
   * Create a Key with the given name.
   *
   * @param name The name of the Key to create.
   * @param <T>  The type of the Key to create.
   * @return The created Key.
   */
  public static <T> Key<T> named(String name) {
    return new Key<>(UUID.randomUUID(), name);
  }

  private final UUID id;
  private final String name;

  Key(UUID id, String name) {
    this.id = id;
    this.name = name;
  }

  /**
   * Get the name of this Key.
   *
   * @return The name of this Key.
   */
  public String getName() {
    return name;
  }

  /**
   * Construct a key/value pair using this Key and the supplied value.
   *
   * @param value The value to associate with this Key.
   * @return The constructed key/value pair.
   */
  public KeyValue of(T value) {
    return new KeyValue(this, Preconditions.checkNotNull("value", value));
  }

  /**
   * Construct a key/value pair using this key and the supplied failure reason.
   *
   * @param reason The failure reason to associate with this key.
   * @return The constructed key/value pair
   */
  public KeyValue ofFailure(Throwable reason) {
    return new KeyValue(this, reason);
  }

  @Override
  public boolean equals(Object other) {
    return other == this
        || (other instanceof Key
        && ((Key) other).id.equals(id));
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, id);
  }

  @Override
  public String toString() {
    return name;
  }
}
