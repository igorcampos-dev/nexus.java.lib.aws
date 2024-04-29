package com.nexus.aws.util;

import java.util.Collection;

public final class Objects {

    public static <T extends Collection<?>> void requireNonEmpty(T collection, RuntimeException e) {
        if (collection == null || collection.isEmpty()) {
            throw e;
        }
    }

    public static void throwIfTrue(boolean condition, RuntimeException exception) {
        if (condition) {
            throw exception;
        }
    }

    public static void throwIfFalse(boolean condition, RuntimeException exception) {
        if (!condition) {
            throw exception;
        }
    }

}