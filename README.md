# BE
CEOS-24th MashupDay Team5 모두닥 역기획 프로젝트 백엔드

## Git Convention

## 브랜치 전략

```
main        # 배포 가능한 안정 버전 (직접 push 금지, PR로만)
develop     # 개발 통합 브랜치
feature/*   # 기능 개발
fix/*       # 버그 수정
```

**브랜치 네이밍**

```
feature/#12-schedule-generation
fix/#27-login-token-error
```

- `타입/#이슈번호-간단한-설명` 형식
- 설명은 소문자 + 하이픈(`-`) 연결
- 작업은 항상 이슈를 먼저 만들고 → 브랜치를 판다

---

### Issue Convention

새로운 기능, 오류 수정, 리팩토링 등의 작업을 시작하기 전에 GitHub Issue를 생성합니다.

#### Issue 제목

```text
[TYPE] 작업 내용
```

| Type | 용도 |
| --- | --- |
| `FEAT` | 새로운 기능 구현 |
| `FIX` | 버그 수정 |
| `REFACTOR` | 기능 변화 없는 코드 개선 |
| `TEST` | 테스트 코드 작성 및 수정 |
| `DOCS` | 문서 작성 및 수정 |
| `CHORE` | 설정 및 기타 작업 |
| `BUILD` | Gradle 및 의존성 변경 |

#### Issue 제목 예시

```text
[FEAT] 생성 API 구현
[FIX] 존재하지 않는 데이터 조회 오류 수정
[REFACTOR] 프롬프트 생성 로직 분리
[TEST] 서비스 테스트 작성
[DOCS] API 명세 수정
[BUILD] MySQL 드라이버 추가
```

#### Issue 작성 규칙

- 하나의 Issue에는 하나의 작업만 작성합니다.
- 작업 목적과 구현할 내용을 명확하게 작성합니다.
- 작업 내용을 체크박스로 나누어 진행 상황을 확인합니다.
- 완료 조건을 구체적으로 작성합니다.
- 관련 자료나 참고사항이 있다면 함께 첨부합니다.

---

### Branch Convention

모든 작업 브랜치는 최신 `main` 브랜치에서 생성합니다.

#### Branch 이름

```text
type/작업내용
```

작업 내용은 영문 소문자와 하이픈(`-`)을 사용합니다.

| Type | 용도 |
| --- | --- |
| `feat` | 새로운 기능 구현 |
| `fix` | 버그 수정 |
| `refactor` | 기능 변화 없는 코드 개선 |
| `test` | 테스트 코드 작성 및 수정 |
| `docs` | 문서 작성 및 수정 |
| `chore` | 설정 및 기타 작업 |
| `build` | Gradle 및 의존성 변경 |



---

### Commit Convention

커밋 메시지는 다음 형식을 사용합니다.

```text
type: 작업 내용
```

| Type | 용도 |
| --- | --- |
| `feat` | 새로운 기능 추가 |
| `fix` | 버그 수정 |
| `refactor` | 기능 변화 없는 코드 개선 |
| `test` | 테스트 코드 추가 및 수정 |
| `docs` | 문서 작성 및 수정 |
| `chore` | 환경설정 및 기타 작업 |
| `build` | Gradle 및 의존성 변경 |
| `style` | 공백, 들여쓰기 등 코드 형식 수정 |

#### Commit 메시지 예시

```text
feat: 챗봇 생성 API 구현
feat: 문서 업로드 기능 추가
fix: 존재하지 않는 챗봇 조회 오류 수정
refactor: 프롬프트 생성 로직 분리
test: 챗봇 생성 서비스 테스트 추가
docs: 챗봇 API 사용 방법 추가
chore: 환경변수 예제 파일 추가
build: PostgreSQL 드라이버 의존성 추가
style: 코드 들여쓰기 수정
```

#### Commit 작성 규칙

- 작업 내용을 명확하고 간결하게 작성합니다.
- 작업 내용은 한글로 작성해도 됩니다.
- 문장 끝에 마침표를 붙이지 않습니다.
- 하나의 커밋에는 하나의 목적만 담습니다.
- 의미가 불분명한 커밋 메시지는 사용하지 않습니다.



---

### Pull Request Convention

작업이 완료되면 작업 브랜치에서 `main` 브랜치로 Pull Request를 생성합니다.


#### Pull Request 작성 규칙

- 하나의 Pull Request에는 하나의 작업만 포함합니다.
- 구현 내용과 작업 이유를 작성합니다.
- 테스트 방법과 결과를 작성합니다.
- 관련 Issue가 있다면 `Closes #이슈번호`로 연결합니다.
- 팀원 한 명의 코드 리뷰를 받은 뒤 병합합니다.

#### Pull Request 확인 사항

- 애플리케이션이 정상적으로 실행되는가?
- 기존 기능에 문제가 생기지 않았는가?
- 테스트가 정상적으로 통과하는가?
- API 변경 사항을 명세에 반영했는가?
- 불필요한 로그와 주석을 제거했는가?
- API 키, 비밀번호, `.env` 파일이 포함되지 않았는가?
- `build`, `.gradle`, `.idea` 파일이 포함되지 않았는가?

---

## Templates

### Issue Template

Issue를 생성할 때 다음 템플릿을 사용합니다.

```markdown
---
name: Issue template
about: Suggest an idea for this project
title: ''
labels: ''
assignees: ''

---

## 작업내용

-

<br/>

## TODOS
- [ ] todo

<br/>


## 📝 참고 사항
> 참고 사항을 적어주세요. 해당 작업을 하는 사람이 참고해야 하는 내용을 자유로운 형식으로 적을 수 있습니다.
```

---

### Pull Request Template

Pull Request를 생성할 때 다음 템플릿을 사용합니다.

```markdown
## 작업 내용
- 

## 관련 이슈
- Closes #00

## 체크리스트
- [ ] 로컬에서 정상 동작 확인
- [ ] 불필요한 콘솔/주석 제거
- [ ] 리뷰어 지정 완료
```

### Workflow

모든 작업은 다음 순서로 진행합니다.

```text
GitHub Issue 생성
    ↓
main 브랜치 최신화
    ↓
작업 브랜치 생성
    ↓
기능 구현 및 테스트
    ↓
커밋 및 Push
    ↓
Pull Request 생성
    ↓
팀원 코드 리뷰
    ↓
Squash and merge
    ↓
작업 브랜치 삭제
```
