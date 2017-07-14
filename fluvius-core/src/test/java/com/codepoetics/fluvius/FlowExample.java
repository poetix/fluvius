package com.codepoetics.fluvius;

import com.codepoetics.fluvius.api.Condition;
import com.codepoetics.fluvius.api.Flow;
import com.codepoetics.fluvius.api.functional.SingleParameterStep;
import com.codepoetics.fluvius.api.functional.DoubleParameterStep;
import com.codepoetics.fluvius.api.functional.Predicate;
import com.codepoetics.fluvius.api.functional.ScratchpadFunction;
import com.codepoetics.fluvius.api.scratchpad.Key;
import com.codepoetics.fluvius.api.scratchpad.Scratchpad;
import com.codepoetics.fluvius.conditions.Conditions;
import com.codepoetics.fluvius.flows.Flows;

public final class FlowExample {

  public static final Key<String> userName = Key.named("userName");
  public static final Key<String> password = Key.named("password");
  public static final Key<String> postcode = Key.named("postcode");
  public static final Key<AuthorisationResult> authorisationResult = Key.named("authorisationResult");

  public static final Condition isAuthorised = Conditions.keyMatches(authorisationResult, "is authorized", new Predicate<AuthorisationResult>() {
    @Override
    public boolean test(AuthorisationResult value) {
      return value.isAuthorised();
    }
  });

  public static final Flow<AuthorisationResult> authorize = Flows
      .obtaining(authorisationResult)
      .from(userName, password)
      .using(
          "Check credentials",
          new DoubleParameterStep<String, String, AuthorisationResult>() {
            @Override
            public AuthorisationResult apply(String username, String password) {
              return (password.equals("the real password"))
                  ? new AuthorisationResult(true, "ACCESS TOKEN")
                  : new AuthorisationResult(false, null);
            }
          });

  public static final Key<String> accessToken = Key.named("accessToken");

  public static final Flow<String> extractAccessToken = Flows.obtaining(accessToken).from(authorisationResult).using(new SingleParameterStep<AuthorisationResult, String>() {
            @Override
            public String apply(AuthorisationResult input) {
              return input.getAccessToken();
            }
          });

  public static final Key<Double> temperature = Key.named("temperature");

  public static final Flow<Double> getWeather = Flows
              .obtaining(temperature)
              .from(accessToken, postcode)
              .using(
                  "Fetch weather",
                  new DoubleParameterStep<String, String, Double>() {
                    @Override
                    public Double apply(String accessToken, String postcode) {
                      return 26D;
                    }
                  });

  public static final Key<String> weatherMessage = Key.named("weatherMessage");

  public static final Flow<String> formatWeather = Flows
      .from(userName, postcode, temperature)
      .to(weatherMessage)
      .using("Format weather", new ScratchpadFunction<String>() {
        @Override
        public String apply(Scratchpad scratchpad) {
          return scratchpad.get(userName)
              + ", the temperature at " + scratchpad.get(postcode)
              + " is " + scratchpad.get(temperature) + " degrees";
        }
      });

  public static final Flow<String> formatError = Flows.obtaining(weatherMessage).from(userName).using("Format error message", new SingleParameterStep<String, String>() {
                    @Override
                    public String apply(String userName) {
                      return "Sorry, " + userName + ", your credentials were not valid";
                    }
                  });

  private FlowExample() {
  }

  public static final class AuthorisationResult {
    private final boolean isAuthorised;
    private final String accessToken;

    public AuthorisationResult(boolean isAuthorised, String accessToken) {
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
}
