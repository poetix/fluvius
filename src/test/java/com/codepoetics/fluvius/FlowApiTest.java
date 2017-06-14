package com.codepoetics.fluvius;

import com.codepoetics.fluvius.api.Action;
import com.codepoetics.fluvius.api.Condition;
import com.codepoetics.fluvius.api.Flow;
import com.codepoetics.fluvius.api.FlowVisitor;
import com.codepoetics.fluvius.api.functional.F1;
import com.codepoetics.fluvius.api.functional.F2;
import com.codepoetics.fluvius.api.functional.P1;
import com.codepoetics.fluvius.api.functional.ScratchpadFunction;
import com.codepoetics.fluvius.api.scratchpad.Key;
import com.codepoetics.fluvius.api.scratchpad.Scratchpad;
import com.codepoetics.fluvius.api.tracing.TraceEventListener;
import com.codepoetics.fluvius.api.tracing.TracedFlowExecution;
import com.codepoetics.fluvius.conditions.Conditions;
import com.codepoetics.fluvius.flows.Flows;
import com.codepoetics.fluvius.scratchpad.Keys;
import com.codepoetics.fluvius.scratchpad.Scratchpads;
import com.codepoetics.fluvius.utilities.Serialisation;
import com.codepoetics.fluvius.visitors.Visitors;
import org.junit.Test;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

import static com.codepoetics.fluvius.flows.Flows.branch;
import static com.codepoetics.fluvius.visitors.Visitors.logging;
import static com.codepoetics.fluvius.visitors.Visitors.mutationChecking;
import static org.junit.Assert.assertEquals;

public class FlowApiTest implements Serializable {

  private static final FlowVisitor<Action> LOGGING_VISITOR = mutationChecking(logging(Visitors.getDefault()));

  public static final class AuthorisationResult {
    private final boolean isAuthorised;
    private final String accessToken;

    public AuthorisationResult(final boolean isAuthorised, final String accessToken) {
      this.isAuthorised = isAuthorised;
      this.accessToken = accessToken;
    }

    public boolean isAuthorised() {
      return isAuthorised;
    }

    public String getAccessToken() {
      return accessToken;
    }

    @Override
    public String toString() {
      if (isAuthorised) {
        return "Authorized with access token " + accessToken;
      }
      return "Not authorized";
    }
  }

  private static final Key<String> userName = Keys.named("userName");
  private static final Key<String> password = Keys.named("password");
  private static final Key<String> postcode = Keys.named("postcode");
  private static final Key<AuthorisationResult> authorisationResult = Keys.named("authorisationResult");
  private static final Key<String> accessToken = Keys.named("accessToken");
  private static final Key<Double> temperature = Keys.named("temperature");
  private static final Key<String> weatherMessage = Keys.named("weatherMessage");

  private static final Flow<AuthorisationResult> authorize = Flows
      .obtaining(authorisationResult)
      .from(userName, password)
      .using(
          "Check credentials",
          new F2<String, String, AuthorisationResult>() {
            @Override
            public AuthorisationResult apply(final String username, final String password) {
              return (password.equals("the real password"))
                  ? new AuthorisationResult(true, "ACCESS TOKEN")
                  : new AuthorisationResult(false, null);
            }
          });

  private static final Flow<String> extractAccessToken = Flows.obtaining(accessToken).from(authorisationResult).using(new F1<AuthorisationResult, String>() {
    @Override
    public String apply(final AuthorisationResult input) {
      return input.getAccessToken();
    }
  });

  private static final Condition isAuthorised = Conditions.keyMatches(authorisationResult, "is authorized", new P1<AuthorisationResult>() {
    @Override
    public boolean test(final AuthorisationResult value) {
      return value.isAuthorised();
    }
  });

  private static final Flow<String> formatError = Flows.obtaining(weatherMessage).from(userName).using("Format error message", new F1<String, String>() {
    @Override
    public String apply(final String userName) {
      return "Sorry, " + userName + ", your credentials were not valid";
    }
  });


