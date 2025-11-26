GEMINI - 프로젝트 베이스 가이드라인

이 문서는 Spring Boot 3.2.5 & Java 21 기반의 타임리프 프로젝트를 위한 개발 가이드라인 및 요구사항 명세서입니다.

1. 프로젝트 환경 (Environment)
   Build Tool: Gradle
   Language: Java 21
   Framework: Spring Boot 3.2.5
   Database: MySQL / PostgreSQL
   Template Engine: Thymeleaf

2. 주요 기술 스택 (Tech Stack)
   Web & UI:
   spring-boot-starter-web, spring-boot-starter-validation
   thymeleaf-layout-dialect (레이아웃 모듈화: 헤더, 푸터 등)
   thymeleaf-extras-springsecurity6 (로그인 상태 및 권한별 UI 제어)
   HTMX & NProgress (SPA 경험 제공)

Persistence:
spring-boot-starter-data-jpa
Specification: 복잡한 동적 쿼리는 QueryDSL 대신 JPA Specification(Criteria API 래핑) 사용

Security & Auth:
spring-boot-starter-security
Google Social Login: 회원 가입 및 로그인은 구글 소셜 로그인으로 단일화

Cache & Session:
spring-boot-starter-data-redis
spring-session-data-redis (세션 클러스터링)

Logging & Monitoring:
p6spy-spring-boot-starter (SQL 로깅, 줄바꿈 스타일 적용)
spring-boot-starter-actuator

External Integration:
spring-cloud-starter-openfeign (HTTP Client)
software.amazon.awssdk:s3 (파일 업로드)
spring-boot-starter-mail (이메일 발송)

Docs: springdoc-openapi-starter-webmvc-ui (Swagger)

Utils: bucket4j-core (Rate Limiting), twelvemonkeys (이미지 처리)

3. 아키텍처 및 패키지 구조 (Architecture)
   Service Layer 분리:
   CQRS 지향: read 패키지와 write 패키지로 서비스를 명확히 분리
   예: MemberReadService.java, MemberWriteService.java

Request Handling:
Controller에서 요청을 받을 때는 반드시 별도의 Request DTO 생성하여 사용

Response Handling:
BindingResult는 컨트롤러 코드 내에서 직접 처리하지 않고, AOP를 통해 일괄 감지 및 예외 처리

4. 코딩 컨벤션 (Coding Convention)
   Entity Style:
   No Builder: 빌더 패턴(@Builder)을 사용하지 않음
   Dirty Checking: 데이터 수정 시 set 메서드 등을 통한 변경 감지(Dirty Checking) 활용

Validation:
@InitBinder 사용 시, Validator의 이름과 컨트롤러의 @ModelAttribute 변수명이 일치해야 함을 준수

Clean Code:
불필요한 코드를 줄이고 가독성을 높이는 클린 모드 지향
주석 및 답변은 한글로 작성

Testing:
테스트 코드(JUnit 등)는 작성하지 않음

CQRS 패턴 지향

5. UI/UX 가이드 (Thymeleaf)
   SPA-like Experience (HTMX & NProgress):
   페이지 전체 새로고침을 지양하고 HTMX를 적극 활용하여 부분 렌더링을 구현합니다.
   AJAX 요청 시 NProgress를 연동하여 시각적인 로딩 피드백을 주어 SPA(Single Page Application)와 유사한 사용자 경험을 제공합니다.

Layout Reuse (Essential):
모든 화면은 공통 레이아웃(layout/default.html)을 상속받아 구현해야 합니다.
개별 HTML 파일의 <html> 태그에 layout:decorate="~{layout/default}"를 반드시 명시합니다.
실제 콘텐츠는 <div layout:fragment="content"> 내부에 작성하여, 헤더/푸터/공통 스타일이 자동으로 적용되도록 합니다.

Layout Dialect: 공통 요소(Header, Footer, Sidebar)는 Layout으로 모듈화하여 중복 제거

Fragments: 댓글 목록, 리스트 아이템 등 반복되는 UI는 th:replace를 사용하여 Fragment로 분리 및 재사용

Security Integration: sec:authorize="isAuthenticated()" 등을 활용하여 로그인 여부 및 권한에 따른 버튼 노출/숨김 처리

6. 기타 요구사항
   모든 코드는 전체 코드(Full Code) 형태로 제공
   코드 제공 시 파일 경로와 파일명을 명시하여 수정 위치 혼동 방지
   Google Search API 등의 외부 도구는 상황에 맞게 활용