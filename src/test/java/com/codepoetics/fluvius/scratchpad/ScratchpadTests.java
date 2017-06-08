package com.codepoetics.fluvius.scratchpad;

import com.codepoetics.fluvius.api.scratchpad.Key;
import com.codepoetics.fluvius.api.scratchpad.Scratchpad;
import com.codepoetics.fluvius.utilities.Serialisation;
import org.junit.Test;

import static org.junit.Assert.*;

public class ScratchpadTests {

  private static final Key<String> name = Keys.named("name");
  private static final Key<Integer> age = Keys.named("age");
  private static final Key<String> favouriteColour = Keys.named("favouriteColour");

  @Test
  public void valuesWrittenToScratchpadOnInitialisationAreRetrievable() {
    Scratchpad scratchpad = Scratchpads.create(
        name.of("Arthur Putey"),
        age.of(42)
    );

    assertEquals("Arthur Putey", scratchpad.get(name));
    assertEquals((Integer) 42, scratchpad.get(age));
  }

  @Test
  public void scratchpadCanBeUpdatedWithNewValues() {
    Scratchpad scratchpad = Scratchpads.create(
        name.of("Arthur Putey"),
        age.of(42)
    );

    Scratchpad updated = scratchpad.with(age.of(43), favouriteColour.of("Blue"));

    assertEquals((Integer) 43, updated.get(age));
    assertEquals("Blue", updated.get(favouriteColour));
  }

  @Test
  public void updatesDoNotMutateOriginalScratchpad() {
    Scratchpad scratchpad = Scratchpads.create(
        name.of("Arthur Putey"),
        age.of(42)
    );

    Scratchpad updated = scratchpad.with(age.of(43), favouriteColour.of("Blue"));

    assertFalse(scratchpad.containsKey(favouriteColour));
    assertTrue(updated.containsKey(favouriteColour));
    assertEquals((Integer) 42, scratchpad.get(age));
  }

  @Test
  public void retrievalOfMissingKeyThrowsException() {
    Scratchpad scratchpad = Scratchpads.create(
        name.of("Arthur Putey"),
        age.of(42)
    );
    try {
      scratchpad.get(favouriteColour);
    } catch (NullPointerException e) {
      assertEquals("value of key favouriteColour must not be null", e.getMessage());
      return;
    }
    fail("Expected exception to be thrown");
  }

  @Test
  public void toStringReturnsStringRepresentationOfStorage() {
    Scratchpad scratchpad = Scratchpads.create(
        name.of("Arthur Putey"),
        age.of(42)
    );

    assertEquals("{name=Arthur Putey, age=42}", scratchpad.toString());
  }

  @Test
  public void scratchpadsWithSameContentsAreEqual() {
    assertEquals(
        Scratchpads.create(
            name.of("Arthur Putey"),
            age.of(42)
        ),
        Scratchpads.create(
            name.of("Arthur Putey"),
            age.of(42)
        )
    );
  }

  @Test
  public void scratchpadsWithDifferentContentsAreNotEqual() {
    assertNotEquals(
        Scratchpads.create(
            name.of("Arthur Putey"),
            age.of(42)
        ),
        Scratchpads.create(
            name.of("Arthur Putey"),
            age.of(43)
        )
    );
  }

  @Test
  public void scratchpadsAreSerialisable() {
    Scratchpad scratchpad = Scratchpads.create(
        name.of("Arthur Putey"),
        age.of(42)
    );

    assertEquals(scratchpad, Serialisation.roundtrip(scratchpad));
  }

  @Test(expected=IllegalArgumentException.class)
  public void keysCannotBeOverwrittenInLockedScratchpad() {
    Scratchpad locked = Scratchpads.create(name.of("Arthur Putey")).locked().with(age.of(42));
    assertEquals(Integer.valueOf(42), locked.get(age));

    // throws IllegalArgumentException
    locked.with(name.of("Peter Arthy"));
  }
}
