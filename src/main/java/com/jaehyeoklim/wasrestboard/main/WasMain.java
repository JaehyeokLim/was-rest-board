package main.java.com.jaehyeoklim.wasrestboard.main;

import main.java.com.jaehyeoklim.wasrestboard.board.controller.PostController;
import main.java.com.jaehyeoklim.wasrestboard.board.repository.PostRepository;
import main.java.com.jaehyeoklim.wasrestboard.httpserver.HttpServer;
import main.java.com.jaehyeoklim.wasrestboard.httpserver.servlet.ServletManager;
import main.java.com.jaehyeoklim.wasrestboard.httpserver.servlet.annotation.AnnotationServlet;
import main.java.com.jaehyeoklim.wasrestboard.httpserver.servlet.system.DiscardServlet;
import main.java.com.jaehyeoklim.wasrestboard.user.controller.UserController;
import main.java.com.jaehyeoklim.wasrestboard.user.repository.UserRepository;

import java.io.IOException;
import java.util.List;

public class WasMain {
    private static final int PORT = 54321;

    public static void main(String[] args) throws IOException {
        UserRepository userRepository = new UserRepository();
        PostRepository postRepository = new PostRepository();

        WasMainController wasMainController = new WasMainController();
        UserController userController = new UserController(userRepository);
        PostController postController = new PostController(postRepository);

        AnnotationServlet annotationServlet = new AnnotationServlet(List.of(wasMainController, userController, postController));

        DiscardServlet discardServlet = new DiscardServlet();

        ServletManager servletManager = new ServletManager();
        servletManager.setDefaultServlet(annotationServlet);
        servletManager.addToServletMap("/favicon.ico", discardServlet);

        HttpServer server = new HttpServer(PORT,  servletManager);
        server.start();
    }
}
