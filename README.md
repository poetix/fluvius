# Fluvius

[![Maven Central](https://img.shields.io/maven-central/v/com.codepoetics/fluvius.svg)](http://search.maven.org/#search%7Cga%7C1%7Cfluvius)
[![Build Status](https://travis-ci.org/poetix/fluvius.svg?branch=master)](https://travis-ci.org/poetix/fluvius)

*Fluvius* provides a simple API for co-ordinating sequences of actions, which may include branching logic (but no more complex control flow than that).

Here's an example sequence, for processing a request to update a user's details.

```
Sequence:
    1: Check user's credentials
    2: Branch
    2a) If credentials are authorized: Sequence:
        2a.1: Update user's details
        2a.2: Format success message
    2b) Otherwise: Format failure message
```

At the top-level, this sequence might be defined as follows in Java:

```java
Flow<String> updateUserDetails = checkUsersCredentials.branchOnResult()
    .onCondition(isAuthorised, updateUserDetails.then(formatSuccessMessage))
    .otherwise(formatFailureMessage));
```

In this example, `checkUsersCredentials` is a `Flow` which performs a single action: it accepts a user name and password, and returns an authorisation response:

```java
Flow<AuthorizationResponse> checkUsersCredentials = Flow
    .obtaining(authorizationResponse)
    .from(userName, password)
    .using("Check user's credentials", new DoubleParameterStep<String, String, AuthorizationResponse>() {
        @Override
        public AuthorizationResponse apply(String userName, String password) {
            // Authorization logic goes here
        }
    });
```

We supply a function (in this case, an anonymous inner class extending the `DoubleParameterStep` interface) to do the work.

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
FlowCompiler compiler = Compilers.builder().loggingToConsole().build();

Double result = compiler.compile(completeFlow).run(
    userName.of("Arthur"),
    password.of("Special secret password"),
    postcode.of("VB6 5UX"));
```

The flow is assembled into an executable `FlowExecution` by the `FlowCompiler`, and this `FlowExecution` is run to obtain a result. We can configure the compiler to layer in various behaviours to execution, such as logging or sending trace information to an event listener. Here, we've set it up to add logging to the console, which prints out the following:

```
2017-07-14T14:40:36.483/76cc9d7a-4e77-4140-acdc-36fecbaf6ccc Operation 'Authorize user' started with scratchpad {userName=Arthur, password=the true password, postcode=VB6 5UX}
2017-07-14T14:40:36.491/76cc9d7a-4e77-4140-acdc-36fecbaf6ccc Operation 'Authorize user' completed, writing value ACCESS TOKEN to key accessToken
2017-07-14T14:40:36.491/76cc9d7a-4e77-4140-acdc-36fecbaf6ccc Operation 'Get local temperature' started with scratchpad {userName=Arthur, password=the true password, postcode=VB6 5UX, accessToken=ACCESS TOKEN}
2017-07-14T14:40:36.492/76cc9d7a-4e77-4140-acdc-36fecbaf6ccc Operation 'Get local temperature' completed, writing value 26.0 to key temperature
```

# Reflective flow wrapping

As an alternative to manually defining `Key`s and configuring flows using the fluent API, a `FlowWrapperFactory` can be used to convert objects to `Flow`s directly.

Here's class defining a single flow step:

```java
public static final class SayHelloStep implements Returning<String> {
    @StepMethod("greeting")
    String getGreeting(@KeyName("personName") String personName) {
      return "Hello " + personName;
    }
}
```

and here's how to wrap it into a `Flow``:

```java
KeyProvider keyProvider = Keys.createProvider();
FlowWrapperFactory factory = Wrappers.createWrapperFactory(keyProvider);

Flow<String> helloFlow = factory.flowFor(new SayHelloStep());
```

which pretty-prints as

```
Say hello (requires [personName], provides greeting)
```

Note that the description of the flow step, "Say hello", has been inferred from the class name, while the names of the required and provided keys have been taken from the annotations applied to the `getGreeting` method.

The difficulty here is that because we have not explicitly declared a `personName` `Key`, we cannot provide a value for that key to run the flow with. There are two alternatives here. The first is to use the `KeyProvider` to request a copy of the `Key` it associated with the `personName` parameter:

```java
FlowCompiler compiler = Compilers.builder().loggingToConsole().build();
Key<String> personName = keyProvider.getKey("personName", String.class);

String greeting = compiler.compile(helloFlow).run(personName.of("Gerald"));
```

An alternative is to generate a proxy which will convert a method call into a flow execution. First we need to define the interface for the proxy:

```java
public interface SayHelloRunner extends Returning<String> {
    FlowRunner<String> sayHello(@KeyName("personName") String personName);
}
```

Then we create the proxy object:

```java
FlowExecutionProxyFactory proxyFactory = Wrappers.createProxyFactory(compiler, keyProvider);
SayHelloRunner runner = proxyFactory.proxyFor(SayHelloRunner.class, helloFlow);

String greeting = runner.sayHello("Gerald").run();
```

Note that the `FlowExecutionProxyFactory` must be created with the same `KeyProvider` as the `FlowWrapperFactory`, to ensure that the `Key`s associated with parameters in the proxy interface are the same as those associated with parameters in the wrapped `StepMethod`s.

When defining large numbers of steps, with a separate class used to define each step, this approach can greatly simplify flow definition by moving responsibility for defining and associating `Key`s with flows to the class definitions for individual steps.

