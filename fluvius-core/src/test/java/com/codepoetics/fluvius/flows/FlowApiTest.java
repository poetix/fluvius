package com.codepoetics.fluvius.flows;

import com.codepoetics.fluvius.FlowExample;
import com.codepoetics.fluvius.api.Flow;
import com.codepoetics.fluvius.api.FlowExecution;
import com.codepoetics.fluvius.api.compilation.FlowCompiler;
import com.codepoetics.fluvius.api.functional.SingleParameterStep;
import com.codepoetics.fluvius.api.functional.DoubleParameterStep;
import com.codepoetics.fluvius.api.scratchpad.Key;
import com.codepoetics.fluvius.api.tracing.FlowStepType;
import com.codepoetics.fluvius.api.tracing.TraceMapLabel;
import com.codepoetics.fluvius.compilation.Compilers;
import com.codepoetics.fluvius.exceptions.FailedKeyRetrievedException;
import com.codepoetics.fluvius.test.matchers.AMap;
import com.codepoetics.fluvius.test.matchers.ATraceMap;
import com.codepoetics.fluvius.test.matchers.RecordingMatcher;
import com.codepoetics.fluvius.test.mocks.MockTraceEventListener;
import com.codepoetics.fluvius.tracing.TraceMaps;
import com.codepoetics.fluvius.utilities.Serialisation;
import org.junit.Test;

import java.io.Serializable;
import java.util.UUID;

import static com.codepoetics.fluvius.FlowExample.*;
import static com.codepoetics.fluvius.flows.Flows.branch;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class FlowApiTest implements Serializable {

  private static final FlowCompiler compiler = Compilers.builder()
      .loggingToConsole()
      .mutationChecking()
      .build();

  private static final Key<UUID> sequenceStepId = Key.named("sequence step id");
  private static final Key<UUID> authorizeUserStepId = Key.named("authorize user step id");
  private static final Key<UUID> getTemperatureStepId = Key.named("get temperature step id");

  @Test
  public void testFlowApi() throws Exception {
    Flow<String> combined = authorize
        .then(branch(
            FlowExample.isAuthorised, extractAccessToken
                .then(getWeather)
                .then(formatWeather))
            .otherwise(formatError));

    System.out.println(Flows.prettyPrint(combined));

    FlowExecution<String> execution = compiler.compile(combined);
    assertEquals(
        "Sorry, Fred, your credentials were not valid",
        execution.run(
            userName.of("Fred"),
            password.of("verysecurepassword"),
            postcode.of("VB6 5UX")));

    assertEquals(
        "Fred, the temperature at VB6 5UX is 26.0 degrees",
        execution.run(
            userName.of("Fred"),
            password.of("the real password"),
            postcode.of("VB6 5UX")
        ));
  }

  @Test
  public void dependencyExample() throws Exception {
    Flow<Double> completeFlow = createTemperatureRetrievingFlow();

    System.out.println(Flows.prettyPrint(completeFlow));

    assertEquals(Double.valueOf(26D),
        compiler.compile(completeFlow)
        .run(
            userName.of("Arthur"),
            password.of("the true password"),
            postcode.of("VB6 5UX")
        ));
  }

  @Test
  public void flowsSerialise() throws Exception {
    Flow<Double> completeFlow = Serialisation.roundtrip(createTemperatureRetrievingFlow());

    assertEquals(Double.valueOf(26D),
        compiler.compile(completeFlow)
            .run(
                userName.of("Arthur"),
                password.of("the true password"),
                postcode.of("VB6 5UX")
            ));
  }

  @Test
  public void testTracing() throws Exception {
    RecordingMatcher recorder = new RecordingMatcher();
    MockTraceEventListener listener = new MockTraceEventListener();

    Flow<Double> completeFlow = createTemperatureRetrievingFlow();

    FlowCompiler compiler = Compilers.builder()
        .loggingToConsole()
        .mutationChecking()
        .tracingWith(listener)
        .build();

    FlowExecution<Double> flowExecution = compiler.compile(completeFlow);

    assertThat(
        TraceMaps.getTraceMap(completeFlow),

        ATraceMap.ofType(FlowStepType.SEQUENCE)
          .withId(recorder.record(sequenceStepId))
          .withChildren(AMap
              .containing(
                  TraceMapLabel.forSequenceMember(1),
                  ATraceMap.ofType(FlowStepType.STEP)
                    .withDescription("Authorize user")
                    .withId(recorder.record(authorizeUserStepId)))
              .with(
                  TraceMapLabel.forSequenceMember(2),
                  ATraceMap.ofType(FlowStepType.STEP)
                    .withDescription("Get local temperature")
                    .withId(recorder.record(getTemperatureStepId)))
          )
    );

    UUID flowId = UUID.randomUUID();

    flowExecution
        .run(
            flowId,
            userName.of("Arthur"),
            password.of("the true password"),
            postcode.of("VB6 5UX")
        );

    listener
      .forFlow(flowId)
      .verifyStepStarted(recorder.equalsRecorded(sequenceStepId),
        userName.of("Arthur"),
        password.of("the true password"),
        postcode.of("VB6 5UX"))

        .verifyStepStarted(recorder.equalsRecorded(authorizeUserStepId))
          .andSucceeded("ACCESS TOKEN")
        .verifyStepStarted(recorder.equalsRecorded(getTemperatureStepId),
          accessToken.of("ACCESS TOKEN"))
          .andSucceeded(26D)

      .verifyStepSucceeded(recorder.equalsRecorded(sequenceStepId),26D);

    UUID failedFlowId = UUID.randomUUID();

    try {
      flowExecution.run(
          failedFlowId,
          userName.of("Arthur"),
          password.of("the untrue password"),
          postcode.of("VB6 5UX")
      );
      fail("Exception thrown during flow should be rethrown on execution");
    } catch (FailedKeyRetrievedException e) {
      assertEquals("Invalid password", e.getCause().getMessage());
    }

    listener
        .forFlow(failedFlowId)
        .verifyStepStarted(recorder.equalsRecorded(sequenceStepId),
            userName.of("Arthur"),
            password.of("the untrue password"),
            postcode.of("VB6 5UX"))

        .verifyStepStarted(recorder.equalsRecorded(authorizeUserStepId))
          .andFailed();
  }

  private Flow<Double> createTemperatureRetrievingFlow() {
    Flow<String> getAccessToken = Flows
        .obtaining(accessToken)
        .from(userName, password)
        .using("Authorize user", new DoubleParameterStep<String, String, String>() {
          @Override
          public String apply(String username, String password) throws Exception {
            if (password.equals("the true password")) {
              return "ACCESS TOKEN";
            }
            throw new IllegalArgumentException("Invalid password");
          }
        });

    Flow<Double> getLocalTemperature = Flows
        .obtaining(FlowExample.temperature)
        .from(accessToken, postcode)
        .using("Get local temperature", new DoubleParameterStep<String, String, Double>() {
          @Override
          public Double apply(String accessCode, String postcode) {
            return 26D;
          }
        });

    return getAccessToken.then(getLocalTemperature);
  }

  @Test(expected = IllegalArgumentException.class)
  public void flowsCannotOverwriteAlreadyWrittenKeys() throws Exception {
    Flow<String> changeAccessToken = Flows.obtaining(accessToken).from(accessToken).using(new SingleParameterStep<String, String>() {
      @Override
      public String apply(String input) {
        return input + ", so there!";
      }
    });

    compiler.compile(changeAccessToken).run(accessToken.of("I have access"));
  }
}
