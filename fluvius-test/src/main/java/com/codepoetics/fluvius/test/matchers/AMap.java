package com.codepoetics.fluvius.test.matchers;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import java.util.HashMap;
import java.util.Map;

public final class AMap<K, V> extends TypeSafeDiagnosingMatcher<Map<K, V>> {

  public static <K, V> AMap<K, V> of(Class<K> keyClass, Class<V> valueClass) {
    return new AMap<>(new HashMap<K, Matcher<? super V>>());
  }

  public static <K, V> AMap<K, V> containing(K key, Matcher<? super V> valueMatcher) {
    Map<K, Matcher<? super V>> expectedContents = new HashMap<>();
    expectedContents.put(key, valueMatcher);
    return new AMap<>(expectedContents);
  }

  public static <K, V> AMap<K, V> containing(Map<? extends K, ? extends V> values) {
    Map<K, Matcher<? super V>> expectedContents = toExpectationMap(values);
    return new AMap<>(expectedContents);
  }

  private static <K, V> Map<K, Matcher<? super V>> toExpectationMap(Map<? extends K, ? extends V> values) {
    Map<K, Matcher<? super V>> expectedContents = new HashMap<>();
    for (Map.Entry<? extends K, ? extends V> entry : values.entrySet()) {
      expectedContents.put(entry.getKey(), Matchers.equalTo(entry.getValue()));
    }
    return expectedContents;
  }

  private final Map<K, Matcher<? super V>> expectedContents;

  public AMap<K, V> with(K key, V value) {
    return with(key, Matchers.equalTo(value));
  }

  public AMap<K, V> with(K key, Matcher<? super V> valueMatcher) {
    expectedContents.put(key, valueMatcher);
    return this;
  }

  public AMap<K, V> withValues(Map<? extends K, ? extends V> additionalValues) {
    return with(toExpectationMap(additionalValues));
  }

  public AMap<K, V> with(Map<? extends K, Matcher<? super V>> additionalMatchers) {
    expectedContents.putAll(additionalMatchers);
    return this;
  }

  private AMap(Map<K, Matcher<? super V>> expectedContents) {
    this.expectedContents = expectedContents;
  }

  @Override
  protected boolean matchesSafely(Map<K, V> kvMap, Description description) {
    boolean matches = true;

    for (Map.Entry<K, Matcher<? super V>> entry : expectedContents.entrySet()) {
      if (!entry.getValue().matches(kvMap.get(entry.getKey()))) {
        matches = false;
        IndentationControl.newline(description).appendText(entry.getKey().toString()).appendText(": ");
        entry.getValue().describeMismatch(kvMap.get(entry.getKey()), description);
      }
    }

    return matches;
  }

  @Override
  public void describeTo(Description description) {
    if (expectedContents.isEmpty()) {
      description.appendText("A map");
      return;
    }

    description.appendText("A map with:");
    IndentationControl.indent();
    for (Map.Entry<K, Matcher<? super V>> entry : expectedContents.entrySet()) {
      IndentationControl.newline(description).appendText(entry.getKey().toString()).appendText(": ").appendDescriptionOf(entry.getValue());
    }
    IndentationControl.outdent();
  }
}
