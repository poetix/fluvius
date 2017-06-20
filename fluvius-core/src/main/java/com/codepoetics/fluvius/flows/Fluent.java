package com.codepoetics.fluvius.flows;

import com.codepoetics.fluvius.api.scratchpad.Key;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

final class Fluent {

  private Fluent() {
  }

  static <OUTPUT> TargetCapture<OUTPUT> targetCapture(Key<OUTPUT> target) {
    return new TargetCapture<>(target);
  }

  static InputKeysCapture inputKeysCapture(Key<?>... inputKeys) {
    return inputKeysCapture(new HashSet<>(Arrays.asList(inputKeys)));
  }

  private static InputKeysCapture inputKeysCapture(Set<Key<?>> inputKeys) {
    return new InputKeysCapture(inputKeys);
  }

}
