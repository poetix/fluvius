package com.codepoetics.fluvius.json.history;

import com.codepoetics.fluvius.api.Action;
import com.codepoetics.fluvius.api.Flow;
import com.codepoetics.fluvius.api.FlowExecution;
import com.codepoetics.fluvius.api.FlowVisitor;
import com.codepoetics.fluvius.api.functional.F2;
import com.codepoetics.fluvius.api.history.EventDataSerialiser;
import com.codepoetics.fluvius.api.history.FlowHistory;
import com.codepoetics.fluvius.api.history.FlowHistoryRepository;
import com.codepoetics.fluvius.flows.Flows;
import com.codepoetics.fluvius.history.EventDataSerialisers;
import com.codepoetics.fluvius.history.History;
import com.codepoetics.fluvius.visitors.Visitors;
import com.fasterxml.jackson.core.JsonProcessingException;
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
  private final FlowHistoryRepository<JsonNode> repository = History.createInMemoryRepository(serialiser);

  @Test
  public void inMemoryRepositoryStoresFlowHistory() throws JsonProcessingException {
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

    final FlowExecution<Double> execution = History.compileRecording(completeFlow, repository, LOGGING_VISITOR);

    final UUID flowId = UUID.randomUUID();

    execution
        .run(
            flowId,
            userName.of("Arthur"),
            password.of("Special secret password"),
            postcode.of("VB6 5UX")
        );

    System.out.println(mapper.writeValueAsString(FlowHistoryView.from(repository.getFlowHistory(flowId))));
  }
}
