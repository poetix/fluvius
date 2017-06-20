package com.codepoetics.fluvius.test.matchers;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import java.util.HashMap;
import java.util.Map;

public class RecordingMatcher {

  private final Map<String, Object> recordedValues = new HashMap<>();

  public <T> Matcher<T> record(final String name) {
    return new BaseMatcher<T>() {
      @Override
      public void describeTo(Description description) {
        description.appendText("(recorded as ").appendValue(name).appendText(")");
      }

      @Override
      public boolean matches(Object o) {
        recordedValues.put(name, o);
        return true;
      }
    };
  }

  public <T> Matcher<T> equalsRecorded(final String name) {
    return new BaseMatcher<T>() {
      @Override
      public boolean matches(Object o) {
        return recordedValues.containsKey(name) && recordedValues.get(name).equals(o);
      }

      @Override
      public void describeTo(Description description) {
        description.appendText("equals value recorded as ").appendValue(name);
        if (recordedValues.containsKey(name)) {
          description.appendText(" (").appendValue(recordedValues.get(name)).appendText(")");
        } else {
          description.appendText(" (no value recorded)");
        }
      }
    };
  }
}
