package main.java.com.jaehyeoklim.wasrestboard.httpserver.servlet;

import main.java.com.jaehyeoklim.wasrestboard.httpserver.HttpRequest;
import main.java.com.jaehyeoklim.wasrestboard.httpserver.HttpResponse;
import main.java.com.jaehyeoklim.wasrestboard.httpserver.exception.PageNotFoundException;
import main.java.com.jaehyeoklim.wasrestboard.httpserver.servlet.base.HttpServlet;
import main.java.com.jaehyeoklim.wasrestboard.httpserver.servlet.system.InternalErrorServlet;
import main.java.com.jaehyeoklim.wasrestboard.httpserver.servlet.system.NotFoundServlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static main.java.com.jaehyeoklim.wasrestboard.util.Logger.log;

public class ServletManager {

    private final Map<String, HttpServlet> servletMap = new HashMap<>();
    private final HttpServlet notFoundErrorServlet = new NotFoundServlet();
    private final HttpServlet internalErrorServlet = new InternalErrorServlet();
    private HttpServlet defaultServlet;

    public void addToServletMap(String path, HttpServlet servlet) {
        servletMap.put(path, servlet);
    }

    public void setDefaultServlet(HttpServlet defaultServlet) {
        this.defaultServlet = defaultServlet;
    }

    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        HttpServlet servlet = servletMap.getOrDefault(httpRequest.getPath(), defaultServlet);
        try {
            servlet.service(httpRequest, httpResponse);
        } catch (PageNotFoundException e) {
            log("404 Not Found: " + httpRequest.getPath());
            e.printStackTrace();
            notFoundErrorServlet.service(httpRequest, httpResponse);
        } catch (IOException e) {
            log("500 Internal Server Error: " + httpRequest.getPath() + " (" + e.getMessage() + ")");
            internalErrorServlet.service(httpRequest, httpResponse);
        }
    }
}
