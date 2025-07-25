package main.java.com.jaehyeoklim.wasrestboard.main;

import main.java.com.jaehyeoklim.wasrestboard.httpserver.HttpServer;
import main.java.com.jaehyeoklim.wasrestboard.httpserver.servlet.ServletManager;
import main.java.com.jaehyeoklim.wasrestboard.httpserver.servlet.annotation.AnnotationServlet;
import main.java.com.jaehyeoklim.wasrestboard.httpserver.servlet.system.DiscardServlet;

import java.io.IOException;
import java.util.List;

public class WasMain {
    private static final int PORT = 54321;

    public static void main(String[] args) throws IOException {
        WasMainController wasMainController = new WasMainController();
        AnnotationServlet annotationServlet = new AnnotationServlet(List.of(wasMainController));

        DiscardServlet discardServlet = new DiscardServlet();

        ServletManager servletManager = new ServletManager();
        servletManager.setDefaultServlet(annotationServlet);
        servletManager.addToServletMap("favicon.ico", discardServlet);

        HttpServer server = new HttpServer(PORT,  servletManager);
        server.start();
    }
}
