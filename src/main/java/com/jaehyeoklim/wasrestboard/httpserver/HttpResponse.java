package main.java.com.jaehyeoklim.wasrestboard.httpserver;

import java.io.PrintWriter;

import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpResponse {

    private final PrintWriter printWriter;
    private final StringBuilder bodyBuilder = new StringBuilder();

    private int statusCode = 200;

    public HttpResponse(PrintWriter printWriter) {
        this.printWriter = printWriter;
    }

    /**
     * 응답 헤더와 본문을 클라이언트에게 전송.
     *
     * <p>- Content-Length를 계산하여 헤더에 포함.</p>
     * <p>- flush()가 호출되어야 실제 응답이 전송.</p>
     */
    public void flush() {
        int contentLength = bodyBuilder.toString().getBytes(UTF_8).length;
        printWriter.println("HTTP/1.1 " + statusCode + " " + getReasonPhrase(statusCode));
        printWriter.println("Content-Type: " + "text/html; charset=UTF-8");
        printWriter.println("Content-Length: " + contentLength);
        printWriter.println();
        printWriter.println(bodyBuilder);
        printWriter.flush();
    }

    /**
     * 응답 본문에 문자열을 추가.
     *
     * <p>여러 번 호출해도 누적 (StringBuilder 사용).</p>
     */
    public void writeBody(String body) {
        bodyBuilder.append(body);
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * 상태 코드에 따른 응답 메시지(reason phrase)를 반환.
     *
     * <p>예: 200 → "Success", 404 → "Not Found"</p>
     */
    private String getReasonPhrase(int statusCode) {
        return switch (statusCode) {
            case 200 -> "Success";
            case 404 -> "Not Found";
            case 500 -> "Internal Server Error";
            default -> "Unknown Error";
        };
    }

    @Override
    public String toString() {
        return "HttpResponse{statusCode=" + statusCode + ", reasonPhrase=" + getReasonPhrase(statusCode) + '}';
    }
}
