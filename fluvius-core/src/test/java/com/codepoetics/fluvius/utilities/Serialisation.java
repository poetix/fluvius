package com.codepoetics.fluvius.utilities;

import java.io.*;

public final class Serialisation {

  private Serialisation() {
  }

  public static byte[] serialize(Object input) {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    try (ObjectOutput out = new ObjectOutputStream(bos)) {
      out.writeObject(input);
      out.flush();
      return bos.toByteArray();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @SuppressWarnings("unchecked")
  public static <T> T deserialize(byte[] input) {
    try (ByteArrayInputStream bis = new ByteArrayInputStream(input)) {
      ObjectInputStream in = new ObjectInputStream(bis);
      return (T) in.readObject();
    } catch (IOException | ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  public static <T> T roundtrip(T input) {
    return deserialize(serialize(input));
  }
}
