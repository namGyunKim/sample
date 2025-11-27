💎 GEMINI - 프로젝트 베이스 가이드라인 (Updated)
이 문서는 Spring Boot 3.2.5 & Java 21 기반의 타임리프 프로젝트를 위한 개발 가이드라인, 요구사항 명세서, 그리고 SEO 전략을 포함합니다.

1. 🏗 프로젝트 환경 (Environment)
   Build Tool: Gradle

Language: Java 21

Framework: Spring Boot 3.2.5

Database: MySQL / PostgreSQL

Template Engine: Thymeleaf

2. 🛠 주요 기술 스택 (Tech Stack)
   🌐 Web & UI
   Core: spring-boot-starter-web, spring-boot-starter-validation

Layout: thymeleaf-layout-dialect (헤더, 푸터 등 레이아웃 모듈화)

UI Control: thymeleaf-extras-springsecurity6 (로그인 상태 및 권한별 UI 제어)

Interactivity (SPA UX):

HTMX: HTML 조각 교체 (Partial Rendering)

Alpine.js: 클라이언트 측 경량 상호작용 (모달, 드롭다운)

NProgress: 페이지 로딩 시각화

💾 Persistence
JPA: spring-boot-starter-data-jpa

Query: JPA Specification 사용 (QueryDSL 대신 Criteria API 래핑하여 복잡한 동적 쿼리 처리)

🔐 Security & Auth
Security: spring-boot-starter-security

OAuth2: Google Social Login으로 회원 가입 및 로그인 단일화

⚡ Cache & Session
Redis: spring-boot-starter-data-redis

Session: spring-session-data-redis (세션 클러스터링 지원)

📝 Logging & Monitoring
SQL Logging: p6spy-spring-boot-starter (SQL 파라미터 바인딩 및 줄바꿈 스타일 적용)

Monitoring: spring-boot-starter-actuator

🔌 External Integration
HTTP Client: spring-cloud-starter-openfeign

File Storage: software.amazon.awssdk:s3 (파일 업로드)

Mail: spring-boot-starter-mail (이메일 발송)

📄 Docs
Swagger: springdoc-openapi-starter-webmvc-ui

🧰 Utils
Rate Limiting: bucket4j-core

Image: twelvemonkeys (이미지 처리 및 변환)

3. 🏛 아키텍처 및 패키지 구조 (Architecture)
   🔹 Service Layer 분리 (CQRS 지향)
   Read/Write 분리: read 패키지와 write 패키지로 서비스를 명확히 구분합니다.

예시: MemberReadService.java (조회 전용), MemberWriteService.java (상태 변경 전용)

🔹 Request Handling
DTO 필수: Controller 요청 시 Entity 직접 사용 금지. 반드시 Request DTO를 생성하여 매핑합니다.

🔹 Response Handling
API (RestController): BindingResult를 파라미터에 포함 필수. AOP(BindingAdvice)가 에러 감지 시 RestApiResponse.fail 포맷으로 자동 반환.

View (Controller): BindingResult를 파라미터로 받아 View(HTML)로 전달. Thymeleaf th:errors로 사용자 피드백 제공.

4. 📝 코딩 컨벤션 (Coding Convention)
   Entity Style
   No Builder: @Builder 사용 지양. 생성자 또는 정적 팩토리 메서드 활용.

Dirty Checking: 명시적 save() 호출 대신, 트랜잭션 내 비즈니스 메서드(updateStatus() 등)를 통한 변경 감지 활용.

Validation
Validator 분리: 복잡한 검증 로직은 org.springframework.validation.Validator 구현체로 분리.

@InitBinder: 컨트롤러 내 @InitBinder를 통해 Validator 등록.

주의: @InitBinder("targetName")과 @ModelAttribute("targetName") 변수명 일치 필수.

Clean Code
불필요한 코드 제거 및 가독성 중시.

주석 및 답변은 반드시 한글로 작성.

