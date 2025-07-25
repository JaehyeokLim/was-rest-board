package main.java.com.jaehyeoklim.wasrestboard.httpserver;

import main.java.com.jaehyeoklim.wasrestboard.httpserver.servlet.ServletManager;

import java.io.*;
import java.net.Socket;

import static java.nio.charset.StandardCharsets.*;
import static main.java.com.jaehyeoklim.wasrestboard.util.Logger.log;

/**
* 클라이언트로부터 들어온 HTTP 요청을 처리하는 핸들러.
*
* <p>소켓 하나당 하나씩 실행되며,
* 요청을 파싱해 서블릿에 넘기고, 응답을 생성해 전송함.</p>
*
* <p>WAS 서버가 ThreadPool로 이 핸들러를 실행.</p>
*/
public class HttpRequestHandler implements Runnable {

    private final Socket socket;
    private final ServletManager servletManager;

    @Override
    public void run() {
        try {
            process();
        } catch (Exception e) {
            log("Client request handling failed: " + e.getMessage());
        }
    }

    public HttpRequestHandler(Socket socket, ServletManager servletManager) {
        this.socket = socket;
        this.servletManager = servletManager;
    }

    /**
    * 요청과 응답 스트림을 준비하고, 실제 요청 처리 메서드로 위임함.
    */
    private void process() throws IOException {
        try (socket;
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), UTF_8));
             PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), false, UTF_8);) {

            HttpRequest httpRequest = new HttpRequest(bufferedReader);
            HttpResponse httpResponse = new HttpResponse(printWriter);

            handleRequest(httpRequest, httpResponse);
        }
    }

    /**
     * 서블릿 매니저로 요청을 실행한 뒤 응답을 flush함.
     */
    private void handleRequest(HttpRequest request, HttpResponse response) throws IOException {
        log("Received HTTP request: " + request);
        servletManager.execute(request, response);
        response.flush();
        log("Sent HTTP response: " + response);
    }
}
