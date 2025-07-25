package main.java.com.jaehyeoklim.wasrestboard.httpserver.servlet.system;

import main.java.com.jaehyeoklim.wasrestboard.httpserver.HttpRequest;
import main.java.com.jaehyeoklim.wasrestboard.httpserver.HttpResponse;
import main.java.com.jaehyeoklim.wasrestboard.httpserver.servlet.base.HttpServlet;

import java.io.IOException;

public class InternalErrorServlet implements HttpServlet {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws IOException {
        response.setStatusCode(400);
        response.writeBody("<h1>Internal Error</h1>");
    }
}
