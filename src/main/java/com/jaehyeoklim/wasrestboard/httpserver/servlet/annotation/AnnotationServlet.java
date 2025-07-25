package main.java.com.jaehyeoklim.wasrestboard.httpserver.servlet.annotation;

import main.java.com.jaehyeoklim.wasrestboard.httpserver.HttpRequest;
import main.java.com.jaehyeoklim.wasrestboard.httpserver.HttpResponse;
import main.java.com.jaehyeoklim.wasrestboard.httpserver.exception.PageNotFoundException;
import main.java.com.jaehyeoklim.wasrestboard.httpserver.servlet.base.HttpServlet;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HTTP 요청 경로(Path)에 따라 컨트롤러 메서드를 호출하는 애노테이션 기반 디스패처 서블릿.
 *
 * <p>컨트롤러 클래스 내에서 {@code @Mapping} 애노테이션이 붙은 메서드를 탐색하여,
 * 해당 경로(path)를 key로 하는 매핑 정보를 내부 Map에 저장한다.</p>
 *
 * <p>요청이 들어오면 {@link HttpRequest#getPath()} 값을 기준으로
 * 등록된 메서드를 찾아 실행하며, 매핑이 없는 경우 {@code DefaultMethod}를 통해
 * {@link PageNotFoundException}을 발생시킨다.</p>
 *
 * <p>TCP 채팅 프로젝트의 {@code CommandDispatcher}와 유사하게,
 * HTTP 기반 요청에 특화된 경로 → 메서드 실행 디스패처 역할을 한다.</p>
 *
 * @see Mapping
 * @see ControllerMethod
 * @see HttpServlet
 */
public class AnnotationServlet implements HttpServlet {

    private final Map<String, ControllerMethod> pathMap;

    @Override
    public void service(HttpRequest request, HttpResponse response) throws IOException, NullPointerException {
        String path = request.getPath();
        ControllerMethod controllerMethod = pathMap.getOrDefault(path, new DefaultMethod());
        controllerMethod.invoke(request, response);
    }

    public AnnotationServlet(List<Object> controllers) {
        this.pathMap = new HashMap<>();
        initializePathMap(controllers);
    }

    /**
     * 컨트롤러 리스트를 순회하면서, 각 메서드에 붙은 @Mapping 경로를 파싱해
     * <p>경로(path) → 메서드 매핑 정보를 pathMap에 등록한다.</p>
     *
     * <p>- 중복된 경로 등록 시 IllegalStateException 발생</p>
     *
     * @param controllers HTTP 요청을 처리하는 컨트롤러 객체들
     */
    private void initializePathMap(List<Object> controllers) {
        for (Object controller : controllers) {
            Method[] methods = controller.getClass().getDeclaredMethods();

            for (Method method : methods) {
                if (method.isAnnotationPresent(Mapping.class)) {
                    String path = method.getAnnotation(Mapping.class).value();
                    ensureNoDuplicateMapping(method, path);
                    pathMap.put(path, new ControllerMethod(controller, method));
                }
            }
        }
    }

    /**
     * 동일한 경로(path)에 대해 두 개 이상의 메서드가 매핑되는 경우 예외를 발생시킨다.
     *
     * @param method 등록 시도 중인 메서드
     * @param path   해당 메서드가 매핑하려는 경로
     */
    private void ensureNoDuplicateMapping(Method method, String path) {
        if (pathMap.containsKey(path)) {
            ControllerMethod existingMethod = pathMap.get(path);
            throw new IllegalStateException(
                    "Duplicate path mapping detected:\n" +
                            " - Path        : " + path + "\n" +
                            " - Attempted   : " + method + "\n" +
                            " - Registered  : " + existingMethod.method
            );
        }
    }

    /**
     * 등록된 메서드를 호출하여 요청을 처리한다.
     *
     * <p>메서드의 파라미터 타입을 기준으로 `HttpRequest`, `HttpResponse`를 전달하며,</p>
     * <p>그 외 타입이 존재할 경우 IllegalArgumentException을 발생시킨다.</p>
     */
    private static class ControllerMethod {
        private final Object controller;
        private final Method method;

        public ControllerMethod(Object controller, Method method) {
            this.controller = controller;
            this.method = method;
        }

        // 실제 명령 처리 메서드를 실행
        public void invoke(HttpRequest request, HttpResponse response) {
            Class<?>[] parameterTypes = method.getParameterTypes();
            Object[] args = resolveMethodArguments(request, response, parameterTypes);

            try {
                method.invoke(controller, args);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }

        /**
         * 메서드 파라미터 타입을 분석해, `HttpRequest`, `HttpResponse` 객체를 주입한다.
         *
         * <p>- 현재는 두 타입만 지원하며, 그 외 타입이 존재할 경우 예외 발생</p>
         *
         * @param request       현재 요청 객체
         * @param response      응답 객체
         * @param parameterTypes 메서드의 파라미터 타입 배열
         * @return 메서드 실행에 사용할 인자 배열
         */
        private static Object[] resolveMethodArguments(HttpRequest request, HttpResponse response, Class<?>[] parameterTypes) {
            Object[] args = new Object[parameterTypes.length];

            for (int i = 0; i < parameterTypes.length; i++) {
                if (parameterTypes[i] == HttpRequest.class) {
                    args[i] = request;
                } else if (parameterTypes[i] == HttpResponse.class) {
                    args[i] = response;
                } else {
                    throw new IllegalArgumentException("Unsupported parameter type: " + parameterTypes[i].getName());                }
            }
            return args;
        }
    }

    /**
     * 요청 경로에 매핑된 메서드가 없는 경우 사용되는 기본 처리자.
     *
     * <p>이 메서드는 무조건 404 예외를 발생시킴으로써 명시적으로 "경로 없음" 상태를 표현한다.</p>
     */
    private static class DefaultMethod extends ControllerMethod {
        public DefaultMethod() {
            super(null, null);
        }

        @Override
        public void invoke(HttpRequest request, HttpResponse response) {
            throw new PageNotFoundException("No mapping found for request path: " + request.getPath());
        }
    }
}
