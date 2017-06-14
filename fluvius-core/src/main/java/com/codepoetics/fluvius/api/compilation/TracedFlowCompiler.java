package com.codepoetics.fluvius.api.compilation;

import com.codepoetics.fluvius.api.Flow;
import com.codepoetics.fluvius.api.FlowExecution;
import com.codepoetics.fluvius.api.tracing.TracedFlowExecution;

public interface TracedFlowCompiler {
  <T> TracedFlowExecution<T> compile(Flow<T> flow);
}
