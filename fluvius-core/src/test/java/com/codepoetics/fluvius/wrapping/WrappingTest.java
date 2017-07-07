package com.codepoetics.fluvius.wrapping;

import com.codepoetics.fluvius.api.Flow;
import com.codepoetics.fluvius.api.annotations.KeyName;
import com.codepoetics.fluvius.api.annotations.OperationName;
import com.codepoetics.fluvius.api.annotations.StepMethod;
import com.codepoetics.fluvius.api.functional.Returning;
import com.codepoetics.fluvius.api.wrapping.FlowWrapperFactory;
import com.codepoetics.fluvius.compilation.Compilers;
import com.codepoetics.fluvius.flows.Flows;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class WrappingTest {

  @OperationName("Log in")
  public static final class LoginStep implements Returning<String> {

    private final boolean fails;

    public LoginStep(boolean fails) {
      this.fails = fails;
    }

    @StepMethod("accessToken")
    public String logIn(@KeyName("username") String username, @KeyName("password") String password) {
      if (fails) {
        throw new IllegalStateException("Out of cheese exception");
      }
      return UUID.randomUUID().toString();
    }
  }

  @OperationName("Get account details")
  public static final class GetAccountDetailsStep implements Returning<String> {

    @StepMethod("accountDetails")
    public String getAccountDetails(@KeyName("accessToken") String accessToken, @KeyName("accountNumber") String accountNumber) {
      return "Details for account " + accountNumber;
    }
  }

  @OperationName("Report failure")
  public static final class ReportFailureStep implements Returning<String> {

    @StepMethod("accountDetails")
    public String reportFailure() {
      return "Everything's ruined";
    }
  }

  /**
   * Wraps flow invocation, providing an easy way to set up the initial scratchpad.
   */
  public interface RunAccountDetailsFlow extends Returning<String> {
    String getAccountDetails(
        @KeyName("username") String username,
        @KeyName("password") String password,
        @KeyName("accountNumber") String accountNumber);
  }

  private static final FlowWrapperFactory factory = CompilingFlowWrapperFactory.with(
      Compilers.builder().loggingToConsole().build()
  );

  @Test
  public void runFlow() {
    Flow<String> getAccountDetailsFlow = factory.flowFor(new LoginStep(true))
        .thenOnSuccess(factory.flowFor(new GetAccountDetailsStep()))
        .otherwise(factory.flowFor(new ReportFailureStep()));

    System.out.println(Flows.prettyPrint(getAccountDetailsFlow));

    RunAccountDetailsFlow runner = factory.proxyFor(RunAccountDetailsFlow.class, getAccountDetailsFlow);

    assertEquals("Everything's ruined", runner.getAccountDetails("Bob", "password", "Account01"));
  }
}
