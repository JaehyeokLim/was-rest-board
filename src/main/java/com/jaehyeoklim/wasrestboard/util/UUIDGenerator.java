package main.java.com.jaehyeoklim.wasrestboard.util;

import java.util.UUID;

public abstract class UUIDGenerator {

    public static UUID generateUUID() {
        return UUID.randomUUID();
    }
}
