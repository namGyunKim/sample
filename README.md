🚀 Sample Base Project (Thymeleaf)

Spring Boot 3.2.5 & Java 21 기반의, 즉시 사용 가능한 모던 웹 애플리케이션 보일러플레이트입니다.
CQRS 아키텍처, HTMX를 활용한 SPA 경험, 구글 소셜 로그인, 그리고 강력한 보안 설정이 기본 내장되어 있습니다.

🛠 시작하기 (Getting Started)

사전 요구사항 (Prerequisites)

Java 21 (JDK 21+)

Redis (세션 및 캐시용, 기본 포트: 6379)

PostgreSQL (메인 데이터베이스, 기본 포트: 5432)

환경 설정 (Configuration)

프로젝트 실행 전 src/main/resources/application.yml (또는 -local.yml)에서 다음 설정을 본인의 환경에 맞게 수정해야 합니다.

데이터베이스 설정:

spring:
datasource:
url: jdbc:postgresql://localhost:5432/base_project
username: postgres
password: your_password

필수 API 키 설정:

Google Login: social.google.client-id, secret-key

AWS S3: aws.access-key, secret-key, s3.bucket

Mail: spring.mail.username, password (구글 앱 비밀번호)

실행 방법 (Run)

Windows

./gradlew.bat bootRun

Mac/Linux

./gradlew bootRun

초기 데이터 및 테스트 계정 (Init Data)

서버 최초 실행 시 InitService가 동작하여 아래의 기본 계정들을 자동으로 생성합니다.
(비밀번호 공통: 1234)

권한

아이디

설명

SUPER_ADMIN

superAdmin

최고 관리자 (등급 변경 등 모든 권한)

ADMIN

admin1 ~ admin10

일반 관리자 (회원 관리 가능)

USER

user1 ~ user200

일반 사용자 (더미 데이터)

📚 개발 가이드라인 (Development Guidelines)

프로젝트 환경 (Environment)

Build Tool: Gradle

Language: Java 21

Framework: Spring Boot 3.2.5

Database: MySQL / PostgreSQL

Template Engine: Thymeleaf

주요 기술 스택 (Tech Stack)

Web & UI:

HTMX & NProgress: SPA와 유사한 부드러운 UX 제공

Thymeleaf Layout Dialect: 레이아웃 모듈화

Tailwind CSS: 유틸리티 퍼스트 스타일링

Persistence:

JPA Specification (동적 쿼리)

Auth:

Spring Security + Google OAuth2 (소셜 로그인 단일화)

Logging: P6Spy (쿼리 로그)

아키텍처 및 패키지 구조 (Architecture)

CQRS 지향:

read 패키지: 조회 전용 서비스 (@Transactional(readOnly=true))

write 패키지: 상태 변경 전용 서비스 (Dirty Checking 활용)

Request/Response:

Controller는 반드시 Request DTO를 통해 데이터를 받습니다.

Validation & BindingResult:

Controller (View): BindingResult를 메서드 파라미터로 받아, 뷰(Thymeleaf)에 전달하여 사용자에게 에러를 표시합니다.

RestController (API): BindingResult를 메서드 파라미터로 선언해야 하며, BindingAdvice (AOP)가 이를 자동으로 감지하여 표준 JSON 에러 응답을 반환합니다.

코딩 컨벤션 (Coding Convention)

Entity: @Builder 지양, 생성자/정적 팩토리 메서드 사용. Setter 대신 비즈니스 메서드로 상태 변경.

Validation: @InitBinder와 Validator 구현체를 연결하여 검증 로직 분리.

Testing: 프로토타이핑 속도를 위해 단위 테스트보다는 통합 테스트 또는 수동 테스트 위주 진행.

UI/UX 가이드

HTMX 활용: 페이지 전체 로드 대신 hx-get, hx-target 등을 사용하여 부분 렌더링.

Fragment: 반복되는 UI 요소는 th:replace로 분리.

Layout: 모든 페이지는 layout/default.html을 상속받아 구현.