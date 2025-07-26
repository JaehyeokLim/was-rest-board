package main.java.com.jaehyeoklim.wasrestboard.httpserver;

import main.java.com.jaehyeoklim.wasrestboard.httpserver.enums.HttpMethod;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.*;

public class HttpRequest {

    private String method;
    private String path;

    private final Map<String, String> queryParameters = new HashMap<>();
    private final Map<String, String> headers = new HashMap<>();

    public HttpRequest(BufferedReader bufferedReader) throws IOException {
        parseRequestLine(bufferedReader);
        parseHeaders(bufferedReader);
        parseBody(bufferedReader);
    }

    // 요청 라인 파싱 (메서드, 경로, 쿼리 파라미터)
    private void parseRequestLine(BufferedReader bufferedReader) throws IOException {
        String requestLine = bufferedReader.readLine();

        if (requestLine == null || requestLine.isBlank()) {
            throw new IOException("Empty request line");
        }

        String[] parts = requestLine.split(" ");
        if (parts.length < 2) {
            throw new IOException("Invalid request line: " + requestLine);
        }

        method = parts[0];

        String[] pathParts = parts[1].split("\\?");
        path = pathParts[0];

        if (pathParts.length > 1) {
            parseQueryParameters(pathParts[1]);
        }
    }

    // 쿼리 파라미터 파싱 (key=value)
    private void parseQueryParameters(String queryString) {
        for (String param : queryString.split("&")) {
            String[] keyValue = param.split("=");
            String key = keyValue[0];
            String value = keyValue.length > 1 ? URLDecoder.decode(keyValue[1], UTF_8) : "";
            queryParameters.put(key, value);
        }
    }

    // 헤더 파싱
    private void parseHeaders(BufferedReader reader) throws IOException {
        String line;
        while (!(line = reader.readLine()).isEmpty()) {
            String[] headerParts = line.split(":");
            headers.put(headerParts[0].trim(), headerParts[1].trim());
        }
    }

    // 요청 본문 파싱 (application/x-www-form-urlencoded 지원)
    private void parseBody(BufferedReader reader) throws IOException {
        if (!headers.containsKey("Content-Length")) return;

        int contentLength = Integer.parseInt(headers.get("Content-Length"));
        char[] bodyChars = new char[contentLength];
        int read = reader.read(bodyChars);

        if (read != contentLength) {
            throw new IOException("Failed to read body. Expected " + contentLength + ", but read " + read);
        }

        String body = new String(bodyChars);
        String contentType = headers.get("Content-Type");

        if ("application/x-www-form-urlencoded".equals(contentType)) {
            parseQueryParameters(body);
        }
    }

    public HttpMethod getMethod() {
        return HttpMethod.valueOf(method);
    }

    public String getPath() {
        return path;
    }

    public String getParameter(String name) {
        return queryParameters.get(name);
    }

    public String getCookie(String key) {
        String cookieHeader = headers.get("Cookie");
        if (cookieHeader == null) return null;

        // "Cookie: sessionId=abc; theme=dark" 와 같은 문자열을 파싱
        String[] cookieParts = cookieHeader.split(";");
        for (String cookiePart : cookieParts) {

            // key와 일치하는 쿠키가 있다면 값 반환
            String[] pair = cookiePart.trim().split("=");
            if (pair.length == 2 && pair[0].trim().equals(key)) {
                return pair[1];
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return "HttpRequest{method=" + method + ", path=" + path + ", query=" + queryParameters + "}";
    }
}