-- 04_리뷰 W열의 '도움이 돼요 수'를 V7로 추가한 후기 57건에 반영한다.
-- 리뷰 ID는 자동 생성되므로 병원 ID, 작성자 닉네임, UTC 작성일로 대상을 식별한다.

-- 강남브랜드안과의원 강남점

UPDATE review SET helpful_count = 0
WHERE hospital_id = 1 AND created_at = '2024-08-06 00:00:00'
  AND user_id IN (SELECT user_id FROM `user` WHERE nickname = '스페인 물까치12');

UPDATE review SET helpful_count = 11
WHERE hospital_id = 1 AND created_at = '2025-04-09 00:00:00'
  AND user_id IN (SELECT user_id FROM `user` WHERE nickname = '칠레 홍학27');

UPDATE review SET helpful_count = 9
WHERE hospital_id = 1 AND created_at = '2024-06-27 00:00:00'
  AND user_id IN (SELECT user_id FROM `user` WHERE nickname = '노르웨이 바다오리8');

UPDATE review SET helpful_count = 17
WHERE hospital_id = 1 AND created_at = '2025-08-16 00:00:00'
  AND user_id IN (SELECT user_id FROM `user` WHERE nickname = '몽골 수리부엉이31');

UPDATE review SET helpful_count = 20
WHERE hospital_id = 1 AND created_at = '2024-12-03 00:00:00'
  AND user_id IN (SELECT user_id FROM `user` WHERE nickname = '핀란드 흰올빼미19');

UPDATE review SET helpful_count = 16
WHERE hospital_id = 1 AND created_at = '2025-02-11 00:00:00'
  AND user_id IN (SELECT user_id FROM `user` WHERE nickname = '캐나다 비버52');

UPDATE review SET helpful_count = 20
WHERE hospital_id = 1 AND created_at = '2024-10-05 00:00:00'
  AND user_id IN (SELECT user_id FROM `user` WHERE nickname = '이집트 따오기7');

UPDATE review SET helpful_count = 9
WHERE hospital_id = 1 AND created_at = '2025-06-28 00:00:00'
  AND user_id IN (SELECT user_id FROM `user` WHERE nickname = '호주 웜뱃44');

UPDATE review SET helpful_count = 46
WHERE hospital_id = 1 AND created_at = '2024-09-21 00:00:00'
  AND user_id IN (SELECT user_id FROM `user` WHERE nickname = '케냐 얼룩말63');

UPDATE review SET helpful_count = 7
WHERE hospital_id = 1 AND created_at = '2025-01-17 00:00:00'
  AND user_id IN (SELECT user_id FROM `user` WHERE nickname = '멕시코 벌새16');

UPDATE review SET helpful_count = 2
WHERE hospital_id = 1 AND created_at = '2025-04-29 00:00:00'
  AND user_id IN (SELECT user_id FROM `user` WHERE nickname = '행복한 꺽정이35');

UPDATE review SET helpful_count = 3
WHERE hospital_id = 1 AND created_at = '2024-11-15 00:00:00'
  AND user_id IN (SELECT user_id FROM `user` WHERE nickname = '알제리 무당개구리1');

UPDATE review SET helpful_count = 8
WHERE hospital_id = 1 AND created_at = '2026-05-08 00:00:00'
  AND user_id IN (SELECT user_id FROM `user` WHERE nickname = '청렴한 꼬정모기35');

-- 클리어서울안과의원

UPDATE review SET helpful_count = 1
WHERE hospital_id = 2 AND created_at = '2024-07-30 00:00:00'
  AND user_id IN (SELECT user_id FROM `user` WHERE nickname = '탄자니아 솔개39');

UPDATE review SET helpful_count = 5
WHERE hospital_id = 2 AND created_at = '2025-05-24 00:00:00'
  AND user_id IN (SELECT user_id FROM `user` WHERE nickname = '루마니아 종다리26');

UPDATE review SET helpful_count = 12
WHERE hospital_id = 2 AND created_at = '2024-11-08 00:00:00'
  AND user_id IN (SELECT user_id FROM `user` WHERE nickname = '아이티 넓적부리35');

UPDATE review SET helpful_count = 5
WHERE hospital_id = 2 AND created_at = '2025-07-21 00:00:00'
  AND user_id IN (SELECT user_id FROM `user` WHERE nickname = '과테말라 배스125');

UPDATE review SET helpful_count = 23
WHERE hospital_id = 2 AND created_at = '2024-09-17 00:00:00'
  AND user_id IN (SELECT user_id FROM `user` WHERE nickname = '우간다 큰고니35');

