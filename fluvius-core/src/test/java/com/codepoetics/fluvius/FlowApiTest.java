package com.codepoetics.fluvius;

import com.codepoetics.fluvius.api.Action;
import com.codepoetics.fluvius.api.Flow;
import com.codepoetics.fluvius.api.FlowVisitor;
import com.codepoetics.fluvius.api.functional.F1;
import com.codepoetics.fluvius.api.functional.F2;
import com.codepoetics.fluvius.api.scratchpad.Scratchpad;
import com.codepoetics.fluvius.api.tracing.TraceEventListener;
import com.codepoetics.fluvius.api.tracing.TracedFlowExecution;
import com.codepoetics.fluvius.flows.Flows;
import com.codepoetics.fluvius.scratchpad.Scratchpads;
import com.codepoetics.fluvius.utilities.Serialisation;
import com.codepoetics.fluvius.visitors.Visitors;
import org.junit.Test;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

import static com.codepoetics.fluvius.FlowExample.*;
import static com.codepoetics.fluvius.flows.Flows.branch;
import static com.codepoetics.fluvius.visitors.Visitors.logging;
import static com.codepoetics.fluvius.visitors.Visitors.mutationChecking;
import static org.junit.Assert.assertEquals;

public class FlowApiTest implements Serializable {

  private static final FlowVisitor<Action> LOGGING_VISITOR = mutationChecking(logging(Visitors.getDefault()));

  @Test
  public void testFlowApi() {
    final Flow<String> combined = authorize
        .then(branch(
            FlowExample.isAuthorised, extractAccessToken
                .then(getWeather)
                .then(formatWeather))
            .otherwise(formatError));

    System.out.println(Flows.prettyPrint(combined));

    final Scratchpad input = Scratchpads.create(
        userName.of("Fred"),
        password.of("verysecurepassword"),
        postcode.of("VB6 5UX")
    );

    assertEquals(
        "Sorry, Fred, your credentials were not valid",
        Flows.compile(combined, LOGGING_VISITOR).run(UUID.randomUUID(), input));

    assertEquals(
        "Fred, the temperature at VB6 5UX is 26.0 degrees",
        Flows.compile(combined, LOGGING_VISITOR)
          .run(UUID.randomUUID(), input.with(password.of("the real password"))));
  }

  @Test
  public void dependencyExample() {
    final Flow<String> getAccessToken = Flows
        .obtaining(accessToken)
        .from(userName, password)
        .using("Authorize user", new F2<String, String, String>() {
          @Override
          public String apply(final String username, final String password) {
            return "ACCESS TOKEN";
          }
        });

    final Flow<Double> getLocalTemperature = Flows
        .obtaining(FlowExample.temperature)
        .from(accessToken, postcode)
        .using("Get local temperature", new F2<String, String, Double>() {
          @Override
          public Double apply(final String accessCode, final String postcode) {
            return 26D;
          }
        });

    final Flow<Double> completeFlow = getAccessToken.then(getLocalTemperature);

    System.out.println(Flows.prettyPrint(completeFlow));

    Flows.compile(completeFlow, logging(Visitors.getDefault()))
        .run(UUID.randomUUID(),
            userName.of("Arthur"),
            password.of("Special secret password"),
            postcode.of("VB6 5UX")
    );
  }

  @Test
  public void flowsSerialise() {
    final Flow<String> getAccessToken = Flows
        .obtaining(accessToken)
        .from(userName, password)
        .using("Authorize user", new F2<String, String, String>() {
          @Override
          public String apply(final String username, final String password) {
            return "ACCESS TOKEN";
          }
        });

    final Flow<Double> getLocalTemperature = Flows
        .obtaining(FlowExample.temperature)
        .from(accessToken, postcode)
        .using("Get local temperature", new F2<String, String, Double>() {
          @Override
          public Double apply(final String accessCode, final String postcode) {
            return 26D;
          }
        });

    final Flow<Double> completeFlow = Serialisation.roundtrip(getAccessToken.then(getLocalTemperature));

    System.out.println(Flows.prettyPrint(completeFlow));

    Flows.compile(completeFlow, logging(Visitors.getDefault()))
        .run(UUID.randomUUID(),
            userName.of("Arthur"),
            password.of("Special secret password"),
            postcode.of("VB6 5UX")
    );
  }

  @Test
  public void tracing() {
    final Flow<String> getAccessToken = Flows
        .obtaining(accessToken)
        .from(userName, password)
        .using("Authorize user", new F2<String, String, String>() {
          @Override
          public String apply(final String username, final String password) {
            return "ACCESS TOKEN";
          }
        });

    final Flow<Double> getLocalTemperature = Flows
        .obtaining(FlowExample.temperature)
        .from(accessToken, postcode)
        .using("Get local temperature", new F2<String, String, Double>() {
          @Override
          public Double apply(final String accessCode, final String postcode) {
            return 26D;
          }
        });

    final Flow<Double> completeFlow = Serialisation.roundtrip(getAccessToken.then(getLocalTemperature));

    System.out.println(Flows.prettyPrint(completeFlow));

    final TraceEventListener listener = new TraceEventListener() {
      @Override
      public void stepStarted(final UUID flowId, final UUID id, final Map<String, Object> scratchpad) {
        System.out.println("Step " + id + " started with scratchpad " + scratchpad);
      }

      @Override
      public void stepSucceeded(final UUID flowId, final UUID id, final Object result) {
        System.out.println("Step " + id + " succeeded with result " + result);
      }

      @Override
      public void stepFailed(final UUID flowId, final UUID id, final Throwable throwable) {
        System.out.println("Step " + id + " failed with exception " + throwable);
      }
    };

    final TracedFlowExecution<Double> tracedFlowExecution = Flows.compileTracing(completeFlow, listener, LOGGING_VISITOR);

    System.out.println(tracedFlowExecution.getTraceMap());

    tracedFlowExecution
        .run(
            UUID.randomUUID(),
            userName.of("Arthur"),
            password.of("Special secret password"),
            postcode.of("VB6 5UX")
        );
  }

  @Test(expected = IllegalArgumentException.class)
  public void flowsCannotOverwriteAlreadyWrittenKeys() {
    final Flow<String> changeAccessToken = Flows.obtaining(accessToken).from(accessToken).using(new F1<String, String>() {
      @Override
      public String apply(final String input) {
        return input + ", so there!";
      }
    });

    Flows.compile(changeAccessToken, LOGGING_VISITOR).run(UUID.randomUUID(), accessToken.of("I have access"));
  }
}
