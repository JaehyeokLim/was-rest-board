package main.java.com.jaehyeoklim.wasrestboard.httpserver;

import main.java.com.jaehyeoklim.wasrestboard.httpserver.enums.HttpStatus;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static main.java.com.jaehyeoklim.wasrestboard.httpserver.enums.HttpStatus.*;

public class HttpResponse {

    private final PrintWriter printWriter;
    private final StringBuilder bodyBuilder = new StringBuilder();
    private final List<String> setCookies = new ArrayList<>();

    private int statusCode = OK.getCode();

    public HttpResponse(PrintWriter printWriter) {
        this.printWriter = printWriter;
    }

    // 응답 헤더에 Set-Cookie를 추가, Path 전역, Http 접근만 허용.
    public void addCookie(String key, String value) {
        setCookies.add(key + "=" + value + "; Path=/; HttpOnly");
    }

    // 응답 헤더와 본문을 클라이언트로 전송
    public void flush() {
        int contentLength = bodyBuilder.toString().getBytes(UTF_8).length;
        printWriter.println("HTTP/1.1 " + statusCode + " " + valueOf(statusCode).getMessage());
        printWriter.println("Content-Type: " + "text/html; charset=UTF-8");
        printWriter.println("Content-Length: " + contentLength);

        // 모든 쿠키를 Set-Cookie 헤더로 추가
        for  (String cookie : setCookies) {
            printWriter.println("Set-Cookie: " + cookie);
        }

        printWriter.println();
        printWriter.println(bodyBuilder);
        printWriter.flush();
    }

    // 본문에 문자열 추가 (누적)
    public void writeBody(String body) {
        bodyBuilder.append(body);
    }

    // 상태 코드 설정 (enum 기반)
    public void setStatusCode(HttpStatus httpStatus) {
        this.statusCode = httpStatus.getCode();
    }

    @Override
    public String toString() {
        return "HttpResponse{statusCode=" + statusCode + ", reasonPhrase=" + valueOf(statusCode).getMessage() + '}';
    }
}
