package com.codepoetics.fluvius.history;

import com.codepoetics.fluvius.api.Flow;
import com.codepoetics.fluvius.api.FlowExecution;
import com.codepoetics.fluvius.api.compilation.FlowCompiler;
import com.codepoetics.fluvius.api.functional.DoubleParameterStep;
import com.codepoetics.fluvius.api.history.FlowHistoryRepository;
import com.codepoetics.fluvius.api.tracing.FlowStepType;
import com.codepoetics.fluvius.api.tracing.TraceMapLabel;
import com.codepoetics.fluvius.compilation.Compilers;
import com.codepoetics.fluvius.flows.Flows;
import com.codepoetics.fluvius.test.matchers.*;
import com.codepoetics.fluvius.tracing.TraceMaps;
import org.junit.Test;

import java.util.UUID;

import static com.codepoetics.fluvius.FlowExample.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class FlowHistoryTest {

  private final FlowHistoryRepository<String> repository = FlowHistoryRepositories.createInMemory(EventDataSerialisers.toStringSerialiser());
  private final FlowCompiler compiler = Compilers.builder()
      .loggingToConsole()
      .recordingTo(repository)
      .build();

  @Test
  public void inMemoryRepositoryStoresFlowHistory() throws Exception {
    RecordingMatcher recorder = new RecordingMatcher();

    Flow<String> getAccessToken = Flows
        .obtaining(accessToken)
        .from(userName, password)
        .using("Authorize user", new DoubleParameterStep<String, String, String>() {
          @Override
          public String apply(String username, String password) {
            return "ACCESS TOKEN";
          }
        });

    Flow<Double> getLocalTemperature = Flows
        .obtaining(temperature)
        .from(accessToken, postcode)
        .using("Get local temperature", new DoubleParameterStep<String, String, Double>() {
          @Override
          public Double apply(String accessCode, String postcode) {
            return 26D;
          }
        });

    Flow<Double> completeFlow = getAccessToken.then(getLocalTemperature);

    assertThat(
        TraceMaps.getTraceMap(completeFlow),
        ATraceMap.ofType(FlowStepType.SEQUENCE)
            .withChildren(AMap
                .containing(
                    TraceMapLabel.forSequence(1),
                    ATraceMap.ofType(FlowStepType.STEP)
                        .withId(recorder.record("authorize user"))
                        .withDescription("Authorize user"))
                .with(
                    TraceMapLabel.forSequence(2),
                    ATraceMap.ofType(FlowStepType.STEP)
                        .withId(recorder.record("get temperature"))
                        .withDescription("Get local temperature"))
            )
    );

    FlowExecution<Double> execution = compiler.compile(completeFlow);

    UUID flowId = UUID.randomUUID();

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
