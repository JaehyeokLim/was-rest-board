package main.java.com.jaehyeoklim.wasrestboard.util;

import java.util.UUID;

public final class UUIDGenerator {

    private UUIDGenerator() {}

    public static UUID generateUUID() {
        return UUID.randomUUID();
    }
}
