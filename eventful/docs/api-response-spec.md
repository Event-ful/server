# API 응답 규격

## 네이밍 컨벤션
모든 API 응답은 **snake_case**를 사용합니다.

## 공통 응답 구조

### 성공 응답
```json
{
  "status_code": 200,
  "data": {
    // 실제 데이터
  }
}
```

### 에러 응답
```json
{
  "status_code": 400,
  "error_message": "에러 메시지",
  "division_code": "ERROR_CODE"
}
```

## URL 경로 규칙
- Path Variable: **케밥 케이스(kebab-case)** 사용
  - 예: `/api/event-group/{group-id}/join`
- Request/Response Body: **snake_case** 사용

## 그룹(EventGroup) API 응답 예시

### 1. 그룹 생성
**POST** `/api/event-group`

**Request Body**
```json
{
  "group_name": "여행 그룹",
  "group_description": "제주도 여행",
  "image_url": "https://example.com/image.jpg"
}
```

**Response**
```json
{
  "status_code": 200,
  "data": {
    "group_id": 1,
    "group_name": "여행 그룹",
    "group_description": "제주도 여행",
    "image_url": "https://example.com/image.jpg",
    "join_password": "Abc123!@"
  }
}
```

### 2. 그룹 참여
**POST** `/api/event-group/{group-id}/join`

**Request Body**
```json
{
  "group_password": "Abc123!@"
}
```

**Response**
```json
{
  "status_code": 200,
  "data": {
    "group_id": 1
  }
}
```

### 3. 그룹 조인 코드 검증
**POST** `/api/event-group/verify-code`

**Request Body**
```json
{
  "join_code": "ABC12345"
}
```

**Response**
```json
{
  "status_code": 200,
  "data": {
    "group_id": 1,
    "group_name": "여행 그룹",
    "group_description": "제주도 여행"
  }
}
```

### 4. 그룹 정보 조회
**GET** `/api/event-group/{group-id}`

**Response**
```json
{
  "status_code": 200,
  "data": {
    "group_name": "여행 그룹",
    "group_description": "제주도 여행",
    "is_leader": true,
    "member_count": 5,
    "join_code": "ABC12345",
    "group_password": "Abc123!@",
    "group_member": [
      {
        "member_id": 1,
        "member_name": "홍길동",
        "is_leader": true
      },
      {
        "member_id": 2,
        "member_name": "김철수",
        "is_leader": false
      }
    ]
  }
}
```

### 5. 그룹 정보 수정
**PUT** `/api/event-group/{group-id}`

**Request Body**
```json
{
  "group_name": "수정된 그룹명",
  "group_description": "수정된 설명",
  "group_image": "https://example.com/new-image.jpg"
}
```

**Response**
```json
{
  "status_code": 200,
  "data": null
}
```

## 회원(Member) API 응답 예시

### 1. 로그인
**POST** `/api/auth/login`

**Request Body**
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

**Response**
```json
{
  "status_code": 200,
  "data": "Success"
}
```

### 2. 회원가입
**POST** `/api/members/signup`

**Request Body**
```json
{
  "email": "user@example.com",
  "password": "Password123!",
  "nickname": "홍길동",
  "verification_code": "123456"
}
```

**Response**
```json
{
  "status_code": 200,
  "data": null
}
```

### 3. 이메일 인증 코드 발송
**POST** `/api/members/signup/verify-email`

**Request Body**
```json
{
  "email": "user@example.com"
}
```

**Response**
```json
{
  "status_code": 200,
  "data": {
    "verification_code": "123456"
  }
}
```

### 4. 이메일 인증 코드 확인
**POST** `/api/members/signup/verify-email/confirm`

**Request Body**
```json
{
  "email": "user@example.com",
  "verification_code": "123456"
}
```

**Response**
```json
{
  "status_code": 200,
  "data": null
}
```

### 5. 닉네임 중복 확인
**POST** `/api/members/signup/check-nickname`

**Request Body**
```json
{
  "nickname": "홍길동"
}
```

**Response**
```json
{
  "status_code": 200,
  "data": null
}
```

## 주의사항

1. **일관성**: 모든 JSON 필드는 snake_case를 사용합니다.
2. **Request Body**: 클라이언트가 보내는 요청도 snake_case를 사용해야 합니다.
3. **Path Variable**: URL 경로는 kebab-case를 사용합니다 (예: `group-id`).
4. **Jackson 설정**: `application.yaml`에 `spring.jackson.property-naming-strategy: SNAKE_CASE` 설정이 전역으로 적용되어 있습니다.
5. **전역 설정 의존**: DTO 클래스는 camelCase로 작성하고, Jackson이 자동으로 snake_case로 변환합니다.
