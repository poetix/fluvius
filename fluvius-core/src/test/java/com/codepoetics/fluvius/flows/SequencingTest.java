package com.codepoetics.fluvius.flows;

import com.codepoetics.fluvius.api.Flow;
import com.codepoetics.fluvius.api.functional.ScratchpadFunction;
import com.codepoetics.fluvius.api.scratchpad.Key;
import com.codepoetics.fluvius.api.scratchpad.Scratchpad;
import com.codepoetics.fluvius.tracing.TraceMaps;
import org.hamcrest.Matchers;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;

public class SequencingTest {

  private static final Key<String> billingDetails = Key.named("billingDetails");
  private static final Key<String> paymentAmount = Key.named("paymentAmount");
  private static final Key<String> customerId = Key.named("customerId");
  private static final Key<String> productBasket = Key.named("productBasket");
  private static final Key<String> paymentReference = Key.named("paymentReference");
  private static final Key<String> orderReference = Key.named("orderReference");
  private static final Key<String> purchaseOutcome = Key.named("purchaseOutcome");
  private static final Key<String> status = Key.named("status");

  private static final ScratchpadFunction<String> NO_OP = new ScratchpadFunction<String>() {
    @Override
    public String apply(Scratchpad input) throws Exception {
      return "";
    }
  };

  private static final Flow<String> makePayment = Flows.from(billingDetails, paymentAmount).to(paymentReference).using("Make payment", NO_OP);
  private static final Flow<String> releaseStock = Flows.from(productBasket).to(status).using("Release stock", NO_OP);
  private static final Flow<String> paymentFailed = Flows.from(paymentReference).to(purchaseOutcome).using("Payment failed", NO_OP);
  private static final Flow<String> placeOrder = Flows.from(customerId, paymentReference, productBasket).to(orderReference).using("Place order", NO_OP);
  private static final Flow<String> success = Flows.from(orderReference).to(purchaseOutcome).using("Return success", NO_OP);

  @Test
  public void requiredKeysForSequenceAreAllKeysNotProvidedWithinSequence() {
    Flow<String> sequence = makePayment.branchOnResult()
        .onFailure(releaseStock.then(paymentFailed))
        .otherwise(placeOrder.then(success));

    System.out.println(Flows.prettyPrint(sequence));

    assertThat(sequence.getRequiredKeys(), Matchers.<Key<?>>containsInAnyOrder(
        billingDetails,
        paymentAmount,
        customerId,
        productBasket));
  }
}
