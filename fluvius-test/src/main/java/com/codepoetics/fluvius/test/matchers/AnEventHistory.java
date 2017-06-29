package com.codepoetics.fluvius.test.matchers;

import com.codepoetics.fluvius.api.history.FlowEvent;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import java.util.Arrays;
import java.util.List;

import static com.codepoetics.fluvius.test.matchers.IndentationControl.indent;
import static com.codepoetics.fluvius.test.matchers.IndentationControl.outdent;

public final class AnEventHistory<T> extends TypeSafeDiagnosingMatcher<List<FlowEvent<T>>> {

  @SafeVarargs
  public static <T> AnEventHistory<T> of(Matcher<FlowEvent<T>>...matchers) {
    return new AnEventHistory<>(Arrays.asList(matchers));
  }

  private final List<Matcher<FlowEvent<T>>> matchers;

  private AnEventHistory(List<Matcher<FlowEvent<T>>> matchers) {
    this.matchers = matchers;
  }

  @Override
  protected boolean matchesSafely(List<FlowEvent<T>> flowEvents, Description description) {
    if (flowEvents.size() != matchers.size()) {
      description.appendText("history contained ").appendText(Integer.toString(flowEvents.size())).appendText(" events");
      return false;
    }
    boolean matched = true;

    for (int i = 0; i < matchers.size(); i++) {
      Matcher<FlowEvent<T>> matcher = matchers.get(i);
      FlowEvent<T> flowEvent = flowEvents.get(i);
      if (!matcher.matches(flowEvent)) {
        matched = false;
        IndentationControl.newline(description)
            .appendText(Integer.toString(i))
            .appendText(": ");
        indent();
        matcher.describeMismatch(flowEvent, description);
        outdent();
      }
    }

    return matched;
  }

  @Override
  public void describeTo(Description description) {
    description.appendText("A flow event history:");
    indent();
    for (int i = 0; i < matchers.size(); i++) {
      IndentationControl.newline(description)
          .appendText(Integer.toString(i))
          .appendText(": ")
          .appendDescriptionOf(matchers.get(i));
    }
    outdent();
  }
}
