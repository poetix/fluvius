package com.codepoetics.fluvius;

import com.codepoetics.fluvius.api.*;
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
        Flow<AuthorisationResult> authorize = Flows.from(userName, password).to(authorisationResult).using(new Operation<AuthorisationResult>() {
            @Override
            public String getName() {
                return "Authorize credentials";
            }

            @Override
            public AuthorisationResult run(Scratchpad scratchpad) {
                if (scratchpad.get(password).equals("the real password")) {
                    return new AuthorisationResult(true, "ACCESS TOKEN");
                } else {
                    return new AuthorisationResult(false, null);
                }
            }
        });

        Flow<String> extractAccessToken = Flows.from(authorisationResult).to(accessToken).using(new Operation<String>() {
            @Override
            public String getName() {
                return "Extract access token";
            }

            @Override
            public String run(Scratchpad scratchpad) {
                return scratchpad.get(authorisationResult).getAccessToken();
            }
        });

        Condition isAuthorised = new Condition() {
            @Override
            public String getDescription() {
                return "is authorised";
            }

            @Override
            public boolean test(Scratchpad scratchpad) {
                return scratchpad.get(authorisationResult).isAuthorised();
            }
        };

        Flow<String> formatError = Flows.from(userName, authorisationResult).to(weatherMessage).using(new Operation<String>() {
            @Override
            public String getName() {
                return "Format error message";
            }

            @Override
            public String run(Scratchpad scratchpad) {
                return "Sorry, " + scratchpad.get(userName) + ", your credentials were not valid";
            }
        });

        Flow<Double> getWeather = Flows.from(accessToken, postcode).to(temperature).using(new Operation<Double>() {
            @Override
            public String getName() {
                return "Get weather";
            }

            @Override
            public Double run(Scratchpad scratchpad) {
                return 26D;
            }
        });

        Flow<String> formatWeather = Flows.from(userName, postcode, temperature).to(weatherMessage).using(new Operation<String>() {
            @Override
            public String getName() {
                return "Format weather";
            }

            @Override
            public String run(Scratchpad scratchpad) {
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
