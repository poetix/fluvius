package com.codepoetics.fluvius.preconditions;

public final class Preconditions {

    private Preconditions() {
    }

    public static <T> T checkNotNull(String name, T value) {
        if (name == null) {
            throw new NullPointerException("name must not be null");
        }
        if (value == null) {
            throw new NullPointerException(name + " must not be null");
        }
        return value;
    }

    public static void checkArgument(String description, boolean isValid) {
        if (!isValid) {
            throw new IllegalArgumentException(description);
        }
    }
}