  private static final Flow<Double> getWeather = Flows
      .obtaining(temperature)
      .from(accessToken, postcode)
      .using(
          "Fetch weather",
          new F2<String, String, Double>() {
            @Override
            public Double apply(final String accessToken, final String postcode) {
              return 26D;
            }
          });

  private static final Flow<String> formatWeather = Flows
      .from(userName, postcode, temperature)
      .to(weatherMessage)
      .using("Format weather", new ScratchpadFunction<String>() {
        @Override
        public String apply(final Scratchpad scratchpad) {
          return scratchpad.get(userName)
              + ", the temperature at " + scratchpad.get(postcode)
              + " is " + scratchpad.get(temperature) + " degrees";
        }
      });

  @Test
  public void testFlowApi() {
    Flow<String> combined = authorize
        .then(branch(
            isAuthorised, extractAccessToken
                .then(getWeather)
                .then(formatWeather))
            .otherwise(formatError));

    System.out.println(Flows.prettyPrint(combined));

    Scratchpad input = Scratchpads.create(
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
    Flow<String> getAccessToken = Flows
        .obtaining(accessToken)
        .from(userName, password)
        .using("Authorize user", new F2<String, String, String>() {
          @Override
          public String apply(final String username, final String password) {
            return "ACCESS TOKEN";
          }
        });

    Flow<Double> getLocalTemperature = Flows
        .obtaining(temperature)
        .from(accessToken, postcode)
        .using("Get local temperature", new F2<String, String, Double>() {
          @Override
          public Double apply(final String accessCode, final String postcode) {
            return 26D;
          }
        });

    Flow<Double> completeFlow = getAccessToken.then(getLocalTemperature);

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
    Flow<String> getAccessToken = Flows
        .obtaining(accessToken)
        .from(userName, password)
        .using("Authorize user", new F2<String, String, String>() {
          @Override
          public String apply(final String username, final String password) {
            return "ACCESS TOKEN";
          }
        });

    Flow<Double> getLocalTemperature = Flows
        .obtaining(temperature)
        .from(accessToken, postcode)
        .using("Get local temperature", new F2<String, String, Double>() {
          @Override
          public Double apply(final String accessCode, final String postcode) {
            return 26D;
          }
        });

    Flow<Double> completeFlow = Serialisation.roundtrip(getAccessToken.then(getLocalTemperature));

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
    Flow<String> getAccessToken = Flows
        .obtaining(accessToken)
        .from(userName, password)
        .using("Authorize user", new F2<String, String, String>() {
          @Override
          public String apply(final String username, final String password) {
            return "ACCESS TOKEN";
          }
        });

    Flow<Double> getLocalTemperature = Flows
        .obtaining(temperature)
        .from(accessToken, postcode)
        .using("Get local temperature", new F2<String, String, Double>() {
          @Override
          public Double apply(final String accessCode, final String postcode) {
            return 26D;
          }
        });

    Flow<Double> completeFlow = Serialisation.roundtrip(getAccessToken.then(getLocalTemperature));

    System.out.println(Flows.prettyPrint(completeFlow));

    TraceEventListener listener = new TraceEventListener() {
      @Override
      public void stepStarted(UUID flowId, UUID id, Map<String, Object> scratchpad) {
        System.out.println("Step " + id + " started with scratchpad " + scratchpad);
      }

      @Override
      public void stepSucceeded(UUID flowId, UUID id, Object result) {
        System.out.println("Step " + id + " succeeded with result " + result);
      }

      @Override
      public void stepFailed(UUID flowId, UUID id, Throwable throwable) {
        System.out.println("Step " + id + " failed with exception " + throwable);
      }
    };

    TracedFlowExecution<Double> tracedFlowExecution = Flows.compileTracing(completeFlow, listener, LOGGING_VISITOR);

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
    Flow<String> changeAccessToken = Flows.obtaining(accessToken).from(accessToken).using(new F1<String, String>() {
      @Override
      public String apply(final String input) {
        return input + ", so there!";
      }
    });

    Flows.compile(changeAccessToken, LOGGING_VISITOR).run(UUID.randomUUID(), accessToken.of("I have access"));
  }
}
