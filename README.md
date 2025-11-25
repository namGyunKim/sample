Spring Boot Base Project

This project is a robust Spring Boot 3.2 starter template designed for rapid development of scalable web applications. It integrates essential features such as authentication (Social Login), file storage (AWS S3), messaging (Email, SMS), and database interactions using JPA and QueryDSL.

🚀 Key Features

Authentication & Security:

Spring Security integration.

Social Login support (Google OAuth2).

Custom authentication success handlers.

Member Management:

Role-based access control (USER, ADMIN, SUPER_ADMIN).

Member CRUD with Strategy Pattern.

Profile image management.

Infrastructure & Storage:

AWS S3: Image upload and management.

Redis: Session storage and caching.

Messaging:

Email: Async email sending via SMTP.

SMS: Verification code sending service (CoolSMS).

Database & ORM:

Spring Data JPA & QueryDSL for dynamic queries.

Auditing (CreatedAt, ModifiedAt, CreatedBy, ModifiedBy).

P6Spy: Pretty SQL logging for development.

Logging & Monitoring:

MDC Logging (Trace ID tracking).

AOP-based controller logging.

Activity logging (Login, Update, etc.).

View:

Server-side rendering with Thymeleaf & Tailwind CSS.

🛠 Tech Stack

Java: 21

Framework: Spring Boot 3.2

Database: MySQL / PostgreSQL

Cache/Session: Redis

ORM: JPA (Hibernate), QueryDSL

Template Engine: Thymeleaf

Build Tool: Gradle

⚙️ Configuration

Copy the application-dummy.yml file to src/main/resources/application.yml.

Fill in the required environment variables or direct values in the YAML file.

# Example
datasource:
url: jdbc:mysql://localhost:3306/your_db
username: root
password: your_password

aws:
access-key: YOUR_AWS_ACCESS_KEY
secret-key: YOUR_AWS_SECRET_KEY


스프링 부트 베이스 프로젝트

이 프로젝트는 확장 가능한 웹 애플리케이션의 빠른 개발을 위해 설계된 Spring Boot 3.2 기반의 스타터 템플릿입니다. 인증(소셜 로그인), 파일 저장소(AWS S3), 메시징(이메일, SMS), 그리고 JPA와 QueryDSL을 활용한 데이터베이스 상호작용 등 필수적인 기능들이 통합되어 있습니다.

🚀 주요 기능

인증 및 보안:

Spring Security 통합.

소셜 로그인 지원 (Google OAuth2).

커스텀 인증 성공 핸들러.

회원 관리:

역할 기반 접근 제어 (USER, ADMIN, SUPER_ADMIN).

전략 패턴(Strategy Pattern)을 적용한 회원 CRUD.

프로필 이미지 관리.

인프라 및 저장소:

AWS S3: 이미지 업로드 및 관리.

Redis: 세션 저장소 및 캐싱 활용.

메시징:

Email: SMTP를 이용한 비동기 이메일 발송.

SMS: 인증 번호 발송 서비스 (CoolSMS).

데이터베이스 및 ORM:

Spring Data JPA 및 동적 쿼리를 위한 QueryDSL.

Auditing 적용 (생성일, 수정일, 생성자, 수정자 자동 관리).

P6Spy: 개발 편의를 위한 가독성 높은 SQL 로깅.

로깅 및 모니터링:

MDC 로깅 (요청별 Trace ID 추적).

AOP 기반의 컨트롤러 요청/응답 로깅.

사용자 활동 로그 기록 (로그인, 정보 수정 등).

뷰 (View):

Thymeleaf 및 Tailwind CSS를 이용한 서버 사이드 렌더링.

🛠 기술 스택

Java: 21

Framework: Spring Boot 3.2

Database: MySQL / PostgreSQL

Cache/Session: Redis

ORM: JPA (Hibernate), QueryDSL

Template Engine: Thymeleaf

Build Tool: Gradle

⚙️ 설정 방법

application-dummy.yml 파일의 내용을 복사하여 src/main/resources/application.yml 파일을 생성하거나 덮어씁니다.

YAML 파일 내의 주요 설정 값(DB 정보, AWS 키, API 키 등)을 본인의 환경에 맞게 수정합니다.

# 예시
datasource:
url: jdbc:mysql://localhost:3306/your_db
username: root
password: your_password

aws:
access-key: 발급받은_AWS_ACCESS_KEY
secret-key: 발급받은_AWS_SECRET_KEY
