package main.java.com.jaehyeoklim.wasrestboard.httpserver.servlet.annotation;

import main.java.com.jaehyeoklim.wasrestboard.httpserver.enums.HttpMethod;

import java.util.Objects;

/**
 * HTTP 요청 경로(path)와 메서드(GET, POST 등)를 조합한 키.
 *
 * <p>AnnotationServlet에서 경로와 메서드에 따라 컨트롤러 메서드를 매핑하기 위해 사용됨</p>
 */
public class MappingKey {

    private final String path;
    private final HttpMethod method;

    public MappingKey(String path, HttpMethod method) {
        this.path = path;
        this.method = method;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        MappingKey that = (MappingKey) object;
        return Objects.equals(path, that.path) && method == that.method;
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, method);
    }
}
