package main.java.com.jaehyeoklim.wasrestboard.httpserver.servlet.annotation;

import java.lang.annotation.*;


/**
 * HTTP 요청 경로와 컨트롤러 메서드를 연결해주는 애노테이션.
 *
 * <p>예를 들어, @Mapping("/users")가 붙은 메서드는
 * 클라이언트가 /users 경로로 요청을 보냈을 때 실행.</p>
 *
 * <p>AnnotationServlet이 이 애노테이션을 읽어 메서드를 자동으로 등록하고 실행.</p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface Mapping {
    String value();
}
