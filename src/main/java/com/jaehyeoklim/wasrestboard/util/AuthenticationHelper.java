package main.java.com.jaehyeoklim.wasrestboard.util;

import main.java.com.jaehyeoklim.wasrestboard.httpserver.HttpRequest;
import main.java.com.jaehyeoklim.wasrestboard.httpserver.HttpResponse;
import main.java.com.jaehyeoklim.wasrestboard.user.domain.User;

import static main.java.com.jaehyeoklim.wasrestboard.httpserver.enums.HttpStatus.*;
import static main.java.com.jaehyeoklim.wasrestboard.session.SessionManager.getSession;
import static main.java.com.jaehyeoklim.wasrestboard.util.Logger.log;

public final class AuthenticationHelper {

    private AuthenticationHelper() {}

    public static User getAuthenticatedUser(HttpRequest request, HttpResponse response) {
        String sessionId = request.getCookie("sessionId");
        User user = (sessionId != null) ? getSession(sessionId) : null;

        if (user == null) {
            log("Unauthorized access attempt");
            response.setStatusCode(UNAUTHORIZED);
            response.writeBody("Unauthorized access. ");
            response.writeBody("<a href='/'>Back to Home</a>");
            response.flush();
        }

        return user;
    }
}
