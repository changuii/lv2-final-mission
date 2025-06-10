# 맛집 예약 서비스


### 필수 기능
- 사용자는 특정 대상(회의실, 맛집, 클래스 등)을 예약할 수 있습니다.
  - 예약 생성
- 모든 사용자는 예약 현황을 확인할 수 있습니다.
  - 모든 사용자
  - 예약 현황 조회: 특정 날짜, 시간에 예약이 되어 있는지 여부
- 사용자 본인은 자신이 한 예약의 상세 정보까지 확인할 수 있습니다.
  - 사용자 본인만
  - 상세 정보 조회: 예약의 모든 정보
- 사용자는 본인의 예약만 수정하고 삭제할 수 있습니다.
  - 사용자 본인만
  - 수정, 삭제
- 자신이 구현한 기능은 README.md 파일에 명확히 작성해 주세요.

### 필요한 기능
- [x] 예약 생성
  - [x] 인증된 누구나 가능
  - [ ] 예약 생성시 이메일 전송
- [ ] 특정 식당, 날짜에 대한 예약 현황 조회
  - [ ] 인증된 누구나 가능
- [x] 특정 유저의 예약 전체 조회
  - [x] 예약의 소우쥬와 동일
- [x] 특정 예약의 상세 조회
  - [x] 예약의 소유주와 동일
- [ ] 특정 예약의 accept
  - [ ] 가게 소유주와 동일
- [x] 특정 예약의 삭제
  - [x] 예약의 소유주와 동일
- [ ] 식당 생성
- [ ] 전체 식당 조회
- [ ] 특정 식당의 예약 시간 생성
- [x] 로그인
- [x] 회원가입

### 저장이 필요한 정보
- Member
  - id
  - email
  - password
- Reservation
  - id
  - date
  - userId
  - restaurantId
- ReservationTime
  - id
  - time
  - restaurantId
- Restaurant
  - id
  - name

## 기능 목록

### 회원가입
#### 요청
url: `/member`
method: `POST`
body
```json
{
  "email": "asd123@naver.com",
  "password": "password"
}
```
---
#### 응답
**성공**
201 Created
```json
{
"email": "asd123@naver.com"
}
```
**실패**
400 Bad Request
```json
{
    "message": "이미 존재하는 이메일입니다."
}
```

### 로그인
#### 요청
url: `/auth/login`
method: `POST`
body
```json
{
  "email": "asd123@naver.com",
  "password": "password"
}
```
#### 응답
**성공**
201 Created
```json
{
  "token": "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhc2QxMjNAbmF2ZXIuY29tIn0.qddpjJbZCXnig97F-IHvsKXtf8McLAH4AvqbwTCcmGY"
}
```
**실패**
400 Bad Request
```json
{
  "message": "존재하지 않는 이메일입니다."
}
```


### 예약 생성
#### 요청
url: `/reservation`
method: `POST`
header: {
  Authorization: Bearer Token
}
body
```json
{
  "date": "2025-06-11",
  "reservationTimeId": 1,
  "restaurantId": 1
}
```
#### 응답
**성공**
201 Created
```json
{
  "id": 1,
  "date": "2025-06-11",
  "state": "승인 대기중",
  "time": {
    "id": 1,
    "time": "12:00:00"
  },
  "member": {
    "email": "asd123@naver.com"
  },
  "restaurant": {
    "id": 1,
    "name": "만리장성"
  }
}
```
**실패**
400 Bad Request
```json
{
  "message": "존재하지 않는 멤버입니다."
}
```

### 예약 멤버 기준 전체 조회
#### 요청
url: `/reservation`
header: {
  Authorization: Bearer Token
}
method: `GET`

#### 응답
**성공**
200 OK
```json
[
  {
    "id": 1,
    "date": "2025-06-11",
    "state": "승인 대기중",
    "timeId": 1,
    "email": "asd123@naver.com",
    "restaurantId": 1
  },
  {
    "id": 2,
    "date": "2025-06-11",
    "state": "승인 대기중",
    "timeId": 1,
    "email": "asd123@naver.com",
    "restaurantId": 1
  },
  {
    "id": 3,
    "date": "2025-06-11",
    "state": "승인 대기중",
    "timeId": 1,
    "email": "asd123@naver.com",
    "restaurantId": 1
  }
]
```
**실패**
400 Bad Request
```json
{
  "message": "존재하지 않는 멤버입니다."
}
```

### 특정 예약의 상세 조회
#### 요청
url: `/reservation/{id}`
header: {
  Authorization: Bearer Token
}
method: `GET`
path variable: `id`

#### 응답
**성공**
200 OK
```json
{
  "id": 1,
  "date": "2025-06-11",
  "state": "승인 대기중",
  "time": {
    "id": 1,
    "time": "12:00:00"
  },
  "member": {
    "email": "asd123@naver.com"
  },
  "restaurant": {
    "id": 1,
    "name": "만리장성"
  }
}
```
**실패**
400 Bad Request
```json
{
  "message": "존재하지 않는 예약 id 입니다."
}
```

### 특정 예약의 삭제
#### 요청
url: `/reservation/{id}`
method: `DELETE`
header: {
  Authorization: Bearer Token
}
path variable: `id`

#### 응답
**성공**
204 No Content

**실패**
400 Bad Request
```json
{
  "message": "존재하지 않는 예약 id 입니다."
}
```

