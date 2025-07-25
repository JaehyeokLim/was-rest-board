package main.java.com.jaehyeoklim.wasrestboard.httpserver.exception;

/**
 * 요청한 경로에 해당하는 페이지가 없을 때 던지는 예외.
 *
 * <p>HTTP 404 응답을 표현할 때 사용됨.</p>
 */
public class PageNotFoundException extends RuntimeException {
    public PageNotFoundException(String message) {
        super(message);
    }
}
