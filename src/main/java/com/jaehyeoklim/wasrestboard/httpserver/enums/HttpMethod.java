package main.java.com.jaehyeoklim.wasrestboard.httpserver.enums;


/**
 * HTTP 요청 메서드(enum).
 *
 * <p>RESTful API에서 사용되는 대표적인 HTTP 메서드 정의</p>
 * <ul>
 *     <li>GET: 리소스 조회</li>
 *     <li>POST: 리소스 생성</li>
 *     <li>PUT: 리소스 전체 수정</li>
 *     <li>DELETE: 리소스 삭제</li>
 * </ul>
 */
public enum HttpMethod {
    GET,
    POST,
    PUT,
    DELETE
}
