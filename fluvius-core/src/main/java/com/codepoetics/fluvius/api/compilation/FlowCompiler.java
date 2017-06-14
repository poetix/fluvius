package com.codepoetics.fluvius.api.compilation;

import com.codepoetics.fluvius.api.Flow;
import com.codepoetics.fluvius.api.FlowExecution;

public interface FlowCompiler {
  <T> FlowExecution<T> compile(Flow<T> flow);
}
