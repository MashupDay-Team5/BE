# 기준 데이터 입력

`master-data.sql`은 진료 분류와 지역 기준 데이터를 개발 DB에 한 번만 입력하는 SQL입니다.

## 실행 전제

- 애플리케이션을 한 번 실행해 Hibernate가 최신 Entity 기준으로 스키마와 UNIQUE 제약을 반영해야 합니다.
- `medical_category`, `treatment_category`, `treatment`, `region` 테이블이 비어 있어야 합니다.
- SQL은 UTF-8로 읽도록 설정한 DB 클라이언트에서 트랜잭션으로 실행하고, 오류가 발생하면 전체를 롤백해야 합니다.

## 실행 후 확인

```sql
SELECT COUNT(*) FROM medical_category;
SELECT COUNT(*) FROM treatment_category;
SELECT COUNT(*) FROM treatment;
SELECT region_type, COUNT(*) FROM region GROUP BY region_type;
```

정상 입력 결과는 medical category 20개, treatment category 98개, treatment 577개, SIDO 17개, SIGUNGU 135개입니다. SQL을 다시 실행하면 PK 또는 UNIQUE 제약 위반으로 실패하는 것이 정상입니다.
