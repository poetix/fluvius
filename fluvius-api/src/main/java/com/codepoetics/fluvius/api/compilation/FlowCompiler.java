package com.codepoetics.fluvius.api.compilation;

import com.codepoetics.fluvius.api.Flow;
import com.codepoetics.fluvius.api.FlowExecution;

/**
 * A compiler which can compile a {@link Flow} into a {@link FlowExecution}.
 */
public interface FlowCompiler {

  /**
   * Compile the provided {@link Flow} into a {@link FlowExecution}.
   *
   * @param flow The flow to compile.
   * @param <T> The type of value returned by the flow.
   * @return A flow execution which will run the flow and return the value.
   */
  <T> FlowExecution<T> compile(Flow<T> flow);
}
