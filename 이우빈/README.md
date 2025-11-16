# 25-26-Backend-Assignment-05

## 주요 기능

- 회원가입/로그인(로컬)
- Google OAuth2 로그인 (Authorization Code → Access Token → Google Profile)
- 액세스 토큰(JWT) + 리프레시 토큰(쿠키, 회전/로테이션)
- 게시글 CRUD (권한 체크: 작성자 또는 ADMIN)
- 댓글 CRUD (권한 체크: 작성자 또는 ADMIN)
- 정적 페이지:
    - `/index.html` 홈
    - `/signup.html` 회원가입
    - `/login.html` 로그인(로컬/구글)
    - `/posts.html` 글 목록
    - `/post.html?id={postId}` 글 상세 + 댓글
    - `/createPost.html` 글 작성
    - `/myPage.html` 내 정보
    - `/admin.html` 어드민 테스트(ROLE_ADMIN 전용 API 호출)

## 인증/인가 흐름

### 로컬 로그인

1. `/auth/login` (이메일/비밀번호) → Refresh Token(쿠키)
2. 프론트는 `api.js`가 Access Token을 메모리에 보관하고, API 호출 시 `Authorization: Bearer` 헤더에 포함
3. Access Token 만료 시 자동으로 `/auth/refresh` 호출 → 새 Access Token 발급(+ 필요 시 Refresh 회전)

### Google OAuth2

1. `/oauth2/authorize/google` → Google
2. 콜백(`/oauth2/callback/google`)에서 `code` 수신
3. 백엔드가 Google 토큰 엔드포인트에 `code` 교환 → Google Access Token 획득
4. Google UserInfo 조회 → 사용자 생성/연동 → **내 서비스 Access/Refresh 발급**
5. 프론트는 `api.js.bootstrapAfterGoogle()`에서 `/auth/refresh`로 교환해 Access Token 세팅

### Refresh Token 회전(rotate)

- `/auth/refresh` 호출 시 만료 임박(설정값 이하)하면 새 Refresh 발급 + 기존 저장값 교체
- 항상 새로운 Access Token 발급
- Refresh Token은 HttpOnly **쿠키**로만 보관