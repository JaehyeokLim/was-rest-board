package main.java.com.jaehyeoklim.wasrestboard.user.controller;

import main.java.com.jaehyeoklim.wasrestboard.httpserver.HttpRequest;
import main.java.com.jaehyeoklim.wasrestboard.httpserver.HttpResponse;
import main.java.com.jaehyeoklim.wasrestboard.httpserver.servlet.annotation.Mapping;
import main.java.com.jaehyeoklim.wasrestboard.user.domain.User;
import main.java.com.jaehyeoklim.wasrestboard.user.repository.UserRepository;

import java.io.IOException;
import java.util.UUID;

import static main.java.com.jaehyeoklim.wasrestboard.httpserver.enums.HttpMethod.*;
import static main.java.com.jaehyeoklim.wasrestboard.httpserver.enums.HttpStatus.*;
import static main.java.com.jaehyeoklim.wasrestboard.session.SessionManager.*;
import static main.java.com.jaehyeoklim.wasrestboard.util.Logger.log;
import static main.java.com.jaehyeoklim.wasrestboard.util.PasswordEncoder.*;
import static main.java.com.jaehyeoklim.wasrestboard.util.UUIDGenerator.*;

public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Mapping(path = "/login", method = POST)
    public void login(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        String loginId = httpRequest.getParameter("loginId");
        String password = httpRequest.getParameter("password");

        User user = userRepository.findByLoginId(loginId);

        if (user == null) {
            log("Login id not found");
            httpResponse.setStatusCode(NOT_FOUND);
            httpResponse.writeBody("Login failed. Invalid ID or Password");
            httpResponse.writeBody("<a href='/'>Back to Home</a>");
            httpResponse.flush();
            return;
        }

        if (!matches(password, user.getPassword())) {
            log("Passwords don't match");
            httpResponse.setStatusCode(UNAUTHORIZED);
            httpResponse.writeBody("Login failed. Invalid ID or Password");
            httpResponse.writeBody("<a href='/'>Back to Home</a>");
            httpResponse.flush();
            return;
        }

        String sessionId = createSession(user);
        httpResponse.addCookie("sessionId", sessionId);

        log("Successfully logged in");
        httpResponse.setStatusCode(OK);
        httpResponse.writeBody("Successfully logged in! ");
        httpResponse.writeBody("<a href='/'>Go to Home</a>");
        httpResponse.flush();
    }

    @Mapping(path = "/logout",  method = GET)
    public void logout(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        String sessionId = httpRequest.getCookie("sessionId");

        if (sessionId != null) {
            log("Successfully deleted logged in session: " +  sessionId);
            httpResponse.addCookie("sessionId", "deleted; Max-Age=0; Path=/");
            removeSession(sessionId);
        }

        log("Successfully logged out");
        httpResponse.setStatusCode(OK);
        httpResponse.writeBody("Successfully logged out! ");
        httpResponse.writeBody("<a href='/'>Go to Home</a>");
        httpResponse.flush();

    }

    @Mapping(path = "/signup", method = GET)
    public void signupForm(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        String html =
                """
                <html>
                <head>
                    <style>
                        body {
                            font-family: sans-serif;
                            margin: 40px;
                        }
                        form {
                            margin-bottom: 20px;
                        }
                        .form-group {
                            margin-bottom: 15px;
                            display: flex;
                            align-items: center;
                        }
                        .form-group label {
                            width: 80px;
                        }
                        .form-group input {
                            padding: 5px;
                            font-size: 1em;
                        }
                    </style>
                </head>
                <body>
                    <h1>Sign Up</h1>
        
                    <form action="/signup" method="POST">
                        <div class="form-group">
                            <label for="loginId">ID:</label>
                            <input type="text" id="loginId" name="loginId" required>
                        </div>
                        <div class="form-group">
                            <label for="password">Password:</label>
                            <input type="password" id="password" name="password" required>
                        </div>
                        <div class="form-group">
                            <label for="name">Name:</label>
                            <input type="text" id="name" name="name" required>
                        </div>
                        <button type="submit">Submit</button>
                    </form>
                </body>
                </html>
                """;

        httpResponse.writeBody(html);
    }

    @Mapping(path = "/signup", method = POST)
    public void signup(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        String loginId = httpRequest.getParameter("loginId");
        String hashedPassword = hash(httpRequest.getParameter("password"));
        String name = httpRequest.getParameter("name");

        if (userRepository.findByLoginId(loginId) != null) {
            log("ID already exists");
            httpResponse.setStatusCode(CONFLICT);
            httpResponse.writeBody("ID already exists, please try again");
            httpResponse.flush();
            return;
        }

        UUID uuid = generateUUID();
        User user = new User(uuid, loginId, hashedPassword,  name);
        userRepository.add(user);

        log("Successfully added user");
        httpResponse.setStatusCode(CREATED);
        httpResponse.writeBody("Successfully signed up! ");
        httpResponse.writeBody("<a href='/'>Back to Home</a>");
        httpResponse.flush();
    }
}
