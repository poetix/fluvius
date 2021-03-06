package com.codepoetics.fluvius.scratchpad;

import com.codepoetics.fluvius.api.scratchpad.Key;
import com.codepoetics.fluvius.api.scratchpad.KeyValue;
import com.codepoetics.fluvius.api.scratchpad.ScratchpadStorage;
import com.codepoetics.fluvius.utilities.Serialisation;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class KeyTests {

  private static final Key<Integer> age = Key.named("age");

  @Test
  public void keysHaveNames() {
    assertEquals("age", age.getName());
  }

  @Test
  public void keysCreateKeyValues() {
    ScratchpadStorage storage = mock(ScratchpadStorage.class);

    KeyValue value = age.of(42);
    value.store(storage);

    verify(storage).storeSuccess(age, 42);
  }

  @Test(expected = NullPointerException.class)
  public void keyValuesCannotBeNull() {
    age.of(null);
  }

  @Test
  public void keysWithSameNameAreNotEqual() {
    Key<String> costA = Key.named("cost");
    Key<Double> costB = Key.named("cost");

    assertNotEquals(costA, costB);
  }

  @Test
  public void keysAreSerialisable() {
    assertEquals(age, Serialisation.roundtrip(age));
  }

}
