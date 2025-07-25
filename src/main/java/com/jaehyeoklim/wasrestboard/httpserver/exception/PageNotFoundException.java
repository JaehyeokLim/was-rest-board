package main.java.com.jaehyeoklim.wasrestboard.httpserver.exception;

public class PageNotFoundException extends RuntimeException {
    public PageNotFoundException(String message) {
        super(message);
    }
}
