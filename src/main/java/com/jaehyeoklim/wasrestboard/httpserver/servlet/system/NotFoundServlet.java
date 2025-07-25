package main.java.com.jaehyeoklim.wasrestboard.httpserver.servlet.system;

import main.java.com.jaehyeoklim.wasrestboard.httpserver.HttpRequest;
import main.java.com.jaehyeoklim.wasrestboard.httpserver.HttpResponse;
import main.java.com.jaehyeoklim.wasrestboard.httpserver.servlet.base.HttpServlet;

import java.io.IOException;

public class NotFoundServlet implements HttpServlet {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws IOException {
        response.setStatusCode(404);
        response.writeBody("<h1>404 Not Found</h1>");
    }
}
