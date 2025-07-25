package main.java.com.jaehyeoklim.wasrestboard.httpserver;

import main.java.com.jaehyeoklim.wasrestboard.httpserver.servlet.ServletManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static main.java.com.jaehyeoklim.wasrestboard.util.Logger.log;

public class HttpServer {
    private final ExecutorService es = Executors.newFixedThreadPool(10);
    private final ServletManager servletManager;

    private final int port;

    private ServerSocket serverSocket;

    public HttpServer(int port, ServletManager servletManager) {
        this.port = port;
        this.servletManager = servletManager;
    }

    public void start() throws IOException {
        log("Starting HTTP server on port " + port + "...");
        serverSocket = new ServerSocket(port);
        run();
    }

    private void run() {
        while (true) {
            try {
                log("Waiting for incoming connection on port " + port + "...");
                Socket socket = serverSocket.accept();
                log("New connection accepted from " + socket.getRemoteSocketAddress());

                HttpRequestHandler httpRequestHandler = new HttpRequestHandler(socket, servletManager);
                es.submit(httpRequestHandler);
            } catch (IOException e) {
                log("Failed to accept connection: " + e.getMessage());
            }
        }
    }
}
