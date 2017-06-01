package com.codepoetics.fluvius.exceptions;

import com.codepoetics.fluvius.api.scratchpad.Key;

import java.util.Set;

public class MissingKeysException extends RuntimeException {
    private final Set<Key<?>> missingKeys;

    public MissingKeysException(Set<Key<?>> missingKeys) {
        this.missingKeys = missingKeys;
    }

    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("Missing keys: ");
        boolean isFirst = true;

        for (Key<?> key : missingKeys) {
            if (isFirst) {
                isFirst = false;
            } else {
                sb.append(",");
            }
            sb.append(key.getName());
        }

        return sb.toString();
    }
}
