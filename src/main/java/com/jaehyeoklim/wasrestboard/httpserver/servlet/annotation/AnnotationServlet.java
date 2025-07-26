package main.java.com.jaehyeoklim.wasrestboard.httpserver.servlet.annotation;

import main.java.com.jaehyeoklim.wasrestboard.httpserver.HttpRequest;
import main.java.com.jaehyeoklim.wasrestboard.httpserver.HttpResponse;
import main.java.com.jaehyeoklim.wasrestboard.httpserver.enums.HttpMethod;
import main.java.com.jaehyeoklim.wasrestboard.httpserver.exception.PageNotFoundException;
import main.java.com.jaehyeoklim.wasrestboard.httpserver.servlet.base.HttpServlet;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Mapping 애노테이션을 기반으로 요청 경로 + HTTP 메서드에 따라
 * 컨트롤러 메서드를 실행하는 디스패처 서블릿.
 */
public class AnnotationServlet implements HttpServlet {

    private final Map<MappingKey, ControllerMethod> pathMap;

    @Override
    public void service(HttpRequest request, HttpResponse response) throws IOException {
        String path = request.getPath();
        HttpMethod method = request.getMethod();

        MappingKey key = new MappingKey(path, method);
        ControllerMethod controllerMethod = pathMap.getOrDefault(key, new DefaultMethod());

        controllerMethod.invoke(request, response);
    }

    public AnnotationServlet(List<Object> controllers) {
        this.pathMap = new HashMap<>();
        initializePathMap(controllers);
    }

    /**
     * 컨트롤러들의 @Mapping 정보를 읽어 pathMap에 등록한다.
     * 중복 매핑은 예외 발생.
     */
    private void initializePathMap(List<Object> controllers) {
        for (Object controller : controllers) {
            Method[] methods = controller.getClass().getDeclaredMethods();

            for (Method method : methods) {
                if (method.isAnnotationPresent(Mapping.class)) {
                    Mapping mapping = method.getAnnotation(Mapping.class); // ✅ 성능 개선
                    String path = mapping.path();
                    HttpMethod httpMethod = mapping.method();

                    MappingKey mappingKey = new MappingKey(path, httpMethod);
                    ensureNoDuplicateMapping(method, mappingKey);
                    pathMap.put(mappingKey, new ControllerMethod(controller, method));
                }
            }
        }
    }

    /**
     * 중복된 매핑이 있는 경우 예외 발생.
     */
    private void ensureNoDuplicateMapping(Method method, MappingKey mappingKey) {
        if (pathMap.containsKey(mappingKey)) {
            ControllerMethod existingMethod = pathMap.get(mappingKey);
            throw new IllegalStateException(
                    "Duplicate path mapping detected:\n" +
                            " - Path        : " + mappingKey.getPath() + "\n" +
                            " - HTTP Method : " + mappingKey.getMethod() + "\n" +
                            " - Attempted   : " + method + "\n" +
                            " - Registered  : " + existingMethod.method
            );
        }
    }

    /**
     * 컨트롤러 메서드를 실행하고, 파라미터 타입에 따라 HttpRequest/HttpResponse를 주입한다.
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
         * 파라미터 타입을 분석해 메서드 호출 인자를 구성한다.
         * 지원하지 않는 타입이 있으면 예외 발생.
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
     * 매핑된 메서드가 없을 경우 404 예외를 발생시키는 기본 처리자.
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
