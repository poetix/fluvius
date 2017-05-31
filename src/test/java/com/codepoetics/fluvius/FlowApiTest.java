package com.codepoetics.fluvius;

import com.codepoetics.fluvius.api.*;
import com.codepoetics.fluvius.conditions.Conditions;
import com.codepoetics.fluvius.flows.Flows;
import com.codepoetics.fluvius.scratchpad.Keys;
import com.codepoetics.fluvius.scratchpad.Scratchpads;
import com.codepoetics.fluvius.visitors.Visitors;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FlowApiTest {

    private static final FlowVisitor LOGGING_VISITOR = Visitors.logging(Visitors.getDefault());

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

    private static final Key<String> userName = Keys.create("userName");
    private static final Key<String> password = Keys.create("password");
    private static final Key<String> postcode = Keys.create("postcode");
    private static final Key<AuthorisationResult> authorisationResult = Keys.create("authorisationResult");
    private static final Key<String> accessToken = Keys.create("accessToken");
    private static final Key<Double> temperature = Keys.create("temperature");
    private static final Key<String> weatherMessage = Keys.create("weatherMessage");

    @Test
    public void testFlowApi() {
        Flow<AuthorisationResult> authorize = Flows.extracting(authorisationResult).from(userName, password).using(
                "Check credentials",
                new Extractor2<String, String, AuthorisationResult>() {
                    @Override
                    public AuthorisationResult extract(String username, String password) {
                        return (password.equals("the real password"))
                            ? new AuthorisationResult(true, "ACCESS TOKEN")
                            : new AuthorisationResult(false, null);
                    }
                });

        Flow<String> extractAccessToken = Flows.extracting(accessToken).from(authorisationResult).using(new Extractor<AuthorisationResult, String>() {
            @Override
            public String extract(AuthorisationResult input) {
                return input.getAccessToken();
            }
        });

        Condition isAuthorised = Conditions.keyMatches(authorisationResult, "is authorized", new ValuePredicate<AuthorisationResult>() {
            @Override
            public boolean test(AuthorisationResult value) {
                return value.isAuthorised();
            }
        });

        Flow<String> formatError = Flows.extracting(weatherMessage).from(userName).using("Format error message", new Extractor<String, String>() {
            @Override
            public String extract(String userName) {
                return "Sorry, " + userName + ", your credentials were not valid";
            }
        });

        Flow<Double> getWeather = Flows.extracting(temperature).from(accessToken, postcode).using(
                "Fetch weather",
                new Extractor2<String, String, Double>() {
            @Override
            public Double extract(String accessToken, String postcode) {
                return 26D;
            }
        });

        Flow<String> formatWeather = Flows.from(userName, postcode, temperature).to(weatherMessage).using("Format weather", new ScratchpadFunction<String>() {
            @Override
            public String apply(Scratchpad scratchpad) {
                return scratchpad.get(userName)
                        + ", the temperature at " + scratchpad.get(postcode)
                        + " is " + scratchpad.get(temperature) + " degrees";
            }
        });

        Flow<String> combined = authorize
                .then(formatError
                        .orIf(isAuthorised,
                                extractAccessToken
                                        .then(getWeather)
                                        .then(formatWeather)));

        System.out.println(Flows.prettyPrint(combined));

        Scratchpad initialScratchpad = Scratchpads.create(
                userName.of("Fred"),
                password.of("verysecurepassword"),
                postcode.of("VB6 5UX")
        );

        assertEquals(
                "Sorry, Fred, your credentials were not valid",
                Flows.run(combined, initialScratchpad, LOGGING_VISITOR));

        Scratchpad retryScratchpad = initialScratchpad.with(password.of("the real password"));

        assertEquals(
                "Fred, the temperature at VB6 5UX is 26.0 degrees",
                Flows.run(combined, retryScratchpad, LOGGING_VISITOR));
    }
}
