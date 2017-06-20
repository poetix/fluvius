package com.codepoetics.fluvius.exceptions;

import com.codepoetics.fluvius.api.scratchpad.Key;

/**
 * Thrown when a Flow is added to a branching flow which writes to a different key than the default branch.
 */
public final class IllegalBranchOutputKeyException extends RuntimeException {

  /**
   * Create an IllegalBranchOutputKeyException.
   * @param defaultOutputKey The output Key specified by the default branch.
   * @param branchDescription Description of the branch which specifies a different output key.
   * @param conditionalOutputKey The output key specified by the branch.
   * @return The exception.
   */
  public static IllegalBranchOutputKeyException create(Key<?> defaultOutputKey, String branchDescription, Key<?> conditionalOutputKey) {
    return new IllegalBranchOutputKeyException("Branch " + branchDescription
        + " outputs key " + conditionalOutputKey.getName()
        + " but default branch outputs key " + defaultOutputKey.getName());
  }

  private IllegalBranchOutputKeyException(String message) {
    super(message);
  }

}
