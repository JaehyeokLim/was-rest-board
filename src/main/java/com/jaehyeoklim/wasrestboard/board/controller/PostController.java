package main.java.com.jaehyeoklim.wasrestboard.board.controller;

import main.java.com.jaehyeoklim.wasrestboard.board.domain.Post;
import main.java.com.jaehyeoklim.wasrestboard.board.repository.PostRepository;
import main.java.com.jaehyeoklim.wasrestboard.httpserver.HttpRequest;
import main.java.com.jaehyeoklim.wasrestboard.httpserver.HttpResponse;
import main.java.com.jaehyeoklim.wasrestboard.httpserver.servlet.annotation.Mapping;
import main.java.com.jaehyeoklim.wasrestboard.user.domain.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import static main.java.com.jaehyeoklim.wasrestboard.httpserver.enums.HttpMethod.*;
import static main.java.com.jaehyeoklim.wasrestboard.httpserver.enums.HttpStatus.*;
import static main.java.com.jaehyeoklim.wasrestboard.util.AuthenticationHelper.*;
import static main.java.com.jaehyeoklim.wasrestboard.util.Logger.log;
import static main.java.com.jaehyeoklim.wasrestboard.util.UUIDGenerator.*;

public class PostController {

    private final PostRepository postRepository;

    public PostController(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    private Post validateOwnership(HttpRequest request, HttpResponse response, User user) {
        UUID postId = UUID.fromString(request.getParameter("id"));
        Post post = postRepository.findById(postId);

        if (!user.getLoginId().equals(post.getOwner())) {
            log("Unauthorized access attempt");
            response.setStatusCode(UNAUTHORIZED);
            response.writeBody("Unauthorized access. ");
            response.writeBody("<a href='/'>Back to Home</a>");
            response.flush();
            return null;
        }

        return post;
    }

    private String renderPostForm(String action, String title, String content, String heading) {
        return """
        <html>
            <style>
                body { font-family: sans-serif; margin: 40px; }
                form { margin-bottom: 20px; }
                input[type='text'], textarea {
                    width: 300;
                    padding: 8px;
                    margin-bottom: 12px;
                    border: 1px solid #ccc;
                    border-radius: 4px;
                }
            </style>
        <body>
            <h1>%s</h1>
            <form action="%s" method="POST">
                <label>Title</label><br>
                <input type="text" name="title" value="%s" required><br>

                <label>Content</label><br>
                <textarea name="content" rows="10" required>%s</textarea><br>

                <button type="submit">Submit</button>
            </form>
            <a href="/board">Back to Board</a>
        </body>
        </html>
    """.formatted(heading, action, title, content);
    }

    @Mapping(path = "/board", method = GET)
    public void board(HttpRequest httpRequest, HttpResponse httpResponse) {
        User user = getAuthenticatedUser(httpRequest, httpResponse);
        if (user == null) return;

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
                <h2>Posts</h2>
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
                    .append("<a href=\"/board/delete?id=")
                    .append(post.getId())
                    .append("\">Delete</a>")
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

    @Mapping(path = "/board/write", method = GET)
    public void writeForm(HttpRequest request, HttpResponse response) {
        User user = getAuthenticatedUser(request, response);
        if (user == null) return;

        String html = renderPostForm("/board/write", "", "", "Write a Post");
        response.setStatusCode(OK);
        response.writeBody(html);
        response.flush();
    }

    @Mapping(path = "/board/edit", method = GET)
    public void editForm(HttpRequest httpRequest, HttpResponse httpResponse) {
        User user = getAuthenticatedUser(httpRequest, httpResponse);
        if (user == null) return;
        Post post = validateOwnership(httpRequest, httpResponse, user);
        if (post == null) return;

        String html = renderPostForm("/board/edit?id=" + post.getId(), post.getTitle(), post.getContent(), "Edit Post");
        httpResponse.setStatusCode(OK);
        httpResponse.writeBody(html);
        httpResponse.flush();
    }

    @Mapping(path = "/board/write", method = POST)
    public void write(HttpRequest httpRequest, HttpResponse httpResponse) {
        User user = getAuthenticatedUser(httpRequest, httpResponse);
        if (user == null) return;

        String title =  httpRequest.getParameter("title");
        String content = httpRequest.getParameter("content");

        Post newPost = new Post(
                generateUUID(),
                user.getLoginId(),
                LocalDateTime.now(),
                title,
                content
        );
        postRepository.add(newPost);
        log("Successfully added post");

        httpResponse.setStatusCode(CREATED);
        httpResponse.writeBody("Successfully wrote post! ");
        httpResponse.writeBody("<a href='/board'>Back to Board</a>");
        httpResponse.flush();
    }

    @Mapping(path = "/board/edit", method = POST)
    public void edit(HttpRequest httpRequest, HttpResponse httpResponse) {
        User user = getAuthenticatedUser(httpRequest, httpResponse);
        if (user == null) return;
        Post post = validateOwnership(httpRequest, httpResponse, user);
        if (post == null) return;

        String title =  httpRequest.getParameter("title");
        String content = httpRequest.getParameter("content");

        postRepository.update(post.getId().toString(), title, content);
        log("Successfully updated post");

        httpResponse.setStatusCode(OK);
        httpResponse.writeBody("Successfully updated post! ");
        httpResponse.writeBody("<a href='/board'>Back to Board</a>");
        httpResponse.flush();
    }

    @Mapping(path = "/board/delete", method = GET)
    public void deleteForm(HttpRequest httpRequest, HttpResponse httpResponse) {
        User user = getAuthenticatedUser(httpRequest, httpResponse);
        if (user == null) return;
        Post post = validateOwnership(httpRequest, httpResponse, user);
        if (post == null) return;

        String html = """
        <html>
            <style>
                body { font-family: sans-serif; margin: 40px; }
                form { margin-bottom: 20px; }
            </style>
        <body>
            <h1>Delete Post</h1>
            <p>Are you sure you want to delete your post?</p>
            <p><strong>This action is irreversible and your data will be deleted immediately.</strong></p>
            <form action="/board/delete?id=%s" method="POST">
                <button type="submit">Confirm Deletion</button>
            </form>
            <br>
            <a href="/board">Go Back</a>
        </body>
        </html>
        """.formatted(post.getId());

        httpResponse.setStatusCode(OK);
        httpResponse.writeBody(html);
        httpResponse.flush();
    }

    @Mapping(path = "/board/delete", method = POST)
    public void delete(HttpRequest httpRequest, HttpResponse httpResponse) {
        User user = getAuthenticatedUser(httpRequest, httpResponse);
        if (user == null) return;
        Post post = validateOwnership(httpRequest, httpResponse, user);
        if (post == null) return;

        postRepository.deleteByPostId(post.getId().toString());
        log("Successfully deleted post");

        httpResponse.setStatusCode(OK);
        httpResponse.writeBody("Successfully deleted post!");
        httpResponse.writeBody("<a href='/board'>Back to Board</a>");
        httpResponse.flush();
    }
}
