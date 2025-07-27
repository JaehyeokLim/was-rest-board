package main.java.com.jaehyeoklim.wasrestboard.board.controller;

import main.java.com.jaehyeoklim.wasrestboard.board.domain.Post;
import main.java.com.jaehyeoklim.wasrestboard.board.repository.PostRepository;
import main.java.com.jaehyeoklim.wasrestboard.httpserver.HttpRequest;
import main.java.com.jaehyeoklim.wasrestboard.httpserver.HttpResponse;
import main.java.com.jaehyeoklim.wasrestboard.httpserver.servlet.annotation.Mapping;
import main.java.com.jaehyeoklim.wasrestboard.user.domain.User;

import java.time.format.DateTimeFormatter;
import java.util.List;

import static main.java.com.jaehyeoklim.wasrestboard.httpserver.enums.HttpMethod.*;
import static main.java.com.jaehyeoklim.wasrestboard.httpserver.enums.HttpStatus.OK;
import static main.java.com.jaehyeoklim.wasrestboard.httpserver.enums.HttpStatus.UNAUTHORIZED;
import static main.java.com.jaehyeoklim.wasrestboard.session.SessionManager.getSession;
import static main.java.com.jaehyeoklim.wasrestboard.util.Logger.log;

public class PostController {

    private final PostRepository postRepository;

    public PostController(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    private User getAuthenticatedUser(HttpRequest httpRequest, HttpResponse httpResponse) {
        String sessionId = httpRequest.getCookie("sessionId");
        User user = (sessionId != null) ? getSession(sessionId) : null;

        if (user == null) {
            log("Unauthorized access attempt");
            httpResponse.setStatusCode(UNAUTHORIZED);
            httpResponse.writeBody("Unauthorized access. ");
            httpResponse.writeBody("<a href='/'>Back to Home</a>");
            httpResponse.flush();
        }

        return user;
    }

    @Mapping(path = "/board", method = GET)
    public void board(HttpRequest httpRequest, HttpResponse httpResponse) {
        User user = getAuthenticatedUser(httpRequest, httpResponse);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("""
            <html>
                <style>
                    body { font-family: sans-serif; margin: 40px; }
                    form { margin-bottom: 20px; }
                </style>
            <body>
                <h1>Board</h1>
                <div>
                    <p>ID: %s</p>
                </div>
                <div>
                    <p>Name: %s</p>
                </div>
                <div>
                    <a href="/board/write" style="margin-right: 12px;">Write Post</a>
                    <a href="/">Back to Home</a>
                </div>
                <br>
                <br>
                <h1>Posts</h1>
            """.formatted(user.getLoginId(), user.getName()));

        List<Post> posts = postRepository.findAllPosts();
        for (Post post : posts) {
            stringBuilder.append("<div style='margin-top: 10px;'>")
                    .append("<h3>").append(post.getTitle()).append("</h3>")
                    .append("<p>").append(post.getOwner()).append("</p>")
                    .append("<p>").append(post.getContent()).append("</p>")
                    .append("<p>").append(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(post.getCreatedAt())).append("</p>")
                    .append("</div>")
                    .append("<div>")
                    .append("<a href=\"/board/edit?id=")
                    .append(post.getId())
                    .append("\" style=\"margin-right: 12px;\">Edit</a>")
                    .append("<a href=\"/board/delete\">Delete</a>")
                    .append("</div>")
                    .append("<br>")
                    .append("<br>");
        }
        stringBuilder.append("</body>");
        stringBuilder.append("</html>");

        httpResponse.setStatusCode(OK);
        httpResponse.writeBody(stringBuilder.toString());
        httpResponse.flush();
    }
}
