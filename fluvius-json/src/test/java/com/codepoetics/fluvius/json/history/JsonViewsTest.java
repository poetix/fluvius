package com.codepoetics.fluvius.json.history;

import com.codepoetics.fluvius.api.Action;
import com.codepoetics.fluvius.api.Flow;
import com.codepoetics.fluvius.api.FlowExecution;
import com.codepoetics.fluvius.api.FlowVisitor;
import com.codepoetics.fluvius.api.compilation.FlowCompiler;
import com.codepoetics.fluvius.api.functional.DoubleParameterStep;
import com.codepoetics.fluvius.api.history.EventDataSerialiser;
import com.codepoetics.fluvius.api.history.FlowEventRepository;
import com.codepoetics.fluvius.compilation.Compilers;
import com.codepoetics.fluvius.flows.Flows;
import com.codepoetics.fluvius.history.FlowEventRepositories;
import com.codepoetics.fluvius.tracing.TraceMaps;
import com.codepoetics.fluvius.visitors.Visitors;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.util.UUID;

import static com.codepoetics.fluvius.json.history.FlowExample.*;
import static com.codepoetics.fluvius.visitors.Visitors.logging;
import static com.codepoetics.fluvius.visitors.Visitors.mutationChecking;

public class JsonViewsTest {

  private static final FlowVisitor<Action> LOGGING_VISITOR = mutationChecking(logging(Visitors.getDefault()));

  private final ObjectMapper mapper = new ObjectMapper();
  private final EventDataSerialiser<JsonNode> serialiser = JsonEventDataSerialiser.using(mapper);
  private final FlowEventRepository<JsonNode> repository = FlowEventRepositories.createInMemory(serialiser);
  private final FlowCompiler compiler = Compilers.builder()
      .loggingToConsole()
      .mutationChecking()
      .tracingWith(repository)
      .build();

  @Test
  public void flowHistoryViewExample() throws Exception {
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

    FlowExecution<Double> execution = compiler.compile(completeFlow);

    UUID flowId = UUID.randomUUID();

    execution
        .run(
            flowId,
            userName.of("Arthur"),
            password.of("Special secret password"),
            postcode.of("VB6 5UX")
        );

    System.out.println(mapper.writeValueAsString(FlowHistoryView.from(
        flowId,
        TraceMaps.getTraceMap(completeFlow),
        repository.getEvents(flowId))));
  }
}
