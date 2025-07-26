package main.java.com.jaehyeoklim.wasrestboard.main;

import main.java.com.jaehyeoklim.wasrestboard.httpserver.HttpRequest;
import main.java.com.jaehyeoklim.wasrestboard.httpserver.HttpResponse;
import main.java.com.jaehyeoklim.wasrestboard.httpserver.servlet.annotation.Mapping;
import main.java.com.jaehyeoklim.wasrestboard.user.domain.User;

import java.io.IOException;

import static main.java.com.jaehyeoklim.wasrestboard.httpserver.enums.HttpMethod.*;
import static main.java.com.jaehyeoklim.wasrestboard.session.SessionManager.*;
import static main.java.com.jaehyeoklim.wasrestboard.util.Logger.log;

public class WasMainController {

    @Mapping(path = "/", method = GET)
    public void mainForm(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        String sessionId = httpRequest.getCookie("sessionId");
        User user = null;

        if (sessionId != null) {
            try {
                user = getSession(sessionId);
                log("Session ID: " + sessionId);
                log("Checking if user is logged in: " + user);
            } catch (IllegalArgumentException e) {
                log("Invalid UUID format in sessionId cookie: " + sessionId);
            }
        }

        String html;
        if (user != null) {
            html = """
                <html>
                <body>
                    <h1>Hello!, %s!</h1>
                    <a href="/board">Board</a>
                    <a href="/profile">Profile</a>
                    <a href="/logout">Logout
                    </a>
                </body>
                </html>
                """.formatted(user.getName());
        } else {
            html = """
                <html>
                <head>
                    <style>
                        body { font-family: sans-serif; margin: 40px; }
                        form { margin-bottom: 20px; }
                        .form-group { margin-bottom: 15px; display: flex; align-items: center; }
                        .form-group label { width: 80px; }
                        .form-group input { padding: 5px; font-size: 1em; }
                    </style>
                </head>
                <body>
                    <h1>Welcome to the WAS Rest Board</h1>
                    <h2>Login</h2>
                    <form action="/login" method="POST">
                        <div class="form-group">
                            <label for="loginId">ID:</label>
                            <input type="text" id="loginId" name="loginId" required>
                        </div>
                        <div class="form-group">
                            <label for="password">Password:</label>
                            <input type="password" id="password" name="password" required>
                        </div>
                        <button type="submit">Submit</button>
                    </form>
        
                    <a href="/signup">Sign Up</a>
                </body>
                </html>
                """;
        }

        httpResponse.writeBody(html);
    }
}
