package com.codepoetics.fluvius.test.matchers;

import com.codepoetics.fluvius.api.tracing.FlowStepType;
import com.codepoetics.fluvius.api.tracing.TraceMap;
import com.codepoetics.fluvius.api.tracing.TraceMapLabel;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

import java.util.*;

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
  private AMap<TraceMapLabel, TraceMap> childrenMatcher = AMap.of(TraceMapLabel.class, TraceMap.class);

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

  @SafeVarargs
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

  public final ATraceMap withChildren(AMap<TraceMapLabel, TraceMap> childrenMatcher) {
    this.childrenMatcher = childrenMatcher;
    return this;
  }

  public final ATraceMap withChild(TraceMapLabel label, Matcher<TraceMap> traceMapMatcher) {
    this.childrenMatcher = this.childrenMatcher.with(label, traceMapMatcher);
    return this;
  }

  @Override
  protected void describeProperties(PropertyDescriber describer) {
    describer.describeProperty("type", typeMatcher)
        .describeProperty("id", idMatcher)
        .describeProperty("description", descriptionMatcher)
        .describeProperty("requiredKeys", requiredKeysMatcher)
        .describeProperty("providedKey", providedKeyMatcher)
        .describeProperty("children", childrenMatcher);
  }

  @Override
  protected void checkProperties(TraceMap traceMap, PropertyMismatchDescriber describer) {
    describer.check("type", traceMap.getType(), typeMatcher)
        .check("id", traceMap.getStepId(), idMatcher)
        .check("description", traceMap.getDescription(), descriptionMatcher)
        .check("requiredKeys", traceMap.getRequiredKeys(), requiredKeysMatcher)
        .check("providedKey", traceMap.getProvidedKey(), providedKeyMatcher)
        .check("children", traceMap.getChildren(), childrenMatcher);
  }
}
