package com.codepoetics.fluvius.history;

import com.codepoetics.fluvius.api.Flow;
import com.codepoetics.fluvius.api.FlowExecution;
import com.codepoetics.fluvius.api.compilation.FlowCompiler;
import com.codepoetics.fluvius.api.functional.F2;
import com.codepoetics.fluvius.api.history.FlowHistoryRepository;
import com.codepoetics.fluvius.compilation.Compilers;
import com.codepoetics.fluvius.flows.Flows;
import org.junit.Test;

import java.util.UUID;

import static com.codepoetics.fluvius.FlowExample.*;

public class InMemoryRepositoryTest {

  private final FlowHistoryRepository<String> repository = History.createInMemoryRepository(EventDataSerialisers.toStringSerialiser());
  private final FlowCompiler compiler = Compilers.builder()
      .loggingToConsole()
      .recordingTo(repository)
      .build();

  @Test
  public void inMemoryRepositoryStoresFlowHistory() {
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

    final FlowExecution<Double> execution = compiler.compile(completeFlow);

    final UUID flowId = UUID.randomUUID();

    execution
        .run(
            flowId,
            userName.of("Arthur"),
            password.of("Special secret password"),
            postcode.of("VB6 5UX")
        );

    System.out.println(repository.getFlowHistory(flowId));
  }
}
