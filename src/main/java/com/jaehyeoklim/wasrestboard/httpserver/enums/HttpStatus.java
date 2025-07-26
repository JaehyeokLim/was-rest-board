package main.java.com.jaehyeoklim.wasrestboard.httpserver.enums;

/**
 * HTTP 상태 코드(Enum).
 *
 * <p>각 상태는 숫자 코드와 메시지로 구성된다.</p>
 * <p>예: 200 OK, 404 Not Found 등</p>
 */
public enum HttpStatus {
    OK(200, "OK"),
    CREATED(201, "Created"),
    BAD_REQUEST(400, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Not Found"),
    CONFLICT(409, "Conflict"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error");

    private final int code;
    private final String message;

    HttpStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static HttpStatus valueOf(int code) {
        for (HttpStatus httpStatus : HttpStatus.values()) {
            if (httpStatus.getCode() == code) {
                return httpStatus;
            }
        }

        throw new IllegalArgumentException("HttpStatus code " + code + " not found");
    }
}
