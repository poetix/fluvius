package com.codepoetics.fluvius.api.compilation;

import com.codepoetics.fluvius.api.Flow;
import com.codepoetics.fluvius.api.FlowExecution;
import com.codepoetics.fluvius.api.tracing.TracedFlowExecution;

/**
 * A compiler which can compile a {@link Flow} into a {@link TracedFlowExecution}.
 */
public interface TracedFlowCompiler {

  /**
   * Compile the provided {@link Flow} into a {@link TracedFlowExecution}.
   *
   * @param flow The flow to compile.
   * @param <T> The type of value returned by the flow.
   * @return A traced flow execution which will run the flow and return the value.
   */
  <T> TracedFlowExecution<T> compile(Flow<T> flow);
}
