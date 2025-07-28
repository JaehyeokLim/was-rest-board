# WAS-REST-Board

[[1편] 블로그 글](https://jaehyeoklim.tistory.com/13)
[[2편] 블로그 글](https://jaehyeoklim.tistory.com/14)
[[3편] 블로그 글](https://jaehyeoklim.tistory.com/15)

## 개요

Java로 구현한 커스텀 웹 애플리케이션 서버(WAS) 기반의 RESTful 게시판 애플리케이션이다.

HTTP 요청을 처리하는 커스텀 WAS 위에서 사용자 관리(회원가입, 로그인, 계정 관리)와 게시물 CRUD 기능을 제공한다.

애노테이션 기반 라우팅, 세션 관리, 파일 기반 데이터 저장을 통해 간단한 웹 서버 구조를 구현한다.

## 목표

- Java 네트워크 통신과 HTTP 프로토콜 처리 학습
- 멀티스레드 기반 클라이언트 요청 처리
- 애노테이션과 리플렉션 기반 RESTful API 설계
- 세션 관리와 사용자 인증/인가 구현
- 파일 기반 데이터 저장소 설계
- 안정적인 서버 운영과 에러 처리

## 마일스톤

- [x]  커스텀 WAS 구현 및 HTTP 요청/응답 처리
- [x]  사용자 등록, 로그인, 로그아웃, 계정 관리
- [x]  게시물 생성, 조회, 수정, 삭제
- [x]  `@Mapping` 애노테이션 기반 라우팅
- [x]  세션 관리 및 BCrypt 기반 비밀번호 암호화
- [x]  로그 출력과 자원 관리 유틸리티

## 요구사항

### 기본 기능

- 클라이언트 HTTP 요청 처리
- 사용자: 회원가입, 로그인, 로그아웃, 계정 수정/삭제
- 게시물: 생성, 조회, 수정, 삭제
- 세션: 쿠키 기반 세션 관리
- 에러: 404, 500 응답 처리

### 심화 기능

- `@Mapping` 애노테이션으로 경로와 HTTP 메서드 매핑
- `ConcurrentHashMap` 기반 세션 관리
- `JBCrypt` 기반 비밀번호 암호화
- 파일 기반 데이터 저장(`users.dat`, `posts.dat`)
- 멀티스레드 요청 처리(`ExecutorService`)

## 사용 기술

- **Java 24**
- **Socket / ServerSocket API**
- **ExecutorService / Thread 기반 멀티스레딩**
- **애노테이션 + 리플렉션**
- **JBCrypt 비밀번호 암호화**
- **파일 I/O 기반 데이터 저장**

## 주요 구성 요소

### HTTP 서버 모듈

| 클래스 | 설명 |
| --- | --- |
| `HttpServer` | 포트 54321에서 클라이언트 연결 수용, 스레드 풀(10개)로 처리 |
| `HttpRequest` | HTTP 요청 파싱(메서드, 경로, 쿼리, 헤더, 쿠키) |
| `HttpResponse` | HTTP 응답 작성(상태 코드, 본문, 쿠키) |
| `HttpRequestHandler` | 클라이언트 소켓별 요청 처리 및 서블릿 실행 |
| `HttpMethod` | GET, POST, PUT, DELETE 정의 |
| `HttpStatus` | HTTP 상태 코드(200, 404 등) 정의 |
| `PageNotFoundException` | 404 오류 처리 |

### 서블릿 모듈

| 클래스 | 설명 |
| --- | --- |
| `ServletManager` | 경로별 서블릿 매핑 및 실행 |
| `HttpServlet` | 서블릿 인터페이스 |
| `AnnotationServlet` | `@Mapping` 기반 컨트롤러 메서드 디스패처 |
| `Mapping` | 경로와 HTTP 메서드 매핑 애노테이션 |
| `MappingKey` | 경로와 메서드 조합 키 |
| `DefaultServlet` | 기본 서블릿(빈 구현) |
| `DiscardServlet` | 요청 무시 |
| `InternalErrorServlet` | 500 오류 페이지 |
| `NotFoundServlet` | 404 오류 페이지 |

### 사용자 모듈

| 클래스 | 설명 |
| --- | --- |
| `User` | 사용자 데이터(UUID, loginId, password, name) |
| `UserRepository` | 파일(`users.dat`) 기반 사용자 CRUD |
| `UserController` | 로그인, 로그아웃, 회원가입, 계정 관리 API |

### 게시물 모듈

| 클래스 | 설명 |
| --- | --- |
| `Post` | 게시물 데이터(UUID, owner, createdAt, title, content) |
| `PostRepository` | 파일(`posts.dat`) 기반 게시물 CRUD |
| `PostController` | 게시물 생성, 조회, 수정, 삭제 API |

### 공통 모듈

| 클래스 | 설명 |
| --- | --- |
| `Logger` | 시간과 스레드명 포함 로그 출력 |
| `UUIDGenerator` | UUID 생성 |
| `PasswordEncoder` | BCrypt 기반 비밀번호 해시/검증 |
| `AuthenticationHelper` | 세션 기반 인증 |
| `SessionManager` | `ConcurrentHashMap` 기반 세션 관리 |

### 메인 모듈

| 클래스 | 설명 |
| --- | --- |
| `WasMain` | 서버 실행 및 컨트롤러 초기화 |
| `WasMainController` | 루트 경로(`/`)의 로그인/메인 페이지 |

## 프로젝트 구조

```
was-rest-board/
├── main/
│   └── com.jaehyeoklim.wasrestboard.main
│       ├── WasMain.java
│       └── WasMainController.java
├── board/
│   └── com.jaehyeoklim.wasrestboard.board
│       ├── controller/
│       │   └── PostController.java
│       ├── domain/
│       │   └── Post.java
│       └── repository/
│           └── PostRepository.java
├── user/
│   └── com.jaehyeoklim.wasrestboard.user
│       ├── controller/
│       │   └── UserController.java
│       ├── domain/
│       │   └── User.java
│       └── repository/
│           └── UserRepository.java
├── httpserver/
│   └── com.jaehyeoklim.wasrestboard.httpserver
│       ├── HttpServer.java
│       ├── HttpRequest.java
│       ├── HttpResponse.java
│       ├── HttpRequestHandler.java
│       ├── enums/
│       │   ├── HttpMethod.java
│       │   └── HttpStatus.java
│       ├── exception/
│       │   └── PageNotFoundException.java
│       ├── servlet/
│       │   ├── annotation/
│       │   │   ├── AnnotationServlet.java
│       │   │   ├── Mapping.java
│       │   │   └── MappingKey.java
│       │   ├── base/
│       │   │   └── HttpServlet.java
│       │   └── system/
│       │       ├── DefaultServlet.java
│       │       ├── DiscardServlet.java
│       │       ├── InternalErrorServlet.java
│       │       └── NotFoundServlet.java
│       └── ServletManager.java
├── session/
│   └── com.jaehyeoklim.wasrestboard.session
│       └── SessionManager.java
├── util/
│   └── com.jaehyeoklim.wasrestboard.util
│       ├── AuthenticationHelper.java
│       ├── Logger.java
│       ├── PasswordEncoder.java
│       └── UUIDGenerator.java
├── pom.xml
├── .gitignore
├── README.md

```

## 동작 화면

> 브라우저 기반 웹 UI
> 
> 
> (동작 화면은 실제 실행 후 `http://localhost:54321`에서 확인 가능)
> 
> <img width="1034" height="1081" alt="스크린샷 2025-07-28 오후 4:24:42" src="https://github.com/user-attachments/assets/7c433342-a114-4e39-8b61-0a890113c619" />
> 
> <img width="1034" height="1081" alt="스크린샷 2025-07-28 오후 4:24:51" src="https://github.com/user-attachments/assets/3e165361-e615-4432-bbe2-16687e5b6aa2" />
> 
> <img width="1034" height="1081" alt="스크린샷 2025-07-28 오후 4:25:24" src="https://github.com/user-attachments/assets/f733f79f-0f05-4071-b0cc-fc8a826828b2" />
> 
> <img width="1034" height="1081" alt="스크린샷 2025-07-28 오후 4:25:35" src="https://github.com/user-attachments/assets/c8beb66b-abd5-4837-93f2-8ae98aa83861" />
> 
> <img width="1034" height="1081" alt="스크린샷 2025-07-28 오후 4:25:45" src="https://github.com/user-attachments/assets/f599fcba-ce4a-4f1d-a53b-9f93a362337d" />
> 
> <img width="1034" height="1081" alt="스크린샷 2025-07-28 오후 4:25:56" src="https://github.com/user-attachments/assets/19578e18-2713-4c6a-9372-fabfc72f7798" />
> 
> <img width="1034" height="1081" alt="스크린샷 2025-07-28 오후 4:25:59" src="https://github.com/user-attachments/assets/9c31b165-465d-46e0-9c0b-8d4268d38a29" />
> 
> <img width="1034" height="1081" alt="스크린샷 2025-07-28 오후 4:26:03" src="https://github.com/user-attachments/assets/9d5887a2-0141-43a0-b4ea-32f5470d4b4b" />
> 
> <img width="1034" height="1081" alt="스크린샷 2025-07-28 오후 4:26:10" src="https://github.com/user-attachments/assets/0dd0ccf5-08f6-4a6a-b176-6a663393fa83" />
> 
> <img width="1034" height="1081" alt="스크린샷 2025-07-28 오후 4:26:20" src="https://github.com/user-attachments/assets/58159e22-02b4-4dad-9335-c1a4874b7dda" />
> 
> <img width="1034" height="1081" alt="스크린샷 2025-07-28 오후 4:26:23" src="https://github.com/user-attachments/assets/4789beab-bf83-44b0-9740-23b4c06148fb" />
> 
> <img width="1034" height="1081" alt="스크린샷 2025-07-28 오후 4:26:30" src="https://github.com/user-attachments/assets/ada412f5-ea57-441d-ab54-77f72dca38c7" />
> 
> <img width="1034" height="1081" alt="스크린샷 2025-07-28 오후 4:26:35" src="https://github.com/user-attachments/assets/d6f0fc1e-7853-40c3-b95e-f6b50d459a9e" />
