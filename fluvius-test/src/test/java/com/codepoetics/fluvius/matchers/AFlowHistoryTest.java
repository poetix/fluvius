package com.codepoetics.fluvius.matchers;

import com.codepoetics.fluvius.api.tracing.FlowStepType;
import com.codepoetics.fluvius.test.builders.TestFlowEvent;
import com.codepoetics.fluvius.test.builders.TestFlowHistory;
import com.codepoetics.fluvius.test.builders.TestTraceMap;
import com.codepoetics.fluvius.test.matchers.AFlowEvent;
import com.codepoetics.fluvius.test.matchers.AFlowHistory;
import com.codepoetics.fluvius.test.matchers.ATraceMap;
import org.junit.Test;

import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

public class AFlowHistoryTest {

  @Test
  public void matchesAMatchingTraceMap() {
    UUID flowId = UUID.randomUUID();
    UUID firstStepId = UUID.randomUUID();

    assertThat(
        TestFlowHistory
          .withFlowId(flowId)

          .withTraceMap(TestTraceMap
              .ofType(FlowStepType.BRANCH)
              .withId(firstStepId)
              .withRequiredKeys("A", "B")
              .withProvidedKey("Result")
              .withChildren(
                  TestTraceMap.ofType(FlowStepType.STEP),
                  TestTraceMap.ofType(FlowStepType.SEQUENCE)))

          .withEventHistory(
              TestFlowEvent.started(flowId, firstStepId),
              TestFlowEvent.succeeded(flowId, firstStepId, true)),

        AFlowHistory
            .withFlowId(flowId)

            .withTraceMap(ATraceMap.ofType(FlowStepType.BRANCH)
              .withRequiredKeys("A", "B")
              .withProvidedKey(containsString("sul"))
              .withChildren(ATraceMap.ofAnyType(), ATraceMap.ofAnyType()))

            .withEventHistory(
                AFlowEvent.stepStarted().withStepId(firstStepId),
                AFlowEvent.stepSucceeded((Object) true).withStepId(firstStepId)
            )

    );
  }
}
