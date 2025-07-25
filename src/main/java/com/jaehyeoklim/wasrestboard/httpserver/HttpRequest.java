package main.java.com.jaehyeoklim.wasrestboard.httpserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static main.java.com.jaehyeoklim.wasrestboard.util.Logger.log;

public class HttpRequest {

    private String method;
    private String path;
    private final Map<String, String> queryParameters = new HashMap<>();

    public HttpRequest(BufferedReader bufferedReader) throws IOException {
        parseRequestLine(bufferedReader);
    }

    /**
     * 첫 번째 요청 라인(GET /path?name=abc HTTP/1.1 등)을 파싱해서
     * HTTP 메서드와 경로, 쿼리 파라미터 정보를 추출.
     *
     * <p>예시 입력: "GET /users?id=1 HTTP/1.1"</p>
     * <p>→ method = "GET", path = "/users", query = {id=1}</p>
     *
     * @param bufferedReader 요청 입력 스트림 (요청 라인 포함)
     * @throws IOException 요청 라인이 없거나 잘못된 형식일 경우 발생
     */
    private void parseRequestLine(BufferedReader bufferedReader) throws IOException {
        String requestLine = bufferedReader.readLine();

        if (requestLine == null || requestLine.isBlank()) {
            throw new IOException("Empty request line");
        }
        log("Raw request line: " + requestLine);

        String[] parts = requestLine.split(" ");
        if (parts.length < 2) {
            throw new IOException("Invalid request line: " + requestLine);
        }
        log("Raw request parts " + Arrays.toString(parts));

        method = parts[0];
        log("Parse Method: " + method);

        String[] pathParts = parts[1].split("\\?");
        path = pathParts[0];
        log("Parse path: " + path);

        parseQueryParameters(pathParts);
        log("Parse query parameters: " + queryParameters);
    }

    /**
     * 경로에 포함된 쿼리스트링을 파싱해 key-value 쌍으로 저장.
     *
     * <p>예시: "/users?id=1&name=kim"</p>
     * <p>→ queryParameters = {id=1, name=kim}</p>
     *
     * @param pathParts [0]: 경로, [1]: 쿼리 파라미터 문자열
     */
    private void parseQueryParameters(String[] pathParts) {
        if (pathParts.length > 1) {
            for (String param : pathParts[1].split("&")) {
                String[] keyValue = param.split("=");
                String key = keyValue[0];
                String value = keyValue.length > 1 ? keyValue[1] : "";
                queryParameters.put(key, value);
            }
        }
    }

    public String getPath() {
        return path;
    }

    @Override
    public String toString() {
        return "HttpRequest{method=" + method + ", path=" + path + ", query=" + queryParameters + "}";
    }
}
