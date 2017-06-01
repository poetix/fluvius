# fluvius

*Fluvius* provides a simple API for co-ordinating sequences of actions, which may include branching logic (but no more complex control flow than that).

Here's an example sequence, for processing a request to update a user's details.

```
Sequence:
    1: Check user's credentials
    2: Branch
        2a) If user is authorized: Sequence:
            2a.1: Update user's details
            2a.2: Format success message
        2b) Otherwise: Format failure message
```

At the top-level, this sequence might be defined as follows in Java:

```java
Flow<String> updateUserDetails = checkUsersCredentials
    .then(branch(
        ifUserIsAuthorized, updateUserDetails.then(formatSuccessMessage))
        .otherwise(formatFailureMessage));
```

In this example, `checkUsersCredentials` is a `Flow` which performs a single action: it accepts a user name and password, and returns an authorisation response:

```java
Flow<AuthorizationResponse> checkUsersCredentials = Flow
    .obtaining(authorizationResponse)
    .from(userName, password)
    .using("Check user's credentials", new F2<String, String, AuthorizationResponse>() {
        @Override
        public AuthorizationResponse apply(String userName, String password) {
            // Authorization logic goes here
        }
    });
```

We supply a function (in this case, an anonymous inner class extending the `F2` interface) to do the work.

The `obtaining` and `from` expressions in the above code both accept `Key`s, which index values written onto a scratchpad used by the flow. Here's how those are defined:

```java
Key<AuthorizationResponse> authorizationResponse = Key.named("authorizationResponse");
Key<String> userName = Key.named("userName");
Key<String> password = Key.named("password");
```

A `Key<T>` provides a type-safe way of retrieving a value of type `T` from a `Scratchpad`, and of populating a Scratchpad with values:

```java
Scratchpad scratchpad = Scratchpads.create(
    userName.of("Arthur"),
    password.of("Super-secret password"));

String myUserName = scratchpad.get(userName);
```

A flow will typically read one or more values from the scratchpad, and write a new value onto the scratchpad at the end. By specifying which keys it requires, and which key it provides, we can reason about the inputs needed for a flow to run.

For example, suppose we join two flows together:

```java
Flow<String> getAccessToken = Flows
        .obtaining(accessToken)
        .from(userName, password)
        .using("Authorize user", new F2<String, String, String>() {
            @Override
            public String apply(String username, String password) {
                return "ACCESS TOKEN";
            }
        });

Flow<Double> getLocalTemperature = Flows
        .obtaining(temperature)
        .from(accessToken, postcode)
        .using("Get local temperature", new F2<String, String, Double>() {
            @Override
            public Double apply(String accessCode, String postcode) {
                return 26D;
            }
        });

Flow<Double> completeFlow = getAccessToken.then(getLocalTemperature);

System.out.println(Flows.prettyPrint(completeFlow));
```

Note the use of the `then` method on `Flow` to join two flows together. The pretty-printer will represent the combined flow as follows:

```
Sequence (requires [userName,password,postcode], provides temperature):
    1: Authorize user (requires [userName,password], provides accessToken)
    2: Get local temperature (requires [accessToken,postcode], provides temperature)
```

We can see that in order to run the whole sequence, we need to supply a scratchpad with values populated for `userName`, `password` and `postcode`. The first step in the sequence uses only `userName` and `password`, and writes a value for `accessToken` into the scratchpad. The second step uses this `accessToken`, but also requires the `postcode` value.

Here's how we run the flow:

```java
Double result = Flows.run(
        completeFlow,
        Scratchpads.create(
            userName.of("Arthur"),
            password.of("Special secret password"),
            postcode.of("VB6 5UX")),
        Visitors.logging(Visitors.getDefault())
);
```

The flow is assembled into an executable `Action` by a `FlowVisitor`, and this `Action` is run to obtain a result. The default visitor simply assembles the pieces of the flow together, but we can supply visitors that perform other actions such as logging flow steps or dispatching them to be run by a local or remote executor. In this case, we've added logging in to the execution behaviour we want, so that we can see how the flow runs. Here's what that prints out:

```
INFO: Running action Authorize user with scratchpad {userName=Arthur, password=Special secret password, postcode=VB6 5UX}
INFO: Action Authorize user returned result ACCESS TOKEN
INFO: Running action Get local temperature with scratchpad {accessToken=ACCESS TOKEN, password=Special secret password, postcode=VB6 5UX, userName=Arthur}
INFO: Action Get local temperature returned result 26.0
```