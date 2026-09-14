# BE
CEOS-24th MashupDay Team5 모두닥 역기획 프로젝트 백엔드
## Git Convention

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

```bash
# 1. main 브랜치로 이동
git switch main

# 2. 최신 코드 가져오기
git pull origin main

# 3. 작업 브랜치 생성
git switch -c feat/chatbot-create

# 4. 작업 후 커밋
git add .
git commit -m "feat: 챗봇 생성 API 구현"

# 5. 원격 저장소에 Push
git push -u origin feat/chatbot-create
```

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
[FEAT] 챗봇 생성 API 구현
[FEAT] RAG 문서 업로드 기능 구현
[FIX] 존재하지 않는 챗봇 조회 오류 수정
[REFACTOR] 프롬프트 생성 로직 분리
[TEST] 챗봇 서비스 테스트 작성
[DOCS] 챗봇 API 명세 수정
[BUILD] PostgreSQL 드라이버 추가
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

#### Branch 이름 예시

```text
feat/chatbot-create
feat/document-upload
feat/rag-search
fix/chatbot-not-found
refactor/prompt-service
test/chatbot-service
docs/api-specification
build/add-postgresql
```

#### Branch 작성 규칙

- 브랜치 이름에 Issue 번호를 포함하지 않습니다.
- 작업 내용은 영어로 작성합니다.
- 여러 단어는 하이픈으로 구분합니다.
- 하나의 브랜치에서는 하나의 기능이나 문제만 다룹니다.
- 작업이 끝난 브랜치는 Pull Request 병합 후 삭제합니다.
- `main` 브랜치에는 직접 Push하지 않습니다.

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

```text
# 잘못된 예시

수정
코드 변경
최종 수정
오류 해결
여러 가지 작업
```

---

### Pull Request Convention

작업이 완료되면 작업 브랜치에서 `main` 브랜치로 Pull Request를 생성합니다.

#### Pull Request 제목

```text
[TYPE] 작업 내용
```

Issue 제목과 동일한 형식을 사용합니다.

```text
[FEAT] 챗봇 생성 API 구현
[FIX] 문서 삭제 오류 수정
[REFACTOR] 프롬프트 생성 로직 분리
```

#### Pull Request 작성 규칙

- 하나의 Pull Request에는 하나의 작업만 포함합니다.
- 구현 내용과 작업 이유를 작성합니다.
- 테스트 방법과 결과를 작성합니다.
- 관련 Issue가 있다면 `Closes #이슈번호`로 연결합니다.
- 작성자가 먼저 변경된 코드를 확인합니다.
- 팀원 한 명의 코드 리뷰를 받은 뒤 병합합니다.
- 병합할 때는 `Squash and merge`를 사용합니다.
- 병합 후 작업 브랜치는 삭제합니다.

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
## 작업 개요

<!-- 어떤 작업인지 간단하게 설명해주세요. -->


## 작업 목적

<!-- 이 작업이 필요한 이유를 작성해주세요. -->


## 작업 내용

- [ ] 구현할 작업 1
- [ ] 구현할 작업 2
- [ ] 테스트 작성
- [ ] 문서 수정


## 완료 조건

- [ ] 기능이 정상적으로 동작한다.
- [ ] 테스트가 정상적으로 통과한다.
- [ ] 기존 기능에 문제가 발생하지 않는다.


## 참고 사항

<!-- 참고할 문서, 이미지, 링크 또는 추가 내용을 작성해주세요. -->

없음
```

---

### Pull Request Template

Pull Request를 생성할 때 다음 템플릿을 사용합니다.

```markdown
## 변경 내용

<!-- 구현하거나 수정한 내용을 작성해주세요. -->

- 
- 


## 작업 이유

<!-- 해당 작업이 필요한 이유를 작성해주세요. -->


## 테스트

- [ ] 애플리케이션 실행 확인
- [ ] 테스트 코드 통과 확인
- [ ] API 요청 및 응답 확인
- [ ] 기존 기능 동작 확인


## 관련 Issue

<!-- 연결할 Issue가 없다면 '없음'이라고 작성해주세요. -->

Closes #


## API 변경 사항

<!-- 요청 또는 응답이 변경되었다면 작성해주세요. -->

- [ ] API 변경 사항이 있음
- [ ] API 변경 사항이 없음


## 확인 사항

- [ ] `main` 브랜치의 최신 내용을 반영했습니다.
- [ ] 불필요한 로그와 주석을 제거했습니다.
- [ ] API 키나 비밀번호를 포함하지 않았습니다.
- [ ] 변경된 코드를 직접 검토했습니다.


## 참고 사항

<!-- 리뷰어가 확인해야 할 내용이나 참고 자료를 작성해주세요. -->

없음
```