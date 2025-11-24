프로젝트 컨벤션 및 개발 가이드

이 문서는 샘플 베이스 Rest Api 프로젝트의 코드 스타일 및 주요 아키텍처 규칙을 정의합니다.

1. 기술 스택

Java: 21 (Virtual Threads enabled)

Spring Boot: 3.2.5

ORM: JPA + QueryDSL

Database: H2 (Local), PostgreSQL (Runtime)

Build Tool: Gradle

2. 코드 스타일 (Code Style)

Builder 패턴 지양

객체 생성 시 롬복의 @Builder 대신 생성자(Constructor) 또는 정적 팩토리 메서드를 사용합니다.

변경이 필요한 필드에 대해서만 명시적인 수정 메서드를 제공합니다.

JPA & Dirty Checking

Explicit Save 지양: 엔티티 수정 시 repository.save()를 호출하지 않고, Transactional 안에서 엔티티의 상태를 변경하여 **Dirty Checking(변경 감지)**이 동작하도록
합니다.

Open-In-View는 false로 설정되어 있으므로, 지연 로딩(Lazy Loading)은 트랜잭션 범위 내(Service Layer)에서 완료되어야 합니다.

3. 유효성 검사 (Validation)

@InitBinder 규칙 [중요]

컨트롤러에서 @InitBinder를 사용할 때, value 값은 반드시 해당 컨트롤러 메서드의 파라미터 변수명과 일치해야 Validator가 정상 작동합니다.

// 예시
@InitBinder("memberCreateRequest") // 변수명 일치 필수
public void initBinder(WebDataBinder binder) {
binder.addValidators(memberCreateValidator);
}

@PostMapping
public ResponseEntity<?> create(@Valid @RequestBody MemberCreateRequest memberCreateRequest) { ... }

BindingResult 처리 (AOP)

컨트롤러 메서드 파라미터에 BindingResult를 선언해두면, BindingAdvice (AOP)가 이를 가로채서 에러 검출 시 BindingException을 발생시킵니다.

별도의 if (bindingResult.hasErrors()) 코드를 컨트롤러 내부에 작성할 필요가 없습니다.

4. 기타 설정

가상 스레드 (Virtual Threads): 활성화됨 (spring.threads.virtual.enabled: true)

P6Spy: 로컬 환경에서 쿼리 파라미터 로깅 확인 가능 (설정에서 on/off 가능)

Batch Fetch Size: 100으로 설정되어 컬렉션 조회 시 N+1 문제 완화