package main.java.com.jaehyeoklim.wasrestboard.httpserver.servlet.annotation;

import main.java.com.jaehyeoklim.wasrestboard.httpserver.enums.HttpMethod;

import java.lang.annotation.*;

/**
 * 컨트롤러 메서드에 HTTP 경로와 메서드를 매핑하기 위한 애노테이션.
 *
 * <p>예: @Mapping(path = "/users", method = HttpMethod.POST)</p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface Mapping {
    String path();
    HttpMethod method() default HttpMethod.GET;
}
