package main.java.com.jaehyeoklim.wasrestboard.session;

import main.java.com.jaehyeoklim.wasrestboard.user.domain.User;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static main.java.com.jaehyeoklim.wasrestboard.util.UUIDGenerator.*;

public class SessionManager {

    private static final Map<String, User> sessionStore = new ConcurrentHashMap<>();

    public static String createSession(User user) {
        String id = generateUUID().toString();
        sessionStore.put(id, user);
        return id;
    }

    public static User getSession(String id) {
        return sessionStore.get(id);
    }

    public static User removeSession(String id) {
        return sessionStore.remove(id);
    }
}
