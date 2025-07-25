package main.java.com.jaehyeoklim.wasrestboard.httpserver.servlet.base;

import main.java.com.jaehyeoklim.wasrestboard.httpserver.HttpRequest;
import main.java.com.jaehyeoklim.wasrestboard.httpserver.HttpResponse;

import java.io.IOException;

/**
 * 모든 서블릿이 구현해야 하는 기본 인터페이스.
 *
 * <p>클라이언트의 요청이 들어오면, 이 service() 메서드가 실행.</p>
 *
 * <p>각 서블릿은 이 메서드를 오버라이드해서 요청에 대한 응답을 작성.</p>
 */
public interface HttpServlet {
    void service(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException;
}
