package com.codepoetics.fluvius.history;

import com.codepoetics.fluvius.api.Flow;
import com.codepoetics.fluvius.api.FlowExecution;
import com.codepoetics.fluvius.api.compilation.FlowCompiler;
import com.codepoetics.fluvius.api.functional.F2;
import com.codepoetics.fluvius.api.history.FlowHistoryRepository;
import com.codepoetics.fluvius.api.tracing.FlowStepType;
import com.codepoetics.fluvius.compilation.Compilers;
import com.codepoetics.fluvius.flows.Flows;
import com.codepoetics.fluvius.test.matchers.AFlowEvent;
import com.codepoetics.fluvius.test.matchers.AFlowHistory;
import com.codepoetics.fluvius.test.matchers.ATraceMap;
import com.codepoetics.fluvius.test.matchers.RecordingMatcher;
import com.codepoetics.fluvius.tracing.TraceMaps;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.UUID;

import static com.codepoetics.fluvius.FlowExample.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class FlowHistoryTest {

  private final FlowHistoryRepository<String> repository = History.createInMemoryRepository(EventDataSerialisers.toStringSerialiser());
  private final FlowCompiler compiler = Compilers.builder()
      .loggingToConsole()
      .recordingTo(repository)
      .build();

  @Test
  public void inMemoryRepositoryStoresFlowHistory() throws Exception {
    RecordingMatcher recorder = new RecordingMatcher();

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
        .obtaining(temperature)
        .from(accessToken, postcode)
        .using("Get local temperature", new F2<String, String, Double>() {
          @Override
          public Double apply(final String accessCode, final String postcode) {
            return 26D;
          }
        });

    final Flow<Double> completeFlow = getAccessToken.then(getLocalTemperature);

    assertThat(
        TraceMaps.getTraceMap(completeFlow),
        ATraceMap.ofType(FlowStepType.SEQUENCE)
            .withChildren(
                ATraceMap.ofType(FlowStepType.STEP)
                    .withId(recorder.record("authorize user"))
                    .withDescription("Authorize user"),

                ATraceMap.ofType(FlowStepType.STEP)
                    .withId(recorder.record("get temperature"))
                    .withDescription("Get local temperature")
            )
    );

    final FlowExecution<Double> execution = compiler.compile(completeFlow);

    final UUID flowId = UUID.randomUUID();

    execution
        .run(
            flowId,
            userName.of("Arthur"),
            password.of("Special secret password"),
            postcode.of("VB6 5UX")
        );

    assertThat(repository.getFlowHistory(flowId),
        AFlowHistory
            .<String>withFlowId(flowId)

            .withEventHistory(
                AFlowEvent.<String>stepStarted(),

                  AFlowEvent.<String>stepStarted()
                      .withStepId(recorder.equalsRecorded("authorize user")),
                  AFlowEvent.stepSucceeded("ACCESS TOKEN"),

                  AFlowEvent.<String>stepStarted()
                      .withStepId(recorder.equalsRecorded("get temperature")),
                  AFlowEvent.stepSucceeded("26.0"),

                AFlowEvent.stepSucceeded("26.0")
            )
    );
  }
}
