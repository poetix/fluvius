package com.codepoetics.fluvius.wrapping;

import com.codepoetics.fluvius.api.Flow;
import com.codepoetics.fluvius.api.annotations.KeyName;
import com.codepoetics.fluvius.api.annotations.OperationName;
import com.codepoetics.fluvius.api.annotations.StepMethod;
import com.codepoetics.fluvius.api.functional.Predicate;
import com.codepoetics.fluvius.api.functional.Returning;
import com.codepoetics.fluvius.api.scratchpad.KeyProvider;
import com.codepoetics.fluvius.api.wrapping.FlowExecutionProxyFactory;
import com.codepoetics.fluvius.api.wrapping.FlowRunner;
import com.codepoetics.fluvius.api.wrapping.FlowWrapperFactory;
import com.codepoetics.fluvius.compilation.Compilers;
import com.codepoetics.fluvius.flows.Flows;
import com.codepoetics.fluvius.scratchpad.Keys;
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
    FlowRunner<String> getAccountDetails(
        @KeyName("username") String username,
        @KeyName("password") String password,
        @KeyName("accountNumber") String accountNumber);
  }

  private static final KeyProvider keyProvider = Keys.createProvider();
  private static final FlowWrapperFactory factory = Wrappers.createWrapperFactory(keyProvider);
  private static final FlowExecutionProxyFactory proxyFactory = Wrappers.createProxyFactory(Compilers.builder().loggingToConsole().build(), keyProvider);

  private static final Flow<String> loginFlow = factory.flowFor(new LoginStep(true));
  private static final Flow<String> getAccountDetailsFlow = factory.flowFor(new GetAccountDetailsStep());
  private static final Flow<String> reportFailureFlow = factory.flowFor(new ReportFailureStep());

  @Test
  public void runFlow() {
    Predicate<String> isEmpty = new Predicate<String>() {
      @Override
      public boolean test(String value) {
        return value.isEmpty();
      }
    };

    Flow<String> completeFlow = loginFlow.branchOnResult()
        .onFailure(reportFailureFlow)
        .onCondition("is empty", isEmpty, reportFailureFlow)
        .otherwise(getAccountDetailsFlow);

    System.out.println(Flows.prettyPrint(completeFlow));

    RunAccountDetailsFlow runner = proxyFactory.proxyFor(RunAccountDetailsFlow.class, completeFlow);

    assertEquals("Everything's ruined", runner.getAccountDetails("Bob", "password", "Account01").run());
  }
}
