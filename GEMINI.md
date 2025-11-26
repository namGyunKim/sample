# GEMINI - 프로젝트 베이스 가이드라인

이 문서는 Spring Boot 3.2.5 & Java 21 기반의 타임리프 프로젝트를 위한 개발 가이드라인 및 요구사항 명세서입니다.

## 1. 프로젝트 환경 (Environment)
* **Build Tool:** Gradle
* **Language:** Java 21
* **Framework:** Spring Boot 3.2.5
* **Database:** MySQL / PostgreSQL
* **Template Engine:** Thymeleaf

## 2. 주요 기술 스택 (Tech Stack)

### Web & UI
* `spring-boot-starter-web`, `spring-boot-starter-validation`
* `thymeleaf-layout-dialect` (레이아웃 모듈화: 헤더, 푸터 등)
* `thymeleaf-extras-springsecurity6` (로그인 상태 및 권한별 UI 제어)
* **HTMX & Alpine.js & NProgress** (SPA 경험 제공 및 클라이언트 인터랙션)

### Persistence
* `spring-boot-starter-data-jpa`
* **Specification:** 복잡한 동적 쿼리는 QueryDSL 대신 JPA Specification(Criteria API 래핑) 사용

### Security & Auth
* `spring-boot-starter-security`
* **Google Social Login:** 회원 가입 및 로그인은 구글 소셜 로그인으로 단일화

### Cache & Session
* `spring-boot-starter-data-redis`
* `spring-session-data-redis` (세션 클러스터링)

### Logging & Monitoring
* `p6spy-spring-boot-starter` (SQL 로깅, 줄바꿈 스타일 적용)
* `spring-boot-starter-actuator`

### External Integration
* `spring-cloud-starter-openfeign` (HTTP Client)
* `software.amazon.awssdk:s3` (파일 업로드)
* `spring-boot-starter-mail` (이메일 발송)

### Docs
* `springdoc-openapi-starter-webmvc-ui` (Swagger)

### Utils
* `bucket4j-core` (Rate Limiting)
* `twelvemonkeys` (이미지 처리)

## 3. 아키텍처 및 패키지 구조 (Architecture)

### Service Layer 분리 (CQRS 지향)
* **Read/Write 분리:** `read` 패키지와 `write` 패키지로 서비스를 명확히 분리하여 관리합니다.
    * 예: `MemberReadService.java` (조회 전용), `MemberWriteService.java` (상태 변경 전용)

### Request Handling
* **DTO 필수:** Controller에서 요청을 받을 때는 반드시 별도의 Request DTO를 생성하여 매핑합니다. Entity를 직접 받지 않습니다.

### Response Handling
* **Global Exception Handling:** `BindingResult`는 컨트롤러 코드 내에서 직접 `if (bindingResult.hasErrors())`로 처리하지 않고, AOP(GlobalExceptionHandler)를 통해 일괄 감지 및 예외 처리 응답을 반환합니다.

## 4. 코딩 컨벤션 (Coding Convention)

### Entity Style
* **No Builder:** 빌더 패턴(`@Builder`)을 사용하지 않습니다. 생성자 또는 정적 팩토리 메서드를 활용합니다.
* **Dirty Checking:** 데이터 수정 시 명시적인 `save()` 호출보다는, 트랜잭션 내에서 `set` 메서드(또는 의미 있는 비즈니스 메서드)를 통한 변경 감지(Dirty Checking)를 활용합니다.

### Validation
* Validator 분리: 검증 로직은 가능한 경우 어노테이션(@NotNull 등)에 의존하기보다, org.springframework.validation.Validator 인터페이스를 구현한 별도의 Validator 클래스로 분리하여 작성합니다.
* @InitBinder 활용: 작성한 Validator는 컨트롤러 내 @InitBinder 메서드를 통해 WebDataBinder에 등록하여 적용합니다.
* Naming Convention: @InitBinder("targetName") 사용 시, 등록하는 Validator의 대상 이름과 컨트롤러 메서드 파라미터의 @ModelAttribute("targetName") 변수명이 반드시 일치해야 합니다.

### Clean Code
* 불필요한 코드를 줄이고 가독성을 높이는 클린 코드를 지향합니다.
* **주석 및 답변은 반드시 한글로 작성합니다.**

### Testing
* 테스트 코드(JUnit 등)는 작성하지 않습니다. (프로토타이핑 속도 중시)

## 5. UI/UX 가이드 (Thymeleaf + Modern Frontend)

### SPA-like Experience (HTMX & NProgress & Alpine.js)
서버 사이드 렌더링(SSR)을 유지하면서도 사용자에게는 네이티브 앱이나 SPA와 같은 부드러운 화면 전환 경험을 제공합니다.

1.  **HTMX (Partial Rendering):**
    * 페이지 전체 새로고침(`full page reload`)을 지양합니다.
    * `hx-get`, `hx-post`, `hx-target`, `hx-swap` 속성을 적극 활용하여 변경이 필요한 HTML 조각(Fragment)만 서버에서 받아와 교체합니다.

2.  **Alpine.js (Client Interactivity):**
    * 서버 요청 없이 처리 가능한 UI 상호작용(모달 Open/Close, 탭 전환, 드롭다운 메뉴 등)은 Alpine.js(`x-data`, `x-show` 등)를 사용하여 가볍게 처리합니다.
    * HTMX와 함께 사용하여 "서버 통신은 HTMX", "화면 제어는 Alpine.js"로 역할을 분담합니다.

3.  **NProgress (Visual Feedback):**
    * HTMX 요청 발생 시(`htmx:beforeRequest`) NProgress 바를 시작하고, 요청 완료 시(`htmx:afterOnLoad`) 종료하여 시각적인 로딩 피드백을 제공합니다.
    * 이는 사용자가 페이지가 멈춘 것이 아니라 데이터를 불러오고 있음을 인지하게 하여 UX를 크게 향상시킵니다.

### Layout Reuse (Essential)
* 모든 화면은 공통 레이아웃(`layout/default.html`)을 상속받아 구현해야 합니다.
* 개별 HTML 파일의 `<html>` 태그에 `layout:decorate="~{layout/default}"`를 반드시 명시합니다.
* 실제 콘텐츠는 `<div layout:fragment="content">` 내부에 작성하여, 헤더/푸터/공통 스타일이 자동으로 적용되도록 합니다.

### Component Modularization
* **Layout Dialect:** 공통 요소(Header, Footer, Sidebar)는 Layout으로 모듈화하여 중복을 제거합니다.
* **Fragments:** 댓글 목록, 리스트 아이템 등 반복되는 UI는 `th:replace`를 사용하여 별도 Fragment 파일로 분리 및 재사용합니다.

### Security Integration
* `sec:authorize="isAuthenticated()"` 등을 활용하여 로그인 여부 및 권한에 따른 버튼 노출/숨김 처리를 뷰 레벨에서 제어합니다.

## 6. 기타 요구사항
* **Full Code 제공:** 모든 코드는 생략 없이 전체 코드(Full Code) 형태로 제공합니다.
* **경로 명시:** 코드 제공 시 `src/main/java/...` 등 파일 경로와 파일명을 상단에 주석으로 명시하여 수정 위치 혼동을 방지합니다.
* **외부 도구 활용:** Google Search API 등 필요한 외부 도구는 상황에 맞게 적극 활용합니다.