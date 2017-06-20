package com.codepoetics.fluvius.test.matchers;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import static com.codepoetics.fluvius.test.matchers.IndentationControl.indent;
import static com.codepoetics.fluvius.test.matchers.IndentationControl.newline;
import static com.codepoetics.fluvius.test.matchers.IndentationControl.outdent;

abstract class BasePropertyMatcher<T> extends TypeSafeDiagnosingMatcher<T> {

  private final String entityType;

  protected BasePropertyMatcher(String entityType) {
    this.entityType = entityType;
  }

  protected boolean propertyMatches(String propertyName, T propertyValue, Matcher<? super T> matcher, Description description) {
    if (matcher == null) {
      return true;
    }
    if (matcher.matches(propertyValue)) {
      return true;
    }
    newline(description).appendText(propertyName).appendText(": ");
    indent();
    matcher.describeMismatch(propertyValue, description);
    outdent();
    return false;
  }

  @Override
  public void describeTo(Description description) {
    description.appendText("A ").appendText(entityType).appendText(" with");
    indent();
    describeProperties(new PropertyDescriber(description));
    outdent();
  }

  protected abstract void describeProperties(PropertyDescriber describer);
  protected abstract void checkProperties(T t, PropertyMismatchDescriber describer);

  @Override
  protected boolean matchesSafely(T t, Description description) {
    PropertyMismatchDescriber mismatchDescriber = new PropertyMismatchDescriber(description);
    checkProperties(t, mismatchDescriber);
    return mismatchDescriber.result();
  }
}
