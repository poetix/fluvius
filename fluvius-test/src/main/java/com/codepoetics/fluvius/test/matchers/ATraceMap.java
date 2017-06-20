package com.codepoetics.fluvius.test.matchers;

import com.codepoetics.fluvius.api.tracing.FlowStepType;
import com.codepoetics.fluvius.api.tracing.TraceMap;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;

/**
 * Matcher for a {@link com.codepoetics.fluvius.api.tracing.TraceMap}
 */
public final class ATraceMap extends BasePropertyMatcher<TraceMap> {

  private final Matcher<? super FlowStepType> typeMatcher;
  private Matcher<? super UUID> idMatcher;
  private Matcher<String> descriptionMatcher;
  private Matcher<Iterable<? extends String>> requiredKeysMatcher;
  private Matcher<String> providedKeyMatcher;
  private List<Matcher<? super TraceMap>> childMatchers;

  private ATraceMap(Matcher<FlowStepType> typeMatcher) {
    super("TraceMap");
    this.typeMatcher = typeMatcher;
  }

  public static ATraceMap ofType(FlowStepType type) {
    return new ATraceMap(equalTo(type));
  }

  public static ATraceMap ofAnyType() {
   return new ATraceMap(Matchers.any(FlowStepType.class));
  }

  public ATraceMap withId(UUID expected) {
    return withId(equalTo(expected));
  }

  public ATraceMap withId(Matcher<? super UUID> idMatcher) {
    this.idMatcher = idMatcher;
    return this;
  }

  public ATraceMap withDescription(String expected) {
    return withDescription(equalTo(expected));
  }

  public ATraceMap withDescription(Matcher<String> descriptionMatcher) {
    this.descriptionMatcher = descriptionMatcher;
    return this;
  }

  public ATraceMap withRequiredKeys(String...requiredKeys) {
    return withRequiredKeys(Matchers.containsInAnyOrder(requiredKeys));
  }

  public final ATraceMap withRequiredKeys(Matcher<? super String>...keyMatchers) {
    return withRequiredKeys(Matchers.containsInAnyOrder(Arrays.asList(keyMatchers)));
  }

  public ATraceMap withRequiredKeys(Collection<String> requiredKeys) {
    return withRequiredKeys(Matchers.containsInAnyOrder(requiredKeys.toArray(new String[requiredKeys.size()])));
  }

  public ATraceMap withRequiredKeys(Matcher<Iterable<? extends String>> requiredKeysMatcher) {
    this.requiredKeysMatcher = requiredKeysMatcher;
    return this;
  }

  public ATraceMap withProvidedKey(String expected) {
    return withProvidedKey(Matchers.equalTo(expected));
  }

  public ATraceMap withProvidedKey(Matcher<String> providedKeyMatcher) {
    this.providedKeyMatcher = providedKeyMatcher;
    return this;
  }

  public ATraceMap withChildren(Matcher<? super TraceMap>...traceMapMatchers) {
    return withChildren(Arrays.asList(traceMapMatchers));
  }

  public ATraceMap withChildren(List<Matcher<? super TraceMap>> childMatchers) {
    this.childMatchers = childMatchers;
    return this;
  }

  @Override
  protected void describeProperties(PropertyDescriber describer) {
    describer.describeProperty("type", typeMatcher)
        .describeProperty("id", idMatcher)
        .describeProperty("description", descriptionMatcher)
        .describeProperty("requiredKeys", requiredKeysMatcher)
        .describeProperty("providedKey", providedKeyMatcher)
        .describeProperty("children", childMatchers);
  }

  @Override
  protected void checkProperties(TraceMap traceMap, PropertyMismatchDescriber describer) {
    describer.check("type", traceMap.getType(), typeMatcher)
        .check("id", traceMap.getStepId(), idMatcher)
        .check("description", traceMap.getDescription(), descriptionMatcher)
        .check("requiredKeys", traceMap.getRequiredKeys(), requiredKeysMatcher)
        .check("providedKey", traceMap.getProvidedKey(), providedKeyMatcher)
        .check("children", traceMap.getChildren(), childMatchers == null ? null : Matchers.contains(childMatchers));
  }
}
