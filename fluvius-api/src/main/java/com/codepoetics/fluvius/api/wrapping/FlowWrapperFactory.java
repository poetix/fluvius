package com.codepoetics.fluvius.api.wrapping;

import com.codepoetics.fluvius.api.Flow;
import com.codepoetics.fluvius.api.functional.Returning;

public interface FlowWrapperFactory {

  <OUTPUT, T extends Returning<OUTPUT>> Flow<OUTPUT> flowFor(T function);
  <OUTPUT, T extends Returning<OUTPUT>> T proxyFor(Class<T> functionClass, Flow<OUTPUT> flow);

}
