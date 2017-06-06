package com.codepoetics.fluvius.api.scratchpad;

import java.io.Serializable;

/**
 * A pair of a Key and an associated value, which may be added to a Scratchpad's storage.
 */
public interface KeyValue extends Serializable {

  /**
   * Store this key/value pair in the supplied ScratchpadStorage.
   *
   * @param storage The ScratchpadStorage to write this key/value pair into.
   */
  void store(ScratchpadStorage storage);

}