UPDATE review SET helpful_count = 5
WHERE hospital_id = 2 AND created_at = '2025-01-30 00:00:00'
  AND user_id IN (SELECT user_id FROM `user` WHERE nickname = '세네갈 멋쟁이35');

UPDATE review SET helpful_count = 9
WHERE hospital_id = 2 AND created_at = '2024-12-22 00:00:00'
  AND user_id IN (SELECT user_id FROM `user` WHERE nickname = '터키 풍조13');

UPDATE review SET helpful_count = 0
WHERE hospital_id = 2 AND created_at = '2025-05-11 00:00:00'
  AND user_id IN (SELECT user_id FROM `user` WHERE nickname = '세네갈 산달125');

UPDATE review SET helpful_count = 10
WHERE hospital_id = 2 AND created_at = '2024-08-06 00:00:00'
  AND user_id IN (SELECT user_id FROM `user` WHERE nickname = '몽골 노랑부리44');

UPDATE review SET helpful_count = 17
WHERE hospital_id = 2 AND created_at = '2025-02-19 00:00:00'
  AND user_id IN (SELECT user_id FROM `user` WHERE nickname = '칠레 바다제비27');

UPDATE review SET helpful_count = 19
WHERE hospital_id = 2 AND created_at = '2024-10-28 00:00:00'
  AND user_id IN (SELECT user_id FROM `user` WHERE nickname = '페루 큰부리새81');

UPDATE review SET helpful_count = 15
WHERE hospital_id = 2 AND created_at = '2025-06-03 00:00:00'
  AND user_id IN (SELECT user_id FROM `user` WHERE nickname = '케냐 물총새19');

UPDATE review SET helpful_count = 12
WHERE hospital_id = 2 AND created_at = '2025-04-16 00:00:00'
  AND user_id IN (SELECT user_id FROM `user` WHERE nickname = '볼리비아 콘야비15');

UPDATE review SET helpful_count = 26
WHERE hospital_id = 2 AND created_at = '2024-11-02 00:00:00'
  AND user_id IN (SELECT user_id FROM `user` WHERE nickname = '레소토 바다수달5');

UPDATE review SET helpful_count = 11
WHERE hospital_id = 2 AND created_at = '2024-08-05 00:00:00'
  AND user_id IN (SELECT user_id FROM `user` WHERE nickname = '포르투갈 물떼새2');

-- 강남조은눈안과의원

UPDATE review SET helpful_count = 0
WHERE hospital_id = 3 AND created_at = '2026-09-30 00:00:00'
  AND user_id IN (SELECT user_id FROM `user` WHERE nickname = '기니 연어45');

UPDATE review SET helpful_count = 0
WHERE hospital_id = 3 AND created_at = '2026-09-30 00:00:00'
  AND user_id IN (SELECT user_id FROM `user` WHERE nickname = '알바니아 되새35');

UPDATE review SET helpful_count = 8
WHERE hospital_id = 3 AND created_at = '2026-09-24 00:00:00'
  AND user_id IN (SELECT user_id FROM `user` WHERE nickname = '모리타니 족제비45');

UPDATE review SET helpful_count = 2
WHERE hospital_id = 3 AND created_at = '2026-09-16 00:00:00'
  AND user_id IN (SELECT user_id FROM `user` WHERE nickname = '바하마 설치렁이24');

UPDATE review SET helpful_count = 7
WHERE hospital_id = 3 AND created_at = '2026-09-16 00:00:00'
  AND user_id IN (SELECT user_id FROM `user` WHERE nickname = '베냉 쇠왕부리235');

UPDATE review SET helpful_count = 20
WHERE hospital_id = 3 AND created_at = '2026-09-16 00:00:00'
  AND user_id IN (SELECT user_id FROM `user` WHERE nickname = '투발루 꼬꼬리룩');

UPDATE review SET helpful_count = 8
WHERE hospital_id = 3 AND created_at = '2026-07-29 00:00:00'
  AND user_id IN (SELECT user_id FROM `user` WHERE nickname = '태국 신천옹류135');

UPDATE review SET helpful_count = 14
WHERE hospital_id = 3 AND created_at = '2026-07-16 00:00:00'
  AND user_id IN (SELECT user_id FROM `user` WHERE nickname = '오스트리아 다람쥐5');

UPDATE review SET helpful_count = 16
WHERE hospital_id = 3 AND created_at = '2026-06-12 00:00:00'
  AND user_id IN (SELECT user_id FROM `user` WHERE nickname = '착한 흰꼬리매134');

UPDATE review SET helpful_count = 4
WHERE hospital_id = 3 AND created_at = '2025-12-24 00:00:00'
  AND user_id IN (SELECT user_id FROM `user` WHERE nickname = '스웨덴 왕뱀류125');

