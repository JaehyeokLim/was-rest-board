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

    @Mapping(path = "/logout", method = GET)
    public void logout(HttpRequest httpRequest, HttpResponse httpResponse) {
        String sessionId = httpRequest.getCookie("sessionId");

        if (sessionId != null) {
            String loginId = removeSession(sessionId).getLoginId();
            log("Successfully deleted logged in session: " +  loginId);
            httpResponse.addCookie("sessionId", "deleted; Max-Age=0; Path=/");
            log("Successfully deleted logged in session: " + sessionId);
        } else {
            log("No session ID found during logout");
        }

        log("Successfully logged out");
        httpResponse.setStatusCode(OK);
        httpResponse.writeBody("Successfully logged out! ");
        httpResponse.writeBody("<a href='/'>Go to Home</a>");
        httpResponse.flush();
    }

    @Mapping(path = "/signup", method = GET)
    public void signupForm(HttpResponse httpResponse) {
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
    public void signup(HttpRequest httpRequest, HttpResponse httpResponse) {
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


    @Mapping(path = "/account", method = GET)
    public void account(HttpRequest httpRequest, HttpResponse httpResponse) {
        String sessionId = httpRequest.getCookie("sessionId");
        User user = (sessionId != null) ? getSession(sessionId) : null;

        if (user == null) {
            log("Unauthorized access to account");
            httpResponse.setStatusCode(UNAUTHORIZED);
            httpResponse.writeBody("Unauthorized access to account! ");
            httpResponse.writeBody("<a href='/'>Back to Home</a>");
            httpResponse.flush();
            return;
        }

        String html = """
            <html>
            <body>
                <h1>Account</h1>
                <div>
                    <p>ID: %s</p>
                </div>
                <div>
                    <p>Name: %s</p>
                </div>
                <div>
                    <a href="/account/name">Edit Name</a>
                </div>
                <div>
                    <a href="/account/password">Edit Password</a>
                </div>
                <br>
                <div>
                    <a href="/account/delete">Delete Account</a>
                </div>
            </body>
            </html>
            """.formatted(user.getLoginId(), user.getName());

        httpResponse.setStatusCode(OK);
        httpResponse.writeBody(html);
        httpResponse.flush();
    }

    @Mapping(path = "/account/delete", method = GET)
    public void deleteAccountForm(HttpRequest httpRequest, HttpResponse httpResponse) {
        String sessionId = httpRequest.getCookie("sessionId");
        User user = (sessionId != null) ? getSession(sessionId) : null;

        if (user == null) {
            log("Unauthorized access to delete account");
            httpResponse.setStatusCode(UNAUTHORIZED);
            httpResponse.writeBody("Unauthorized access to delete account! ");
            httpResponse.writeBody("<a href='/'>Back to Home</a>");
            httpResponse.flush();
        }

        String html = """
        <html>
        <body>
            <h1>Delete Account</h1>
            <p>Are you sure you want to delete your account?</p>
            <p><strong>This action is irreversible and your data will be deleted immediately.</strong></p>
            <form action="/account/delete" method="POST">
                <button type="submit">Confirm Deletion</button>
            </form>
            <br>
            <a href="/account">Go Back</a>
        </body>
        </html>
        """;

        httpResponse.setStatusCode(OK);
        httpResponse.writeBody(html);
        httpResponse.flush();
    }

    @Mapping(path = "/account/delete", method = POST)
    public void deleteAccount(HttpRequest httpRequest, HttpResponse httpResponse) {
        String sessionId = httpRequest.getCookie("sessionId");
        User user = (sessionId != null) ? getSession(sessionId) : null;

        if (user == null) {
            log("Unauthorized access to delete account");
            httpResponse.setStatusCode(UNAUTHORIZED);
            httpResponse.writeBody("Unauthorized access to delete account! ");
            httpResponse.writeBody("<a href='/'>Back to Home</a>");
            httpResponse.flush();
        }

        String loginId = removeSession(sessionId).getLoginId();
        httpResponse.addCookie("sessionId", "deleted; Max-Age=0; Path=/");
        log("Successfully deleted logged in session: " + sessionId);

        userRepository.deleteByLoginId(loginId);
        log("Successfully deleted logged in user!");

        httpResponse.setStatusCode(OK);
        httpResponse.writeBody("Successfully deleted logged in user!");
        httpResponse.writeBody("<a href='/'>Go to Home</a>");
        httpResponse.flush();
    }
}