Testing
프로토타이핑 속도 중시: 단위 테스트(JUnit) 작성은 생략하며, 통합 테스트나 수동 테스트로 대체합니다.

5. 🎨 UI/UX 가이드 (Thymeleaf + Modern Frontend)
   SPA-like Experience
   HTMX (Partial Rendering):

전체 새로고침(Full Reload) 지양.

hx-get, hx-target 등을 활용해 필요한 HTML 조각만 서버에서 받아 교체.

Alpine.js (Client Interaction):

서버 통신이 불필요한 UI(모달, 탭, 드롭다운)는 Alpine.js(x-data, x-show)로 가볍게 처리.

NProgress (Visual Feedback):

htmx:beforeRequest → 로딩 바 시작.

htmx:afterOnLoad → 로딩 바 종료.

Layout Reuse
Layout Dialect: 모든 화면은 layout/default.html을 상속(layout:decorate).

Content: 실제 내용은 <div layout:fragment="content"> 내부에 작성.

Component Modularization
Fragments: 반복되는 UI(댓글, 리스트)는 th:replace로 모듈화하여 재사용.

Security Integration
View Level Security: sec:authorize="isAuthenticated()" 등을 활용해 권한별 UI 제어.

6. ✅ 기타 요구사항
   Full Code 제공: 모든 코드는 생략 없이 전체 코드 형태로 제공합니다.

경로 명시: 파일 상단에 src/main/java/... 등 경로와 파일명 주석 명시 필수.

외부 도구: Google Search API 등 필요 시 외부 도구 적극 활용.

7. 🔍 SEO 및 검색 최적화 가이드 (SEO Strategy)
   본 프로젝트는 SSR(Server-Side Rendering) 방식을 사용하여 SPA보다 검색 엔진 최적화(SEO)에 유리합니다. 검색 노출을 극대화하기 위해 다음 가이드를 준수합니다.

🔹 동적 메타 태그 (Dynamic Meta Tags)
모든 페이지는 고유한 제목과 설명을 가져야 합니다. Layout Dialect와 Model을 활용하여 페이지별 정보를 동적으로 주입합니다.

Controller: Model에 title, description, ogImage 속성 추가.

Layout (default.html):

HTML

<head>
    <title th:text="${title} ?: '샘플 베이스 프로젝트'">기본 타이틀</title>
    <meta name="description" th:content="${description} ?: '기본 설명입니다.'">
</head>
🔹 Open Graph (OG) & Twitter Cards
소셜 미디어(카카오톡, 페이스북, 트위터) 공유 시 미리보기를 제공하기 위해 표준 OG 태그를 설정합니다.

og:type: 웹사이트 타입 (website, article 등)

og:title: 페이지 제목

og:description: 페이지 요약

og:image: 썸네일 이미지 URL (권장 사이즈: 1200x630)

og:url: 현재 페이지의 Canonical URL

🔹 Sitemap & Robots.txt
검색 엔진 크롤러가 사이트 구조를 쉽게 파악할 수 있도록 돕습니다.

Robots.txt: src/main/resources/static/robots.txt 위치에 정적 파일 생성. 모든 봇의 접근을 허용하거나 관리자 페이지(/admin/**)는 차단 설정.

Sitemap.xml: 동적으로 변하는 콘텐츠(게시글 등)를 위해 SitemapController를 생성하여 XML을 동적으로 반환하도록 구현 권장.

🔹 구조화된 데이터 (Structured Data - JSON-LD)
Google이 페이지 내용을 더 잘 이해하도록 <script type="application/ld+json">을 활용해 구조화된 데이터를 제공합니다.

적용 대상: 상품 상세, 게시글, FAQ, 이벤트 페이지 등.

구현 방법: Thymeleaf 변수를 JSON 포맷 안에 바인딩하여 렌더링.

🔹 URL 구조 (Clean URLs)
파라미터 방식(?id=123)보다는 경로 방식(/posts/123 또는 /posts/my-first-post)을 사용하여 의미 있는 URL을 설계합니다.