UPDATE review SET helpful_count = 9
WHERE hospital_id = 3 AND created_at = '2026-03-31 00:00:00'
  AND user_id IN (SELECT user_id FROM `user` WHERE nickname = '이라크 해리스매35');

-- 하늘안과의원 강남

UPDATE review SET helpful_count = 13
WHERE hospital_id = 5 AND created_at = '2026-08-21 00:00:00'
  AND user_id IN (SELECT user_id FROM `user` WHERE nickname = '아금바리');

UPDATE review SET helpful_count = 1
WHERE hospital_id = 5 AND created_at = '2026-04-07 00:00:00'
  AND user_id IN (SELECT user_id FROM `user` WHERE nickname = '미얀마 귀뚜라미35');

UPDATE review SET helpful_count = 11
WHERE hospital_id = 5 AND created_at = '2026-03-20 00:00:00'
  AND user_id IN (SELECT user_id FROM `user` WHERE nickname = '이란 무당개구리35');

UPDATE review SET helpful_count = 17
WHERE hospital_id = 5 AND created_at = '2025-03-15 00:00:00'
  AND user_id IN (SELECT user_id FROM `user` WHERE nickname = '폴란드 군대개미15');

UPDATE review SET helpful_count = 5
WHERE hospital_id = 5 AND created_at = '2026-10-02 00:00:00'
  AND user_id IN (SELECT user_id FROM `user` WHERE nickname = '쿠바 굴뚝새45');

UPDATE review SET helpful_count = 5
WHERE hospital_id = 5 AND created_at = '2026-09-30 00:00:00'
  AND user_id IN (SELECT user_id FROM `user` WHERE nickname = '북키프로스 악어45');

UPDATE review SET helpful_count = 0
WHERE hospital_id = 5 AND created_at = '2026-09-30 00:00:00'
  AND user_id IN (SELECT user_id FROM `user` WHERE nickname = '몰타 돼지코자리45');

UPDATE review SET helpful_count = 0
WHERE hospital_id = 5 AND created_at = '2026-09-16 00:00:00'
  AND user_id IN (SELECT user_id FROM `user` WHERE nickname = '페루 윤표1235');

UPDATE review SET helpful_count = 0
WHERE hospital_id = 5 AND created_at = '2026-09-16 00:00:00'
  AND user_id IN (SELECT user_id FROM `user` WHERE nickname = '카메룬 깍도요45');

UPDATE review SET helpful_count = 1
WHERE hospital_id = 5 AND created_at = '2026-09-09 00:00:00'
  AND user_id IN (SELECT user_id FROM `user` WHERE nickname = '우간다 도치1235');

UPDATE review SET helpful_count = 2
WHERE hospital_id = 5 AND created_at = '2026-09-09 00:00:00'
  AND user_id IN (SELECT user_id FROM `user` WHERE nickname = '말라위 늪거북235');

-- 명동콤비치과의원

UPDATE review SET helpful_count = 12
WHERE hospital_id = 12 AND created_at = '2026-08-28 00:00:00'
  AND user_id IN (SELECT user_id FROM `user` WHERE nickname = '달구지67');

UPDATE review SET helpful_count = 12
WHERE hospital_id = 12 AND created_at = '2026-08-26 00:00:00'
  AND user_id IN (SELECT user_id FROM `user` WHERE nickname = '조지아 나방135');

UPDATE review SET helpful_count = 7
WHERE hospital_id = 12 AND created_at = '2026-08-26 00:00:00'
  AND user_id IN (SELECT user_id FROM `user` WHERE nickname = '페루 거위1235');

UPDATE review SET helpful_count = 6
WHERE hospital_id = 12 AND created_at = '2026-08-25 00:00:00'
  AND user_id IN (SELECT user_id FROM `user` WHERE nickname = '사모아 늪영양123');

UPDATE review SET helpful_count = 7
WHERE hospital_id = 12 AND created_at = '2026-04-30 00:00:00'
  AND user_id IN (SELECT user_id FROM `user` WHERE nickname = '벨기에 살모사3');

UPDATE review SET helpful_count = 8
WHERE hospital_id = 12 AND created_at = '2026-05-16 00:00:00'
  AND user_id IN (SELECT user_id FROM `user` WHERE nickname = '체코 양진이35');

UPDATE review SET helpful_count = 14
WHERE hospital_id = 12 AND created_at = '2026-03-20 00:00:00'
  AND user_id IN (SELECT user_id FROM `user` WHERE nickname = '아리쫑');
