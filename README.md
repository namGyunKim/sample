Spring Boot 3.2 + Thymeleaf Base Project

이 프로젝트는 Spring Boot 3.2.5와 Java 21을 기반으로 하는 Monolithic Web Application 베이스 프로젝트입니다.
SSR(Server-Side Rendering) 기술인 Thymeleaf를 사용하며, Spring Security(Session 기반) 인증 방식을 채택하고 있습니다.

확장성과 유지보수성을 고려하여 전략 패턴(Strategy Pattern), AOP 기반 유효성 검사, Dirty Checking 등을 적극적으로 활용합니다.

🛠 Tech Stack

Environment

Java: 21 (LTS)

Spring Boot: 3.2.5

Build Tool: Gradle

Core & Web

Spring WebMVC: Servlet 기반 웹 프레임워크

Thymeleaf: 템플릿 엔진 (+ Layout Dialect, Security Extras)

Spring Security: 인증 및 권한 관리 (Session Based)

Validation: Bean Validation (Jakarta Validation)

Data & Storage

JPA (Hibernate): ORM 표준

QueryDSL 5.0: Type-Safe 동적 쿼리 처리

MySQL / PostgreSQL: 메인 데이터베이스 (Local: MySQL, Prod: PostgreSQL 권장)

Redis: 캐싱, Rate Limiting (Bucket4j), 임시 데이터 저장

AWS S3: 이미지 파일 스토리지

Infra & Utils

Swagger (SpringDoc): API 문서화 (Local 프로필에서만 활성화 권장)

P6Spy: 쿼리 파라미터 로깅

OpenFeign: 외부 API 통신 (Google Login 등)

CoolSMS: SMS 발송

JavaMailSender: 이메일 발송

🏗 Project Architecture & Patterns

1. 회원 관리 전략 (Strategy Pattern)

회원(Member)은 USER, ADMIN, SUPER_ADMIN 등 다양한 역할(Role)을 가집니다. 이를 효율적으로 관리하기 위해 전략 패턴을 사용합니다.

MemberStrategyFactory: 런타임에 AccountRole에 맞는 Service 구현체를 주입해줍니다.

Read/Write 분리: 조회(ReadMemberService)와 변경(WriteMemberService) 로직을 인터페이스단에서 분리하여 CQRS 패턴의 기초를 마련했습니다.

2. AOP 기반 유효성 검사 (Validation)

컨트롤러의 코드를 깔끔하게 유지하기 위해 BindingResult 처리를 AOP로 이관했습니다.

동작 원리:

컨트롤러 메서드에서 @Valid 객체 뒤에 BindingResult를 파라미터로 선언합니다.

BindingAdvice (AOP) 가 메서드 실행 전 BindingResult의 에러 유무를 감지합니다.

에러가 존재하면 BindingException을 throw 합니다.

ExceptionAdvice에서 이를 포착하여 공통 에러 처리를 수행합니다.

⚠️ 주의사항: 컨트롤러 메서드 시그니처에 BindingResult가 없으면 AOP가 동작하지 않고, 스프링 기본 예외가 발생할 수 있습니다.

3. InitBinder & Validator 네이밍 규칙

커스텀 Validator를 @InitBinder로 등록하여 사용할 때, 변수명 일치가 필수적입니다.

// Controller 예시
@InitBinder("memberCreateRequest") // 1. 지정한 이름
public void initBinder(WebDataBinder dataBinder) {
dataBinder.addValidators(memberCreateValidator);
}

@PostMapping(...)
public String create(
// 2. @ModelAttribute의 이름(또는 파라미터 변수명)이 위와 일치해야 함
@Valid @ModelAttribute("memberCreateRequest") MemberCreateRequest request,
BindingResult bindingResult
) { ... }


4. 더티 체킹 (Dirty Checking) 지향

Builder 패턴 미사용: 객체의 일관성을 위해 무분별한 빌더 사용을 지양합니다.

생성자 주입: 필수 필드는 생성 시점에 강제합니다.

Update 메서드: 엔티티 내부에 비즈니스 로직을 담은 수정 메서드(updatePassword, deActive 등)를 정의하고, @Transactional 안에서 조회 후 상태를 변경하여 더티 체킹으로 DB에 반영합니다.

🚀 Getting Started

1. 사전 요구 사항

Java 21 이상 설치

Redis 실행 (기본 포트 6379)

MySQL (Local) 실행

2. 환경 변수 설정 (application.yml)

로컬 실행 시 application-local.yml이 활성화됩니다. 아래 설정들이 필요합니다.

# DB 설정
spring.datasource.username: root
spring.datasource.password: 0000

# AWS S3 (이미지 업로드)
aws.access-key: [YOUR_ACCESS_KEY]
aws.secret-key: [YOUR_SECRET_KEY]
s3.bucket-local: [BUCKET_NAME]

# Google Social Login
social.google.clientId: [CLIENT_ID]
social.google.secretKey: [SECRET_KEY]

# SMS / Mail 등 필요한 키 설정


3. 빌드 및 실행

# Build
./gradlew clean build -x test

# Run
java -jar build/libs/app.jar



📝 Coding Conventions

Entity:

@Setter 사용 지양.

기본 생성자는 protected.

변경 로직은 엔티티 내부 메서드로 구현.

DTO:

Java record 사용 권장 (불변성 보장).

Logging:

ControllerLoggingAspect를 통해 요청/응답을 로깅합니다.

TraceID를 발급하여 요청 흐름을 추적합니다.

가독성을 위해 줄바꿈(\n) 스타일을 사용합니다.

Exception:

비즈니스 로직 예외는 GlobalException을 사용하며 ErrorCode를 통해 관리합니다.

ExceptionAdvice가 Accept 헤더에 따라 JSON 또는 HTML 에러 페이지를 자동으로 분기하여 반환합니다.

🔒 Security & Auth

방식: Session Based Authentication (JSESSIONID)

접근 제어:

SecurityConfig의 filterChain 및 어노테이션(@PreAuthorize) 기반 제어.

MemberGuard 빈을 활용하여 리소스 소유자 확인 등 복잡한 권한 로직 수행.

Current User:

컨트롤러에서 @CurrentAccount CurrentAccountDTO account 파라미터를 통해 현재 로그인된 사용자 정보를 손쉽게 획득 가능.

© 2025 Sample Base Project.