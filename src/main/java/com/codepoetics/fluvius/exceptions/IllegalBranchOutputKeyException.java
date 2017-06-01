package com.codepoetics.fluvius.exceptions;

import com.codepoetics.fluvius.api.scratchpad.Key;

public class IllegalBranchOutputKeyException extends RuntimeException {

    private final Key<?> defaultOutputKey;
    private final String branchDescription;
    private final Key<?> conditionalOutputKey;

    public IllegalBranchOutputKeyException(Key<?> defaultOutputKey, String branchDescription, Key<?> conditionalOutputKey) {
        this.defaultOutputKey = defaultOutputKey;
        this.branchDescription = branchDescription;
        this.conditionalOutputKey = conditionalOutputKey;
    }

    @Override
    public String getMessage() {
        return "Branch " + branchDescription
                + " outputs key " + conditionalOutputKey.getName()
                + " but default branch outputs key " + defaultOutputKey.getName();
    }
}
