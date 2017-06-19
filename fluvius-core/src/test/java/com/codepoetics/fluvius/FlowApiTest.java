package com.codepoetics.fluvius;

import com.codepoetics.fluvius.api.Flow;
import com.codepoetics.fluvius.api.FlowExecution;
import com.codepoetics.fluvius.api.compilation.FlowCompiler;
import com.codepoetics.fluvius.api.compilation.TracedFlowCompiler;
import com.codepoetics.fluvius.api.functional.F1;
import com.codepoetics.fluvius.api.functional.F2;
import com.codepoetics.fluvius.api.scratchpad.Scratchpad;
import com.codepoetics.fluvius.api.tracing.TraceEventListener;
import com.codepoetics.fluvius.api.tracing.TracedFlowExecution;
import com.codepoetics.fluvius.compilation.Compilers;
import com.codepoetics.fluvius.flows.Flows;
import com.codepoetics.fluvius.scratchpad.Scratchpads;
import com.codepoetics.fluvius.utilities.Serialisation;
import org.junit.Test;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

import static com.codepoetics.fluvius.FlowExample.*;
import static com.codepoetics.fluvius.flows.Flows.branch;
import static org.junit.Assert.assertEquals;

public class FlowApiTest implements Serializable {

  private static final FlowCompiler compiler = Compilers.builder()
      .loggingToConsole()
      .mutationChecking()
      .build();

  @Test
  public void testFlowApi() throws Exception {
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

    FlowExecution<String> execution = compiler.compile(combined);
    assertEquals(
        "Sorry, Fred, your credentials were not valid",
        execution.run(input));

    assertEquals(
        "Fred, the temperature at VB6 5UX is 26.0 degrees",
        execution.run(input.with(password.of("the real password"))));
  }

  @Test
  public void dependencyExample() throws Exception {
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

    compiler.compile(completeFlow)
        .run(
            userName.of("Arthur"),
            password.of("Special secret password"),
            postcode.of("VB6 5UX")
    );
  }

  @Test
  public void flowsSerialise() throws Exception {
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

    compiler.compile(completeFlow)
        .run(
            userName.of("Arthur"),
            password.of("Special secret password"),
            postcode.of("VB6 5UX")
    );
  }

  @Test
  public void tracing() throws Exception {
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
      public void stepFailed(final UUID flowId, final UUID id, final Exception exception) {
        System.out.println("Step " + id + " failed with exception " + exception);
      }
    };

    TracedFlowCompiler compiler = Compilers.builder()
        .loggingToConsole()
        .mutationChecking()
        .tracingWith(listener)
        .build();

    final TracedFlowExecution<Double> tracedFlowExecution = compiler.compile(completeFlow);

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
  public void flowsCannotOverwriteAlreadyWrittenKeys() throws Exception {
    final Flow<String> changeAccessToken = Flows.obtaining(accessToken).from(accessToken).using(new F1<String, String>() {
      @Override
      public String apply(final String input) {
        return input + ", so there!";
      }
    });

    compiler.compile(changeAccessToken).run(accessToken.of("I have access"));
  }
}
