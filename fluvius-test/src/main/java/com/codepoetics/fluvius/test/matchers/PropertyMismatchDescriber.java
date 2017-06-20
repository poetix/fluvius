package com.codepoetics.fluvius.test.matchers;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

import static com.codepoetics.fluvius.test.matchers.IndentationControl.*;

final class PropertyMismatchDescriber {
  private final Description description;
  private boolean matches = true;

  PropertyMismatchDescriber(Description description) {
    this.description = description;
  }

  public <T> PropertyMismatchDescriber check(String propertyName, T propertyValue, Matcher<? super T> matcher) {
    if (matcher == null) {
      return this;
    }
    if (matcher.matches(propertyValue)) {
      return this;
    }

    matches = false;

    newline(description).appendText(propertyName).appendText(": ");
    indent();
    matcher.describeMismatch(propertyValue, description);
    outdent();

    return this;
  }

  public boolean result() {
    return matches;
  }
}
