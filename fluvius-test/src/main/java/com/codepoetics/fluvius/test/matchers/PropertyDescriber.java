package com.codepoetics.fluvius.test.matchers;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

import static com.codepoetics.fluvius.test.matchers.IndentationControl.newline;

final class PropertyDescriber {

  private boolean first = true;
  private final Description description;

  PropertyDescriber(Description description) {
    this.description = description;
  }

  public PropertyDescriber describeProperty(String name, Matcher<?> matcher) {
    if (matcher == null) {
      return this;
    }

    if (first) {
      description.appendText(" with:");
      first = false;
    }

    newline(description).appendText(name).appendText(": ");
    matcher.describeTo(description);
    return this;
  }

  public Description getDescription() {
    return description;
  }
}
