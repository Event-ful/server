# Eventful - 이벤트 관리 서비스

여러 사람이 함께하는 이벤트(여행, 소모임 등)를 쉽고 효율적으로 계획하고 실행하도록 돕는 서비스

## 프로젝트 구조

```
src/
├── main/java/side/eventful/
│   ├── domain/              # 도메인 모델 (Member, EventGroup, Event 등)
│   ├── application/         # 서비스 레이어 (비즈니스 로직)
│   ├── infrastructure/      # 인프라 레이어 (Repository, 외부 연동)
│   └── interfaces/          # 프레젠테이션 레이어 (Controller, DTO)
├── test/java/side/eventful/ # 테스트 코드
└── docs/                    # 프로젝트 문서
    ├── project-spec.md      # 기능 명세서
    └── api-response-spec.md # API 응답 규격
```

## 기술 스택

- **Framework**: Spring Boot 3.x
- **Language**: Java 17+
- **Database**: PostgreSQL (or H2 for test)
- **Build**: Gradle
- **Authentication**: Session-based (Spring Security)
- **Testing**: JUnit5, AssertJ, Mockito

## 개발 원칙

### 1. 도메인 주도 설계 (DDD)
- 도메인 모델이 비즈니스 로직의 중심
- 집합체(Aggregate) 경계를 명확히 설정
- 도메인 이벤트를 활용한 느슨한 결합

### 2. 테스트 주도 개발 (TDD)
- 새로운 기능은 테스트 코드부터 작성
- 도메인 로직은 반드시 단위 테스트 작성
- 통합 테스트로 레이어 간 협력 검증

### 3. 객체지향 원칙
- SOLID 원칙 준수
- 불변성(Immutability) 우선
- 의존성 역전을 통한 유연한 설계

### 4. 레이어드 아키텍처
```
Controller (interfaces)
    ↓
Service (application)
    ↓
Domain (domain)
    ↓
Repository (infrastructure)
```

## 개발 가이드

### 코딩 컨벤션
- **메서드 명명**: 도메인 용어를 그대로 사용 (예: `joinEvent`, `leaveGroup`)
- **검증 로직**: 
  - 단일 집합체 규칙 → 도메인 객체
  - 여러 집합체 협력 → 서비스 레이어
- **예외 처리**: 명확한 예외 메시지와 함께 `IllegalArgumentException`, `IllegalStateException` 사용

### 테스트 작성 규칙
- **Given-When-Then** 패턴 사용
- `@DisplayName`으로 테스트 의도 명확히 표현
- 테스트용 Fixture 클래스 활용 (`src/test/.../fixture/`)

### 새로운 기능 추가 시
1. `docs/project-spec.md` 확인 및 업데이트
2. 도메인 모델 설계 및 테스트 작성
3. 서비스 레이어 구현
4. API 엔드포인트 추가
5. 통합 테스트 작성

## 주요 문서

- [기능 명세서](docs/project-spec.md) - 전체 기능 요구사항
- [API 응답 규격](docs/api-response-spec.md) - API 응답 형식
- [개발 가이드](.github/copilot-instructions.md) - AI 페어 프로그래밍 가이드

## 빌드 및 실행

```bash
# 빌드
./gradlew build

# 테스트
./gradlew test

# 실행
./gradlew bootRun

# 특정 프로파일로 실행
./gradlew bootRun --args='--spring.profiles.active=dev'
```

## 환경 설정

### application.yaml
- `application.yaml`: 기본 설정
- `application-test.yml`: 테스트 환경
- `application-prod.yaml`: 프로덕션 환경

---

**작업 중 질문이나 설계 고민이 있다면, AI 페어 프로그래밍 파트너에게 물어보세요!**  
→ `.github/copilot-instructions.md`의 원칙을 기반으로 조언해드립니다.

