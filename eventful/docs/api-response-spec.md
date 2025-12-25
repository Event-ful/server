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
  "division_code": null
}
```

**참고:**
- `division_code`는 **대부분의 경우 `null`**입니다.
- `BusinessException`을 명시적으로 사용하는 특정 비즈니스 예외(예: 이메일 인증 관련)에서만 `division_code`가 포함됩니다.
- 일반적인 유효성 검증 실패나 도메인 로직 예외는 `IllegalArgumentException`을 사용하며, 이 경우 `division_code`는 `null`입니다.

**현재 사용 중인 division_code 목록:**
| division_code | 설명 | 사용 위치 |
|---------------|------|----------|
| `EMAIL_CONFIRM_INVALID_CODE` | 잘못된 이메일 인증 코드 | 이메일 인증 확인 |
| `EMAIL_CONFIRM_ALREADY_VERIFIED` | 이미 인증된 이메일 | 이메일 인증 확인 |
| `EMAIL_CONFIRM_EXPIRED` | 인증 코드 만료 | 이메일 인증 확인 |
| `CHECK_NICKNAME_EXISTS` | 중복된 닉네임 | 닉네임 중복 확인 |
| `CHECK_NICKNAME_REQUIRED` | 닉네임 필수 | 닉네임 중복 확인 |

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

## 이벤트(Event) API 응답 예시

### 1. 이벤트 생성
**POST** `/api/event`

**Request Body**
```json
{
  "event_group_id": 1,
  "event_name": "서울 맛집 탐방",
  "event_description": "강남역 근처 맛집 탐방",
  "event_max_member": 10,
  "event_date": "2025-03-15",
  "place_id": "ChIJN1t_tDeuEmsRUsoyG83frY4"
}
```

**Response (성공 - 200 OK)**
```json
{
  "status_code": 200,
  "data": {
    "event_id": 1,
    "event_group_id": 1,
    "event_name": "서울 맛집 탐방",
    "event_description": "강남역 근처 맛집 탐방",
    "event_max_member": 10,
    "event_date": "2025-03-15",
    "place_id": "ChIJN1t_tDeuEmsRUsoyG83frY4"
  }
}
```

**Error Response (400 Bad Request - 유효성 검증 실패)**
```json
{
  "status_code": 400,
  "error_message": "eventName: 이벤트 이름은 필수입니다",
  "division_code": null
}
```

**Error Response (400 Bad Request - 그룹원이 아님)**
```json
{
  "status_code": 400,
  "error_message": "그룹원만 이벤트를 생성할 수 있습니다.",
  "division_code": null
}
```

**Error Response (400 Bad Request - 최대 참여 인원 초과)**
```json
{
  "status_code": 400,
  "error_message": "최대 참여 인원을 초과했습니다.",
  "division_code": null
}
```

## 주의사항

1. **일관성**: 모든 JSON 필드는 snake_case를 사용합니다.
2. **Request Body**: 클라이언트가 보내는 요청도 snake_case를 사용해야 합니다.
3. **Path Variable**: URL 경로는 kebab-case를 사용합니다 (예: `group-id`).
4. **Jackson 설정**: `application.yaml`에 `spring.jackson.property-naming-strategy: SNAKE_CASE` 설정이 전역으로 적용되어 있습니다.
5. **전역 설정 의존**: DTO 클래스는 camelCase로 작성하고, Jackson이 자동으로 snake_case로 변환합니다.

## 파일 업로드(File Upload) API

### 1. 파일 업로드
**POST** `/api/files/upload`

**Content-Type**: `multipart/form-data`

#### Request Parameters
| 파라미터 | 타입 | 필수 | 설명 | 기본값 |
|---------|------|------|------|--------|
| file | File | ✅ | 업로드할 파일 (최대 10MB) | - |
| directory | String | ❌ | 저장 디렉토리 (`GENERAL`, `GROUP_IMAGES`, `PROFILE_IMAGES`) | `GENERAL` |

#### 허용되는 파일 형식
- **이미지**: jpg, jpeg, png, gif, webp
- **문서**: pdf

#### Response (성공 - 200 OK)
```json
{
  "status_code": 200,
  "data": {
    "file_name": "group-photo.jpg",
    "stored_file_name": "a1b2c3d4-e5f6-7890-abcd-ef1234567890.jpg",
    "file_url": "https://objectstorage.ap-chuncheon-1.oraclecloud.com/n/namespace/b/bucket/o/group-images/a1b2c3d4-e5f6-7890-abcd-ef1234567890.jpg",
    "content_type": "image/jpeg",
    "file_size": 2048576,
    "uploaded_at": "2025-11-18T10:30:00"
  }
}
```

#### Response 필드 설명
| 필드 | 타입 | 설명 |
|------|------|------|
| file_name | String | 원본 파일명 |
| stored_file_name | String | 서버에 저장된 파일명 (UUID 기반) |
| file_url | String | 파일에 접근할 수 있는 Public URL |
| content_type | String | 파일의 MIME 타입 (예: `image/jpeg`) |
| file_size | Long | 파일 크기 (bytes) |
| uploaded_at | String | 업로드 완료 시각 (ISO 8601 형식) |

#### Error Response (400 Bad Request - 파일 없음)
```json
{
  "status_code": 400,
  "error_message": "파일이 비어있습니다.",
  "division_code": null
}
```

#### Error Response (400 Bad Request - 파일 크기 초과)
```json
{
  "status_code": 400,
  "error_message": "파일 크기는 10MB를 초과할 수 없습니다.",
  "division_code": null
}
```

#### Error Response (500 Internal Server Error)
```json
{
  "status_code": 500,
  "error_message": "서버 내부 오류가 발생했습니다.",
  "division_code": null
}
```

### 디렉토리별 사용 목적
| 디렉토리 | 용도 | 예시 |
|---------|------|------|
| `GENERAL` | 일반 파일 (기본값) | 영수증 이미지, 기타 첨부 파일 |
| `GROUP_IMAGES` | 그룹 대표 이미지 | 그룹 생성/수정 시 업로드하는 그룹 썸네일 |
| `PROFILE_IMAGES` | 프로필 이미지 | 회원 프로필 사진 |

### 주의사항

1. **파일 크기 제한**: 현재 최대 10MB까지 업로드 가능
2. **세션 인증**: 로그인된 사용자만 파일 업로드 가능 (세션 쿠키 필요)
3. **파일명 보안**: 서버에서 UUID 기반으로 자동 생성하여 중복 및 보안 문제 방지
4. **URL 유효성**: 반환된 `file_url`은 Public URL이므로 바로 `<img>` 태그에 사용 가능
5. **에러 처리**: 파일 크기, 형식 등을 클라이언트에서 미리 검증하면 UX 개선
6. **진행률 표시**: 대용량 파일 업로드 시 `XMLHttpRequest.upload.onprogress` 또는 Axios의 `onUploadProgress` 활용 권장

### 향후 추가 예정 기능 (TODO)
- 이미지 리사이징 (썸네일 자동 생성)
- 여러 파일 동시 업로드 (batch upload)
- 파일 삭제 API
- 업로드 진행률 WebSocket 지원
