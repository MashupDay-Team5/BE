-- V4 시술·지역 데이터, V5 병원·상품 데이터, V6 예약 유형 변경 이후 실행한다.
-- 작성일은 제공된 날짜의 UTC 00:00:00으로 저장한다.
-- 결제항목 중 금액을 알 수 없는 항목이 있으면 전체 결제 금액은 NULL로 저장한다.

SET @review_seed_hospital_1 = (SELECT hospital_id FROM hospital WHERE name = '강남브랜드안과의원 강남점');
SET @review_seed_hospital_2 = (SELECT hospital_id FROM hospital WHERE name = '클리어서울안과의원');
SET @review_seed_hospital_3 = (SELECT hospital_id FROM hospital WHERE name = '강남조은눈안과의원');
SET @review_seed_hospital_4 = (SELECT hospital_id FROM hospital WHERE name = '하늘안과의원 강남');
SET @review_seed_hospital_5 = (SELECT hospital_id FROM hospital WHERE name = '명동콤비치과의원');

INSERT INTO `user` (nickname) VALUES ('스페인 물까치12');
SET @review_seed_author_1 = LAST_INSERT_ID();

INSERT INTO `user` (nickname) VALUES ('칠레 홍학27');
SET @review_seed_author_2 = LAST_INSERT_ID();

INSERT INTO `user` (nickname) VALUES ('노르웨이 바다오리8');
SET @review_seed_author_3 = LAST_INSERT_ID();

INSERT INTO `user` (nickname) VALUES ('몽골 수리부엉이31');
SET @review_seed_author_4 = LAST_INSERT_ID();

INSERT INTO `user` (nickname) VALUES ('핀란드 흰올빼미19');
SET @review_seed_author_5 = LAST_INSERT_ID();

INSERT INTO `user` (nickname) VALUES ('캐나다 비버52');
SET @review_seed_author_6 = LAST_INSERT_ID();

INSERT INTO `user` (nickname) VALUES ('이집트 따오기7');
SET @review_seed_author_7 = LAST_INSERT_ID();

INSERT INTO `user` (nickname) VALUES ('호주 웜뱃44');
SET @review_seed_author_8 = LAST_INSERT_ID();

INSERT INTO `user` (nickname) VALUES ('케냐 얼룩말63');
SET @review_seed_author_9 = LAST_INSERT_ID();

INSERT INTO `user` (nickname) VALUES ('멕시코 벌새16');
SET @review_seed_author_10 = LAST_INSERT_ID();

INSERT INTO `user` (nickname) VALUES ('행복한 꺽정이35');
SET @review_seed_author_11 = LAST_INSERT_ID();

INSERT INTO `user` (nickname) VALUES ('알제리 무당개구리1');
SET @review_seed_author_12 = LAST_INSERT_ID();

INSERT INTO `user` (nickname) VALUES ('청렴한 꼬정모기35');
SET @review_seed_author_13 = LAST_INSERT_ID();

INSERT INTO `user` (nickname) VALUES ('탄자니아 솔개39');
SET @review_seed_author_14 = LAST_INSERT_ID();

INSERT INTO `user` (nickname) VALUES ('루마니아 종다리26');
SET @review_seed_author_15 = LAST_INSERT_ID();

INSERT INTO `user` (nickname) VALUES ('아이티 넓적부리35');
SET @review_seed_author_16 = LAST_INSERT_ID();

INSERT INTO `user` (nickname) VALUES ('과테말라 배스125');
SET @review_seed_author_17 = LAST_INSERT_ID();

INSERT INTO `user` (nickname) VALUES ('우간다 큰고니35');
SET @review_seed_author_18 = LAST_INSERT_ID();

INSERT INTO `user` (nickname) VALUES ('세네갈 멋쟁이35');
SET @review_seed_author_19 = LAST_INSERT_ID();

INSERT INTO `user` (nickname) VALUES ('터키 풍조13');
SET @review_seed_author_20 = LAST_INSERT_ID();

INSERT INTO `user` (nickname) VALUES ('세네갈 산달125');
SET @review_seed_author_21 = LAST_INSERT_ID();

INSERT INTO `user` (nickname) VALUES ('몽골 노랑부리44');
SET @review_seed_author_22 = LAST_INSERT_ID();

INSERT INTO `user` (nickname) VALUES ('칠레 바다제비27');
SET @review_seed_author_23 = LAST_INSERT_ID();

INSERT INTO `user` (nickname) VALUES ('페루 큰부리새81');
SET @review_seed_author_24 = LAST_INSERT_ID();

INSERT INTO `user` (nickname) VALUES ('케냐 물총새19');
SET @review_seed_author_25 = LAST_INSERT_ID();

INSERT INTO `user` (nickname) VALUES ('볼리비아 콘야비15');
SET @review_seed_author_26 = LAST_INSERT_ID();

INSERT INTO `user` (nickname) VALUES ('레소토 바다수달5');
SET @review_seed_author_27 = LAST_INSERT_ID();

INSERT INTO `user` (nickname) VALUES ('포르투갈 물떼새2');
SET @review_seed_author_28 = LAST_INSERT_ID();

INSERT INTO `user` (nickname) VALUES ('기니 연어45');
SET @review_seed_author_29 = LAST_INSERT_ID();

INSERT INTO `user` (nickname) VALUES ('알바니아 되새35');
SET @review_seed_author_30 = LAST_INSERT_ID();

INSERT INTO `user` (nickname) VALUES ('모리타니 족제비45');
SET @review_seed_author_31 = LAST_INSERT_ID();

INSERT INTO `user` (nickname) VALUES ('바하마 설치렁이24');
SET @review_seed_author_32 = LAST_INSERT_ID();

INSERT INTO `user` (nickname) VALUES ('베냉 쇠왕부리235');
SET @review_seed_author_33 = LAST_INSERT_ID();

INSERT INTO `user` (nickname) VALUES ('투발루 꼬꼬리룩');
SET @review_seed_author_34 = LAST_INSERT_ID();

INSERT INTO `user` (nickname) VALUES ('태국 신천옹류135');
SET @review_seed_author_35 = LAST_INSERT_ID();

INSERT INTO `user` (nickname) VALUES ('오스트리아 다람쥐5');
SET @review_seed_author_36 = LAST_INSERT_ID();

INSERT INTO `user` (nickname) VALUES ('착한 흰꼬리매134');
SET @review_seed_author_37 = LAST_INSERT_ID();

INSERT INTO `user` (nickname) VALUES ('스웨덴 왕뱀류125');
SET @review_seed_author_38 = LAST_INSERT_ID();

INSERT INTO `user` (nickname) VALUES ('이라크 해리스매35');
SET @review_seed_author_39 = LAST_INSERT_ID();

INSERT INTO `user` (nickname) VALUES ('아금바리');
SET @review_seed_author_40 = LAST_INSERT_ID();

INSERT INTO `user` (nickname) VALUES ('미얀마 귀뚜라미35');
SET @review_seed_author_41 = LAST_INSERT_ID();

INSERT INTO `user` (nickname) VALUES ('이란 무당개구리35');
SET @review_seed_author_42 = LAST_INSERT_ID();

INSERT INTO `user` (nickname) VALUES ('폴란드 군대개미15');
SET @review_seed_author_43 = LAST_INSERT_ID();

INSERT INTO `user` (nickname) VALUES ('쿠바 굴뚝새45');
SET @review_seed_author_44 = LAST_INSERT_ID();

INSERT INTO `user` (nickname) VALUES ('북키프로스 악어45');
SET @review_seed_author_45 = LAST_INSERT_ID();

INSERT INTO `user` (nickname) VALUES ('몰타 돼지코자리45');
SET @review_seed_author_46 = LAST_INSERT_ID();

INSERT INTO `user` (nickname) VALUES ('페루 윤표1235');
SET @review_seed_author_47 = LAST_INSERT_ID();

INSERT INTO `user` (nickname) VALUES ('카메룬 깍도요45');
SET @review_seed_author_48 = LAST_INSERT_ID();

INSERT INTO `user` (nickname) VALUES ('우간다 도치1235');
SET @review_seed_author_49 = LAST_INSERT_ID();

INSERT INTO `user` (nickname) VALUES ('말라위 늪거북235');
SET @review_seed_author_50 = LAST_INSERT_ID();

INSERT INTO `user` (nickname) VALUES ('달구지67');
SET @review_seed_author_51 = LAST_INSERT_ID();

INSERT INTO `user` (nickname) VALUES ('조지아 나방135');
SET @review_seed_author_52 = LAST_INSERT_ID();

INSERT INTO `user` (nickname) VALUES ('페루 거위1235');
SET @review_seed_author_53 = LAST_INSERT_ID();

INSERT INTO `user` (nickname) VALUES ('사모아 늪영양123');
SET @review_seed_author_54 = LAST_INSERT_ID();

INSERT INTO `user` (nickname) VALUES ('벨기에 살모사3');
SET @review_seed_author_55 = LAST_INSERT_ID();

INSERT INTO `user` (nickname) VALUES ('체코 양진이35');
SET @review_seed_author_56 = LAST_INSERT_ID();

INSERT INTO `user` (nickname) VALUES ('아리쫑');
SET @review_seed_author_57 = LAST_INSERT_ID();

-- 04_리뷰 3행: 강남브랜드안과의원 강남점
INSERT INTO review (
    hospital_id, user_id, paid_price, rating, content, doctor_name,
    will_revisit, is_receipt_verified, is_modoodoc_visit, reservation_type, helpful_count, created_at
) VALUES (
    COALESCE(@review_seed_hospital_1, -1), @review_seed_author_1, NULL, 10, '지인 추천으로 모두닥에서 예약해서 왔고, 아직 수술 전이지만 다들 친절하시고 설명도 상세했습니다. 검사부터 수술까지 총 3시간 정도 소요됐으니 여유롭게 오시는 게 좋을 것 같고, 수술 전이지만 믿음이 가네요.', '정재승', FALSE, TRUE, TRUE, 'RESERVATION', NULL, '2024-08-06 00:00:00'
);
SET @review_seed_review_id = LAST_INSERT_ID();

INSERT INTO review_treatment (review_id, treatment_id) VALUES
(@review_seed_review_id, 8),
(@review_seed_review_id, 1);

-- 04_리뷰 4행: 강남브랜드안과의원 강남점
INSERT INTO review (
    hospital_id, user_id, paid_price, rating, content, doctor_name,
    will_revisit, is_receipt_verified, is_modoodoc_visit, reservation_type, helpful_count, created_at
) VALUES (
    COALESCE(@review_seed_hospital_1, -1), @review_seed_author_2, NULL, 9.8, '월요일에 대학생들 마지막 방학 주라 사람이 엄청 많고 대기가 길었지만, 친절한 직원분들이 여러 번 대기 안내해주시고 선생님들이 진짜 힘들고 지쳐 보이지만 응대할 때는 또 최선을 다해 한 사람 한 사람 대해주시는 게 좋았어요. 또 상담해주신 선생님도 너무 부드러운 톤으로 과하지 않게 설명도 자세히 해주셔서 부담스럽지 않고 만족합니다. 수술 후 얼마나 결과가 좋아질지 모르겠지만 떨리고 걱정됐는데 잘 다독여주셔서 긴장이 좀 완화됐어요. 수술 직후에는 너무 뿌옇고 사람 얼굴은 보이지만 뭔가 막이 있는 느낌, 도수가 안 맞는 렌즈를 끼고 있는 느낌이 들었어요. 저녁과 밤인데도 모든 불빛, 특히 네온사인이나 간판이 눈부셔서 선글라스를 끼고 다녔고 눈이 시려서 수시로 인공눈물을 넣었습니다. 수술 직후 3시간쯤부터 통증이 심할 수 있다고 안내받았는데 저는 통증은 하나도 없었고, 오히려 시린 증상과 눈부신 느낌이 수술 직후와 비교했을 때 많이 줄어서 좋았습니다. 안구건조가 심한 편이었는데 비교적 괜찮았습니다.', '정재승', TRUE, TRUE, TRUE, 'RESERVATION', NULL, '2025-04-09 00:00:00'
);
SET @review_seed_review_id = LAST_INSERT_ID();

INSERT INTO review_treatment (review_id, treatment_id) VALUES
(@review_seed_review_id, 8),
(@review_seed_review_id, 1);

-- 04_리뷰 5행: 강남브랜드안과의원 강남점
INSERT INTO review (
    hospital_id, user_id, paid_price, rating, content, doctor_name,
    will_revisit, is_receipt_verified, is_modoodoc_visit, reservation_type, helpful_count, created_at
) VALUES (
    COALESCE(@review_seed_hospital_1, -1), @review_seed_author_3, NULL, 10, '상담이 친절하시고 선택지가 많아서 좋아요! 친구랑 같이 왔는데 눈 상태가 심각해서 투데이 가능하다고 해서 친구는 출근 때문에 다음에 하기로 했지만, 저는 당일 검진/당일 수술하고 가요! 감사합니다!', '정재승', TRUE, TRUE, TRUE, 'RESERVATION', NULL, '2024-06-27 00:00:00'
);
SET @review_seed_review_id = LAST_INSERT_ID();

INSERT INTO review_treatment (review_id, treatment_id) VALUES
(@review_seed_review_id, 582),
(@review_seed_review_id, 8),
(@review_seed_review_id, 1);

-- 04_리뷰 6행: 강남브랜드안과의원 강남점
INSERT INTO review (
    hospital_id, user_id, paid_price, rating, content, doctor_name,
    will_revisit, is_receipt_verified, is_modoodoc_visit, reservation_type, helpful_count, created_at
) VALUES (
    COALESCE(@review_seed_hospital_1, -1), @review_seed_author_4, NULL, 10, '수술 전 검사부터 상담, 원장님과의 상담까지 내 눈이 어떻고 어떠한 시술과 수술을 해야 하는지, 왜 그렇게 해야 하는지 등 엄청 꼼꼼하고 체계적으로 해주십니다!! 군인 할인 이용해서 잘 받고 새 생명 얻은 기분으로 돌아갑니다!!', '정재승', TRUE, TRUE, TRUE, 'RESERVATION', NULL, '2025-08-16 00:00:00'
);
SET @review_seed_review_id = LAST_INSERT_ID();

INSERT INTO review_treatment (review_id, treatment_id) VALUES
(@review_seed_review_id, 1),
(@review_seed_review_id, 8);

-- 04_리뷰 7행: 강남브랜드안과의원 강남점
INSERT INTO review (
    hospital_id, user_id, paid_price, rating, content, doctor_name,
    will_revisit, is_receipt_verified, is_modoodoc_visit, reservation_type, helpful_count, created_at
) VALUES (
    COALESCE(@review_seed_hospital_1, -1), @review_seed_author_5, NULL, 10, '라식, 라섹, 스마일 종류가 너무 많아서 고민하던 와중에 지인의 추천으로 모두닥에서 예약하게 되어 방문했습니다. 데스크 분들도 친절하게 안내해주셨고 상담실장님도 이것저것 추천해주시면서 당일 수술까지 받았어요. 마취안약을 넣으니 아프고 그런 건 전혀 없었고, 인형을 안겨주셔서 생각보다 마음이 편했습니다. 대기시간이 조금 긴 편이지만 병원이 인기가 많으니 어쩔 수 없다고 생각합니다. (손님이 무척 많았어요.)', '정재승', TRUE, TRUE, TRUE, 'RESERVATION', NULL, '2024-12-03 00:00:00'
);
SET @review_seed_review_id = LAST_INSERT_ID();

INSERT INTO review_treatment (review_id, treatment_id) VALUES
(@review_seed_review_id, 8),
(@review_seed_review_id, 1);

-- 04_리뷰 8행: 강남브랜드안과의원 강남점
INSERT INTO review (
    hospital_id, user_id, paid_price, rating, content, doctor_name,
    will_revisit, is_receipt_verified, is_modoodoc_visit, reservation_type, helpful_count, created_at
) VALUES (
    COALESCE(@review_seed_hospital_1, -1), @review_seed_author_6, NULL, 10, '편하게 물어보실 수 있도록 친절하게 대해주셔서 좋았습니다. 검사를 받을 때 속눈썹이 길어서 문제가 됐지만 친절하게 면봉으로 교정해주시고, 상담과 진료 때에는 걱정할 만한 사항들을 미리 알려주셔서 안심하고 편하게 수술받을 수 있었습니다.', '정재승', TRUE, TRUE, TRUE, 'RESERVATION', NULL, '2025-02-11 00:00:00'
);
SET @review_seed_review_id = LAST_INSERT_ID();

INSERT INTO review_treatment (review_id, treatment_id) VALUES
(@review_seed_review_id, 8),
(@review_seed_review_id, 1);

-- 04_리뷰 9행: 강남브랜드안과의원 강남점
INSERT INTO review (
    hospital_id, user_id, paid_price, rating, content, doctor_name,
    will_revisit, is_receipt_verified, is_modoodoc_visit, reservation_type, helpful_count, created_at
) VALUES (
    COALESCE(@review_seed_hospital_1, -1), @review_seed_author_7, NULL, 9.8, '검진도 꼼꼼하게 해주시고 이전에 비해 시력이 많이 좋아졌습니다! 아직 일주일밖에 안 돼서 완전히 시력이 올라오진 않은 것 같지만 관리를 더 잘하고 시간이 더 지나면 더 좋아질 것 같아요ㅎㅎㅎㅎ 수술할 때도 설명을 잘해주셔서 마음 편히 진행했던 것 같습니다.', '정재승', FALSE, TRUE, TRUE, 'RESERVATION', NULL, '2024-10-05 00:00:00'
);
SET @review_seed_review_id = LAST_INSERT_ID();

INSERT INTO review_treatment (review_id, treatment_id) VALUES
(@review_seed_review_id, 8),
(@review_seed_review_id, 1);

-- 04_리뷰 10행: 강남브랜드안과의원 강남점
INSERT INTO review (
    hospital_id, user_id, paid_price, rating, content, doctor_name,
    will_revisit, is_receipt_verified, is_modoodoc_visit, reservation_type, helpful_count, created_at
) VALUES (
    COALESCE(@review_seed_hospital_1, -1), @review_seed_author_8, NULL, 10, '눈 수술이라 긴장되는 건 당연하지만 다들 친절하게 도와주시고, 수술한 지 지금 일주일 지났는데 시간이 지나면 지날수록 점점 만족 중입니다! 저처럼 안경 쓰는 게 불편하신 분들, 눈 안 좋은 분들한테 정말 많이 추천합니다.', '정재승', TRUE, TRUE, TRUE, 'RESERVATION', NULL, '2025-06-28 00:00:00'
);
SET @review_seed_review_id = LAST_INSERT_ID();

INSERT INTO review_treatment (review_id, treatment_id) VALUES
(@review_seed_review_id, 8),
(@review_seed_review_id, 1),
(@review_seed_review_id, 582);

-- 04_리뷰 11행: 강남브랜드안과의원 강남점
INSERT INTO review (
    hospital_id, user_id, paid_price, rating, content, doctor_name,
    will_revisit, is_receipt_verified, is_modoodoc_visit, reservation_type, helpful_count, created_at
) VALUES (
    COALESCE(@review_seed_hospital_1, -1), @review_seed_author_9, NULL, 10, '제 눈에 맞는 시술들을 추천해주셨고, 전 스마트라식이 하고 싶어서 추천해주신 시술로 변경하지는 않았어요. 당일 검사 후 수술 진행했는데 주말이라 대기가 조금 길었어요. 수술 진행할 때는 간호사, 의사 선생님들이 계속 안심되는 말을 해주셔서 마음 편하게 할 수 있었고, 수술 후에는 안구건조증 때문에 눈이 좀 많이 불편했고 회복이 더디다는 느낌이 들어서 건조증 치료를 병행하지 않은 게 좀 후회됐어요ㅠ 시력은 잘 교정돼서 만족합니다!', '정재승', TRUE, TRUE, TRUE, 'RESERVATION', NULL, '2024-09-21 00:00:00'
);
SET @review_seed_review_id = LAST_INSERT_ID();

INSERT INTO review_treatment (review_id, treatment_id) VALUES
(@review_seed_review_id, 1),
(@review_seed_review_id, 8);

-- 04_리뷰 12행: 강남브랜드안과의원 강남점
INSERT INTO review (
    hospital_id, user_id, paid_price, rating, content, doctor_name,
    will_revisit, is_receipt_verified, is_modoodoc_visit, reservation_type, helpful_count, created_at
) VALUES (
    COALESCE(@review_seed_hospital_1, -1), @review_seed_author_10, NULL, 10, '라식하려고 몇 군데 예약하고 검사하며 다녀봤는데 제일 마음에 들었던 병원이에요. 예약 접수부터 검사, 상담, 수술 당일, 수술 이후까지 꼼꼼하고 친절하게 봐주셔서 너무 만족스러웠어요. 수술 결과도 너무 만족스럽고 지인이 한다고 하면 무조건 여기 추천할 거예요! 수술 당일에 쓰던 안경에 도수 없는 UV, 블루라이트 차단 렌즈로 맞춰주십니다. 참고하세요!', '이성훈', TRUE, TRUE, TRUE, 'RESERVATION', NULL, '2025-01-17 00:00:00'
);
SET @review_seed_review_id = LAST_INSERT_ID();

INSERT INTO review_treatment (review_id, treatment_id) VALUES
(@review_seed_review_id, 8),
(@review_seed_review_id, 1);

-- 04_리뷰 14행: 강남브랜드안과의원 강남점
INSERT INTO review (
    hospital_id, user_id, paid_price, rating, content, doctor_name,
    will_revisit, is_receipt_verified, is_modoodoc_visit, reservation_type, helpful_count, created_at
) VALUES (
    COALESCE(@review_seed_hospital_1, -1), @review_seed_author_11, NULL, 10, '늦은 나이인줄 모르고 조금 편해지고 싶어서 라식 등을 알아보던 중 스마일라식을 알게되어 집에서 가까운 시력교정전문 안과에 방문했는데 모든 수술이 가능하다고 해놓고 제일 비싼 스마일 프로만 추천을 하고 가격도 그것만 알려주고 해서 다른 안과를 한 군데 더 방문하려고 모두닥에서 알아보던 중에 리뷰도 많고 좋고, 최신 장비에 여러가지로 마음에 들어 예약 후 방문하였습니다. 대기를 조금 하긴 했지만 검안사님께서 꼼꼼하게 검사를 해주셨고 바로 상담사가 아닌 의사선생님께서 진료를 봐주신 점도 좋았어요! 의사선생님께서 저에게 안전한 수술로 라식을 추천해 주셨고, 노안도 생각해서 모노비전으로 진행하게 되었습니다. 그 후 상담직원분과 가격 상담 후 당일 수술을 진행하게 되었고 순식간에 수술이 끝나고 몇시간 이물감 정도 빼고는 눈도 잘 보이고 신세계를 보았습니다. 2일이 지난 지금도 너무 잘 보여요~', '한승수', TRUE, TRUE, TRUE, 'RESERVATION', NULL, '2025-04-29 00:00:00'
);
SET @review_seed_review_id = LAST_INSERT_ID();

INSERT INTO review_treatment (review_id, treatment_id) VALUES
(@review_seed_review_id, 1),
(@review_seed_review_id, 578);

-- 04_리뷰 15행: 강남브랜드안과의원 강남점
INSERT INTO review (
    hospital_id, user_id, paid_price, rating, content, doctor_name,
    will_revisit, is_receipt_verified, is_modoodoc_visit, reservation_type, helpful_count, created_at
) VALUES (
    COALESCE(@review_seed_hospital_1, -1), @review_seed_author_12, NULL, 10, '티비볼때나 운전할때 안경쓰는정도로 생활했어서 불편하지않았는데 어느순간 나이가들고 눈이 점점 안좋아지고 안경도 불편하고해서 라식알아보던중 노안이와서 짝눈으로 교정하는 모노라식으로 결정했어요 첫날 수술하고 바로 일상생활가능할정도로 별 이상이 없었고 짝눈이라 걱정했는데 너무 만족스럽네요 추가로 안구건조시술도 추가로 했는데 아주 만족스럽네요', '이성훈', TRUE, TRUE, TRUE, 'RESERVATION', NULL, '2024-11-15 00:00:00'
);
SET @review_seed_review_id = LAST_INSERT_ID();

INSERT INTO review_treatment (review_id, treatment_id) VALUES
(@review_seed_review_id, 578),
(@review_seed_review_id, 581);

-- 04_리뷰 16행: 강남브랜드안과의원 강남점
INSERT INTO review (
    hospital_id, user_id, paid_price, rating, content, doctor_name,
    will_revisit, is_receipt_verified, is_modoodoc_visit, reservation_type, helpful_count, created_at
) VALUES (
    COALESCE(@review_seed_hospital_1, -1), @review_seed_author_13, NULL, 9.8, '친절한 직원과 빠르게 진행되는 진료로 인해 편하게 수술 받을 수 있었습니다. 가족의 수술 이후 믿고 진행하였는데 역시 옳은 선택이었습니다. 다만, 노인라식으로 통증은 다소 있었습니다.', '정재승', FALSE, TRUE, FALSE, NULL, NULL, '2026-05-08 00:00:00'
);
SET @review_seed_review_id = LAST_INSERT_ID();

INSERT INTO review_treatment (review_id, treatment_id) VALUES
(@review_seed_review_id, 579);

-- 04_리뷰 18행: 클리어서울안과의원
INSERT INTO review (
    hospital_id, user_id, paid_price, rating, content, doctor_name,
    will_revisit, is_receipt_verified, is_modoodoc_visit, reservation_type, helpful_count, created_at
) VALUES (
    COALESCE(@review_seed_hospital_2, -1), @review_seed_author_14, NULL, 10, '전역을 앞두고 스마일라식을 하고 싶어 방문하게 되었는데 검진을 받는 과정부터 상담과 사소한 부분까지 친절하게 안내해 주신 덕분에 큰 어려움 없이 진행할 수 있었습니다. 병원의 편안한 분위기 속에서 직원분들께서 모두 친절하게 대해주셔서 감사했고 검사를 통해 제 눈 상태에 대해 정확하게 알 수 있어서 좋았습니다.', '임태형', TRUE, TRUE, TRUE, 'RESERVATION', NULL, '2024-07-30 00:00:00'
);
SET @review_seed_review_id = LAST_INSERT_ID();

INSERT INTO review_treatment (review_id, treatment_id) VALUES
(@review_seed_review_id, 2),
(@review_seed_review_id, 1);

-- 04_리뷰 19행: 클리어서울안과의원
INSERT INTO review (
    hospital_id, user_id, paid_price, rating, content, doctor_name,
    will_revisit, is_receipt_verified, is_modoodoc_visit, reservation_type, helpful_count, created_at
) VALUES (
    COALESCE(@review_seed_hospital_2, -1), @review_seed_author_15, NULL, 10, '유튜브에서 광고를 보고 라식을 하게 되었고, 상담도 친절하게 해주시고, 수술 들어가기 전에 주의사항이랑 수술 과정을 자세히 알려주셨습니다. 회복도 빠르게 된다고 하니 마음이 놓입니다.', '김재형', TRUE, TRUE, TRUE, 'RESERVATION', NULL, '2025-05-24 00:00:00'
);
SET @review_seed_review_id = LAST_INSERT_ID();

INSERT INTO review_treatment (review_id, treatment_id) VALUES
(@review_seed_review_id, 1),
(@review_seed_review_id, 2);

-- 04_리뷰 20행: 클리어서울안과의원
INSERT INTO review (
    hospital_id, user_id, paid_price, rating, content, doctor_name,
    will_revisit, is_receipt_verified, is_modoodoc_visit, reservation_type, helpful_count, created_at
) VALUES (
    COALESCE(@review_seed_hospital_2, -1), @review_seed_author_16, NULL, 10, '시력교정술 상담을 위해 방문했는데 검사 과정부터 전체적으로 꼼꼼하게 진행해주셔서 좋았습니다. 검사 항목도 다양했고 각 검사 결과와 현재 눈 상태에 대해서도 이해하기 쉽게 하나하나 설명해주셨습니다. 궁금한 부분을 질문했을 때도 충분히 답변해주셔서 수술에 대한 걱정이 많이 줄었습니다. 무조건 수술을 권하기보다는 제 눈 상태에 맞춰 가능한 방법과 주의사항까지 자세하게 안내해주신 점이 만족스러웠습니다. 직원분들도 친절하고 편안한 분위기라 부담 없이 검사와 상담을 받을 수 있었습니다.', '김재형', TRUE, TRUE, TRUE, 'RESERVATION', NULL, '2024-11-08 00:00:00'
);
SET @review_seed_review_id = LAST_INSERT_ID();

INSERT INTO review_treatment (review_id, treatment_id) VALUES
(@review_seed_review_id, 1),
(@review_seed_review_id, 2);

-- 04_리뷰 21행: 클리어서울안과의원
INSERT INTO review (
    hospital_id, user_id, paid_price, rating, content, doctor_name,
    will_revisit, is_receipt_verified, is_modoodoc_visit, reservation_type, helpful_count, created_at
) VALUES (
    COALESCE(@review_seed_hospital_2, -1), @review_seed_author_17, NULL, 10, '스마일라식 수술 전 상담부터 수술까지 받고 왔어요. 상담해 주시는 분들이 친절하고 설명도 꼼꼼해서 궁금한 점도 편하게 물어볼 수 있었습니다. 상담 과정에서 수술이나 불필요한 추가 시술을 강요하는 느낌이 전혀 없어서 부담 없이 상담받을 수 있었고, 제 눈 상태에 맞춰 차분하게 안내해 주셔서 믿음이 갔어요. 수술도 잘 받고 나왔습니다! 선생님께서 수술 전부터 수술하는 동안까지 친절하게 대해주시고 긴장하지 않도록 잘 설명해 주셔서 안심하고 받을 수 있었어요. 처음 상담부터 수술까지 만족스러웠습니다.', '김재형', FALSE, TRUE, TRUE, NULL, NULL, '2025-07-21 00:00:00'
);
SET @review_seed_review_id = LAST_INSERT_ID();

INSERT INTO review_treatment (review_id, treatment_id) VALUES
(@review_seed_review_id, 1),
(@review_seed_review_id, 2);

-- 04_리뷰 22행: 클리어서울안과의원
INSERT INTO review (
    hospital_id, user_id, paid_price, rating, content, doctor_name,
    will_revisit, is_receipt_verified, is_modoodoc_visit, reservation_type, helpful_count, created_at
) VALUES (
    COALESCE(@review_seed_hospital_2, -1), @review_seed_author_18, NULL, 8.3, '상담해주시는 분도 친절하고 자세하게 설명해주시고, 원장님께서도 질문에 대해 친절하게 설명해주셔서 너무 만족했습니다ㅎㅎ 수술도 잘 끝나서 빨리 시력 회복했으면 좋겠어요!!!!ㅠㅠ', '김재형', FALSE, TRUE, TRUE, 'RESERVATION', NULL, '2024-09-17 00:00:00'
);
SET @review_seed_review_id = LAST_INSERT_ID();

INSERT INTO review_treatment (review_id, treatment_id) VALUES
(@review_seed_review_id, 1),
(@review_seed_review_id, 2);

-- 04_리뷰 23행: 클리어서울안과의원
INSERT INTO review (
    hospital_id, user_id, paid_price, rating, content, doctor_name,
    will_revisit, is_receipt_verified, is_modoodoc_visit, reservation_type, helpful_count, created_at
) VALUES (
    COALESCE(@review_seed_hospital_2, -1), @review_seed_author_19, NULL, 10, '검사를 정말 꼼꼼히 해주셔서 좋았어요. 10가지가 넘는 검사를 해주시고 또 수술 전에 시력과 각막 상태를 다시 한번 체크해주시는 게 좋았어요. 직원분들이 정말 친절하시고 눈높이에 맞추어 설명해주셔서 이해가 쉬웠어요. 다음에 안과 오는 일이 있으면 여기로 오고 싶어요.', '임태형', TRUE, TRUE, TRUE, 'RESERVATION', NULL, '2025-01-30 00:00:00'
);
SET @review_seed_review_id = LAST_INSERT_ID();

INSERT INTO review_treatment (review_id, treatment_id) VALUES
(@review_seed_review_id, 1),
(@review_seed_review_id, 2);

-- 04_리뷰 24행: 클리어서울안과의원
INSERT INTO review (
    hospital_id, user_id, paid_price, rating, content, doctor_name,
    will_revisit, is_receipt_verified, is_modoodoc_visit, reservation_type, helpful_count, created_at
) VALUES (
    COALESCE(@review_seed_hospital_2, -1), @review_seed_author_20, NULL, 9.3, '[좋은점] 상담 > 검사 > 수술까지의 프로세스가 깔끔합니다. 의료진, 직원분들 모두 친절하십니다. 처음 견적과 동일합니다. 시력이 개선되었습니다. (안경착용 1.0 > 1.2) [아쉬운점] 50일이 경과하였는데 뻑뻑함이 심합니다. 가까운 사물을 보려면 눈에 많은 힘을 줘야합니다. 어두운 곳에 들어가면 뿌옇게 됩니다.', '임태형', FALSE, TRUE, TRUE, 'RESERVATION', NULL, '2024-12-22 00:00:00'
);
SET @review_seed_review_id = LAST_INSERT_ID();

INSERT INTO review_treatment (review_id, treatment_id) VALUES
(@review_seed_review_id, 1),
(@review_seed_review_id, 2);

-- 04_리뷰 25행: 클리어서울안과의원
INSERT INTO review (
    hospital_id, user_id, paid_price, rating, content, doctor_name,
    will_revisit, is_receipt_verified, is_modoodoc_visit, reservation_type, helpful_count, created_at
) VALUES (
    COALESCE(@review_seed_hospital_2, -1), @review_seed_author_21, NULL, 10, '안경 새로 맞출지 고민하다가 시력교정술을 질러버렸어요ㅋㅋ 검진 받고 모든 시술 다 가능은 하다고 하셔서 스마일라식 선택했습니다. 렌즈도 안 껴봐서 수술할 때 좀 무섭긴 했는데 잘 끝나고 시력회복도 빨랐습니다! 자가혈청안약을 가격 내 포함해주는 게 굉장히 큰 메리트인 것 같아요. 후회는 안합니다!', '김재형', TRUE, TRUE, TRUE, 'RESERVATION', NULL, '2025-05-11 00:00:00'
);
SET @review_seed_review_id = LAST_INSERT_ID();

INSERT INTO review_treatment (review_id, treatment_id) VALUES
(@review_seed_review_id, 2),
(@review_seed_review_id, 1);

-- 04_리뷰 26행: 클리어서울안과의원
INSERT INTO review (
    hospital_id, user_id, paid_price, rating, content, doctor_name,
    will_revisit, is_receipt_verified, is_modoodoc_visit, reservation_type, helpful_count, created_at
) VALUES (
    COALESCE(@review_seed_hospital_2, -1), @review_seed_author_22, NULL, 10, '사실 시력교정술과 관련하여 사전에 온라인으로 조사를 하고 갔음에도 모르는 정보들이 정말 많았고 이를 친절하게 세부적으로 설명해주시고 궁금한 점도 친절하게 답변주셔서 정말 감사했습니다. 이후 스마일라식 수술을 받았는데 수술이 정말 일찍 끝났고 사후 관리도 친절히 해주시고 후유증 없이 좋은 결과를 얻을 수 있어 너무 만족스러웠습니다!', '이지영', TRUE, TRUE, TRUE, 'RESERVATION', NULL, '2024-08-06 00:00:00'
);
SET @review_seed_review_id = LAST_INSERT_ID();

INSERT INTO review_treatment (review_id, treatment_id) VALUES
(@review_seed_review_id, 1),
(@review_seed_review_id, 2),
(@review_seed_review_id, 582);

-- 04_리뷰 27행: 클리어서울안과의원
INSERT INTO review (
    hospital_id, user_id, paid_price, rating, content, doctor_name,
    will_revisit, is_receipt_verified, is_modoodoc_visit, reservation_type, helpful_count, created_at
) VALUES (
    COALESCE(@review_seed_hospital_2, -1), @review_seed_author_23, NULL, 10, '친절하고 체계적인 상담과 진료! 만족스러운 병원입니다! 모두에게 추천하고 저 또한 재진료 의사 10000%입니다. 지인에게 추천하고싶고 부작용 또한 전혀없어서 너무 맘에듭니다. 추천합니다~', '임태형', FALSE, TRUE, TRUE, 'RESERVATION', NULL, '2025-02-19 00:00:00'
);
SET @review_seed_review_id = LAST_INSERT_ID();

INSERT INTO review_treatment (review_id, treatment_id) VALUES
(@review_seed_review_id, 1),
(@review_seed_review_id, 2);

-- 04_리뷰 28행: 클리어서울안과의원
INSERT INTO review (
    hospital_id, user_id, paid_price, rating, content, doctor_name,
    will_revisit, is_receipt_verified, is_modoodoc_visit, reservation_type, helpful_count, created_at
) VALUES (
    COALESCE(@review_seed_hospital_2, -1), @review_seed_author_24, NULL, 10, '스마일 라식을 하려고 알아보던 중 카페도 찾아보고 이것저것 알아봤으나, 모두닥을 통해 알아보는 게 가장 좋을 것 같았어요. 그 후 안과를 선택하여 당일 검사 및 수술을 받았는데 수술 후 몇 시간 동안은 눈이 매우 시리고 눈물이 났으나 낮잠 한 번 자고 일어나니 많이 좋아졌네요. 눈은 렌즈를 끼고 보는 것처럼 보이는 것 같고 아직 좀 흐린 감은 있습니다.', '임태형', TRUE, TRUE, TRUE, 'RESERVATION', NULL, '2024-10-28 00:00:00'
);
SET @review_seed_review_id = LAST_INSERT_ID();

INSERT INTO review_treatment (review_id, treatment_id) VALUES
(@review_seed_review_id, 582),
(@review_seed_review_id, 2),
(@review_seed_review_id, 1);

-- 04_리뷰 29행: 클리어서울안과의원
INSERT INTO review (
    hospital_id, user_id, paid_price, rating, content, doctor_name,
    will_revisit, is_receipt_verified, is_modoodoc_visit, reservation_type, helpful_count, created_at
) VALUES (
    COALESCE(@review_seed_hospital_2, -1), @review_seed_author_25, NULL, 10, '당일 검진받고 당일 스마일라식도 결정하고 진행했는데 데스크 선생님, 검진, 상담선생님, 간호사님 모두 친절하셨어요! 물론 당연히 수술해주신 의사선생님도요 :) 특히 대기가 짧고 청결하게 검진이 이루어졌어요! 당일이라 좀 더 결과 지켜봐야겠지만 모두 만족스러웠습니다!', '이지영', TRUE, TRUE, TRUE, 'RESERVATION', NULL, '2025-06-03 00:00:00'
);
SET @review_seed_review_id = LAST_INSERT_ID();

INSERT INTO review_treatment (review_id, treatment_id) VALUES
(@review_seed_review_id, 2),
(@review_seed_review_id, 1);

-- 04_리뷰 31행: 클리어서울안과의원
INSERT INTO review (
    hospital_id, user_id, paid_price, rating, content, doctor_name,
    will_revisit, is_receipt_verified, is_modoodoc_visit, reservation_type, helpful_count, created_at
) VALUES (
    COALESCE(@review_seed_hospital_2, -1), @review_seed_author_26, NULL, 9.5, '이벤트가 보고 가서 검안 후 당일 수술했습니다. 나이가 40세라 노안라식으로 해야 한다고 해서 이벤트 가격보다 비싸게 비용을 지불했네요. 수술받은 지 한 달 좀 안 됐는데 지금 시력도 좋고 만족합니다.', '임태형', TRUE, TRUE, TRUE, 'RESERVATION', NULL, '2025-04-16 00:00:00'
);
SET @review_seed_review_id = LAST_INSERT_ID();

INSERT INTO review_treatment (review_id, treatment_id) VALUES
(@review_seed_review_id, 580);

-- 04_리뷰 32행: 클리어서울안과의원
INSERT INTO review (
    hospital_id, user_id, paid_price, rating, content, doctor_name,
    will_revisit, is_receipt_verified, is_modoodoc_visit, reservation_type, helpful_count, created_at
) VALUES (
    COALESCE(@review_seed_hospital_2, -1), @review_seed_author_27, 1590000, 10, '각막이 얇은 편이라 수술이 진행이 안 될 수도 있었는데 깎는 두께를 최소화해서 진행해주셔서 다행이었습니다. 40대 초반이라 노안라섹으로 선택했습니다. 사전 검사 과정이 많았지만 눈 상태에 대한 정확한 분석을 위한 과정이라 생각되어 믿음이 갔습니다. 병원비 결제 후 수술 전 대기하는 시간이 살짝 떨리고 긴장되긴 하더군요. 임선생님의 차분한 목소리 덕에 수술대에 누웠을 때 안심이 되었고요. 레이저가 제 각막을 태울 때 단백질 타는 향이 코에 살짝 느껴진 기억이 납니다. 레이저 및 브러시 처리 후 각막강화술 약품을 눈에 도포한 뒤 보호렌즈를 씌우고 수술이 끝났습니다. 집에 온 뒤에도 예상보다 통증은 심하지 않았고요. 선명한 시력 회복은 3주 정도 걸린다고 하네요. 주기적인 검진 충실히 받고 새 눈으로 살게 될 날을 기다려봅니다.', '임태형', TRUE, TRUE, TRUE, 'RESERVATION', NULL, '2024-11-02 00:00:00'
);
SET @review_seed_review_id = LAST_INSERT_ID();

INSERT INTO review_treatment (review_id, treatment_id) VALUES
(@review_seed_review_id, 580);

-- 04_리뷰 33행: 클리어서울안과의원
INSERT INTO review (
    hospital_id, user_id, paid_price, rating, content, doctor_name,
    will_revisit, is_receipt_verified, is_modoodoc_visit, reservation_type, helpful_count, created_at
) VALUES (
    COALESCE(@review_seed_hospital_2, -1), @review_seed_author_28, NULL, 10, '금요일 오후에 방문했는데 사람이 많았습니다. 저는 40대 후반으로 근시로 30여 년 안경을 썼고 2~3년 전부터 노안으로 인한 불편함이 있어 검진을 일단 받았습니다. 검진 후 상담원 분이 검사 결과를 설명해주고 간단한 검사 결과지도 주셔서 좋았습니다. 이후 노안라식 후 예상 시력에 대한 시뮬레이션을 한 후 수술을 결정했습니다. 원장님도 친절하고 세심하게 수술에 대해 설명해주셨고 다른 모병원과 달리 백내장수술을 권유하지도 않았습니다. 노안라식의 장단점을 이해할 수 있게 설명해주셨는데 아직 노안이 진행 중이기 때문에 몇 년 후에 노안으로 다시 불편해질 수 있다는 점, 30cm 이내의 초근거리는 수술 전보다 잘 안 보이지만 30cm부터 중간 원거리 시력은 안경 없이 생활 가능하다고 합니다. 수술은 잘됐고 수술 2일 차 현재 근거리는 30cm 정도 떨어져야 해서 적응이 필요한데 대신 안경 없이 TV가 보이는 게 넘 신기합니다.', '임태형', TRUE, TRUE, TRUE, 'RESERVATION', NULL, '2024-08-05 00:00:00'
);
SET @review_seed_review_id = LAST_INSERT_ID();

INSERT INTO review_treatment (review_id, treatment_id) VALUES
(@review_seed_review_id, 580),
(@review_seed_review_id, 13);

-- 04_리뷰 35행: 강남조은눈안과의원
INSERT INTO review (
    hospital_id, user_id, paid_price, rating, content, doctor_name,
    will_revisit, is_receipt_verified, is_modoodoc_visit, reservation_type, helpful_count, created_at
) VALUES (
    COALESCE(@review_seed_hospital_3, -1), @review_seed_author_29, NULL, 10, '스마일라식 진행한 다음날 리뷰작성합니다. 매우 친절하셔서 편안한 마음으로 수술 진행했습니다. 검진 후 가능한 수술 설명을 해주시는데 자세히 알려주셔서 좋았습니다. 스마일라식 진행했는데 수술한 직후 2, 3시간까지 약간 뿌옇게 보였지만 그 이후로는 잘 보이고 아픈 것도 처음에 양파 썰 때의 눈 매운 정도만 있었지만 금방 사라졌습니다. 지금 시력 1.0 잘 나옵니다. 수술 만족도는 매우 높습니다.', '김준식', TRUE, TRUE, TRUE, 'RESERVATION', NULL, '2026-09-30 00:00:00'
);
SET @review_seed_review_id = LAST_INSERT_ID();

INSERT INTO review_treatment (review_id, treatment_id) VALUES
(@review_seed_review_id, 2),
(@review_seed_review_id, 1);

-- 04_리뷰 36행: 강남조은눈안과의원
INSERT INTO review (
    hospital_id, user_id, paid_price, rating, content, doctor_name,
    will_revisit, is_receipt_verified, is_modoodoc_visit, reservation_type, helpful_count, created_at
) VALUES (
    COALESCE(@review_seed_hospital_3, -1), @review_seed_author_30, NULL, 10, '지인분의 추천으로 스마일 라식 생각하고 방문했습니다. 병원 내부도 깔끔하고 검사도 여러가지 잘 봐주시고 설명도 잘 해주셔서 안심하고 수술할 수 있었습니다. 수술도 잘 끝난 것 같아서 앞으로의 시력이 기대가 됩니다!', '김준현', TRUE, TRUE, TRUE, 'RESERVATION', NULL, '2026-09-30 00:00:00'
);
SET @review_seed_review_id = LAST_INSERT_ID();

INSERT INTO review_treatment (review_id, treatment_id) VALUES
(@review_seed_review_id, 1),
(@review_seed_review_id, 2);

-- 04_리뷰 37행: 강남조은눈안과의원
INSERT INTO review (
    hospital_id, user_id, paid_price, rating, content, doctor_name,
    will_revisit, is_receipt_verified, is_modoodoc_visit, reservation_type, helpful_count, created_at
) VALUES (
    COALESCE(@review_seed_hospital_3, -1), @review_seed_author_31, NULL, 9.3, '혼자 버스 타고 가서 당일 검진, 수술까지 하고 왔습니다. 난시는 심하진 않지만 시축 잡아 주는 것도 그렇고 수술 중에 고정해 놓은 게 빠지면 라섹으로 바뀔 수 있다고 해서 스마일프로랑 스마일라식이랑 고민을 했는데 원장님 상담 후에 스마일라식으로 결정을 했습니다. 라식 하다 움직여서 라섹 하는 경우가 몇백명 중에 한명 꼴이라고 이야기 해주셔서 안심했고, 시축도 안경을 썼을 때 어떻게 보면 안 보이는 부분이 있지 않냐 시력에는 문제가 없다고 해주셔서 각막강화술만 추가했습니다. 고도근시이고 각막이 얇아서 추가했어요. 수술은 5분도 안 걸린 것 같고 그동안 무서워서 못하다 용기내서 했는데 너무 간단하고 나와서 바로 보이고 이 좋은 걸 왜 이제 했나 모르겠네요ㅠ 검사, 수술까지 3-4시간 걸린다고 했는데 2시간 반 걸렸습니다.', '김준식', TRUE, TRUE, TRUE, NULL, NULL, '2026-09-24 00:00:00'
);
SET @review_seed_review_id = LAST_INSERT_ID();

INSERT INTO review_treatment (review_id, treatment_id) VALUES
(@review_seed_review_id, 1),
(@review_seed_review_id, 2);

-- 04_리뷰 38행: 강남조은눈안과의원
INSERT INTO review (
    hospital_id, user_id, paid_price, rating, content, doctor_name,
    will_revisit, is_receipt_verified, is_modoodoc_visit, reservation_type, helpful_count, created_at
) VALUES (
    COALESCE(@review_seed_hospital_3, -1), @review_seed_author_32, NULL, 10, '데스크 직원분들, 검안사님, 상담사님 다들 친절하시고 잘 안내해주십니다. 검안받고 바로 수술예약해서 수술 받았고, 현재는 그뒤로 2주정도 됐는데 경과좋고 개인적으로 부작용도 거의 없어서 만족스럽네요.', '김준식', FALSE, TRUE, TRUE, 'RESERVATION', NULL, '2026-09-16 00:00:00'
);
SET @review_seed_review_id = LAST_INSERT_ID();

INSERT INTO review_treatment (review_id, treatment_id) VALUES
(@review_seed_review_id, 2),
(@review_seed_review_id, 1);

-- 04_리뷰 39행: 강남조은눈안과의원
INSERT INTO review (
    hospital_id, user_id, paid_price, rating, content, doctor_name,
    will_revisit, is_receipt_verified, is_modoodoc_visit, reservation_type, helpful_count, created_at
) VALUES (
    COALESCE(@review_seed_hospital_3, -1), @review_seed_author_33, NULL, 10, '부작용 걱정이 많았는데 수술 직후 통증이나 건조함도 거의 없고 다음 날 바로 일상적인 시력이 확보됐습니다. 회복 기간이 짧아서 출근이나 일상 복귀에도 전혀 차질이 없었네요. 결과가 기대 이상입니다. 특히 검사할 때 제 눈의 정확한 상태나 정밀 수치, 특성들을 아주 세심하게 설명해 주셔서 제 눈에 대해 잘 알게 된 뜻깊은 기회였습니다. 내 눈을 확실히 이해하고 수술을 진행하니 더 안심이 되었습니다. 정말 만족스럽습니다.', '김준식', TRUE, TRUE, TRUE, 'RESERVATION', NULL, '2026-09-16 00:00:00'
);
SET @review_seed_review_id = LAST_INSERT_ID();

INSERT INTO review_treatment (review_id, treatment_id) VALUES
(@review_seed_review_id, 2),
(@review_seed_review_id, 1);

-- 04_리뷰 40행: 강남조은눈안과의원
INSERT INTO review (
    hospital_id, user_id, paid_price, rating, content, doctor_name,
    will_revisit, is_receipt_verified, is_modoodoc_visit, reservation_type, helpful_count, created_at
) VALUES (
    COALESCE(@review_seed_hospital_3, -1), @review_seed_author_34, NULL, 10, '시력교정술을 알아보면서 적절한 수술방법을 위한 검진을 받아봤는데, 생각보다 검사가 꼼꼼하게 진행돼서 좋았습니다. 단순히 시력만 확인하는 것이 아니라 각막, 두께, 안압, 동공 크기 등 여러 가지 검사를 단계별로 진행했고, 검사 결과를 바탕으로 어떤 수술이 가능한지 자세히 설명해 주셨습니다. 검사 종류가 많아서 시간이 조금 걸리긴 했지만 직원분이 검사마다 친절하게 안내해 주셔서 크게 불편하지 않았습니다. 상담할 때도 무조건 특정 수술을 권하기보다는 제 눈 상태에 맞는 방법과 각각의 장단점을 설명해 주셔서 신뢰가 갔습니다. 수술을 고민하고 있다면 우선 검진부터 받아보고 본인 눈 상태를 정확히 확인해 보는 게 좋을 것 같습니다. 전반적으로 체계적으로 친절한 검진이어서 만족스러웠습니다. 비용은 수술 비용의 일부를 선결제해서 실제 검진 비용은 없습니다.', '김준식', TRUE, TRUE, TRUE, 'CONSULTATION', NULL, '2026-09-16 00:00:00'
);
SET @review_seed_review_id = LAST_INSERT_ID();

INSERT INTO review_treatment (review_id, treatment_id) VALUES
(@review_seed_review_id, 1),
(@review_seed_review_id, 2);

-- 04_리뷰 41행: 강남조은눈안과의원
INSERT INTO review (
    hospital_id, user_id, paid_price, rating, content, doctor_name,
    will_revisit, is_receipt_verified, is_modoodoc_visit, reservation_type, helpful_count, created_at
) VALUES (
    COALESCE(@review_seed_hospital_3, -1), @review_seed_author_35, NULL, 10, '병원이 너무 많아 이곳저곳 상담을 받다 강남조은눈안과가 제일 검사가 꼼꼼하시고 설명도 잘해주셔서 수술을 하기로 결정하였습니다. 사실 김준식 원장님 인상이 너무 동안이셔서 경험이 적지 않으실까 아주 살짝 걱정도 했었는데요..ㅎㅎ 걱정이 무색하게 시술하는 내내 세심하게 피드백도 주시고 ''몇 초 남았습니다'' 하고 버텨야 하는 시간을 계속 말씀해주셔서 긴장을 풀고 편안하게 시술을 잘 받을 수 있었습니다. 강남에 안과가 정말 많은데 고민 중이시라면 강남조은눈안과 강력 추천드립니다!', '김준식', TRUE, TRUE, TRUE, 'RESERVATION', NULL, '2026-07-29 00:00:00'
);
SET @review_seed_review_id = LAST_INSERT_ID();

INSERT INTO review_treatment (review_id, treatment_id) VALUES
(@review_seed_review_id, 1),
(@review_seed_review_id, 582),
(@review_seed_review_id, 2);

-- 04_리뷰 42행: 강남조은눈안과의원
INSERT INTO review (
    hospital_id, user_id, paid_price, rating, content, doctor_name,
    will_revisit, is_receipt_verified, is_modoodoc_visit, reservation_type, helpful_count, created_at
) VALUES (
    COALESCE(@review_seed_hospital_3, -1), @review_seed_author_36, NULL, 9, '직원분들이 전반적으로 친절하십니다. 의사선생님께서 눈 검사 결과로 어떤 시력교정술이 적합한지 정확하게 전달해주셨습니다. 병원은 청결하고 기다리는 동안 편했어요. 검진 이후 다음날 수술 예약해서 진행했습니다. 수술 전날 검진 간단하게 다시 하고, 수술 진행했는데 금방 끝났습니다. 수술하고 바로 시력은 좋아졌고 개인적으로 통증은 많이 없었습니다. 흐린 감이 있었는데 일주일 정도 지나니 좋아졌습니다.', '김준식', TRUE, TRUE, TRUE, 'RESERVATION', NULL, '2026-07-16 00:00:00'
);
SET @review_seed_review_id = LAST_INSERT_ID();

INSERT INTO review_treatment (review_id, treatment_id) VALUES
(@review_seed_review_id, 2),
(@review_seed_review_id, 1);

-- 04_리뷰 43행: 강남조은눈안과의원
INSERT INTO review (
    hospital_id, user_id, paid_price, rating, content, doctor_name,
    will_revisit, is_receipt_verified, is_modoodoc_visit, reservation_type, helpful_count, created_at
) VALUES (
    COALESCE(@review_seed_hospital_3, -1), @review_seed_author_37, NULL, 9, '라섹으로 알아보고 다른병원도 검사 다녀왔었는데 검사 방법에 대해 자세히 설명해주시고 꼭 필요한 것 외에는 권유를 하지 않아 영업 하는 느낌이 없었습니다. 수술 또한 잘 진행되었고 첫날은 한쪽 눈은 통증이 좀 있었지만 다음날 부턴 괜찮았고 모두닥 후기 및 실제 리뷰들도 괜찮았어서 좋았어요. 또한 오랜시간 그 자리에 있었기 때문에 더욱 믿음직스러웠습니다.', '김준식', TRUE, TRUE, TRUE, 'CONSULTATION', NULL, '2026-06-12 00:00:00'
);
SET @review_seed_review_id = LAST_INSERT_ID();

INSERT INTO review_treatment (review_id, treatment_id) VALUES
(@review_seed_review_id, 2),
(@review_seed_review_id, 1);

-- 04_리뷰 44행: 강남조은눈안과의원
INSERT INTO review (
    hospital_id, user_id, paid_price, rating, content, doctor_name,
    will_revisit, is_receipt_verified, is_modoodoc_visit, reservation_type, helpful_count, created_at
) VALUES (
    COALESCE(@review_seed_hospital_3, -1), @review_seed_author_38, NULL, 10, '수술은 빨리 끝나고 바로 좀 보이게 되는게 너무 신기했어요. 저는 수술하고 나서 마취 풀려도 하나도 안아팠어요. 약간 시려운 정도? 좀 뿌옇긴 했지만 다음날부터 잘 보여서 일상생활이 가능했어요. 아직 일주일차지만 이제 거의 다 잘 보여서 만족합니다.', '김준식', TRUE, TRUE, TRUE, 'RESERVATION', NULL, '2025-12-24 00:00:00'
);
SET @review_seed_review_id = LAST_INSERT_ID();

INSERT INTO review_treatment (review_id, treatment_id) VALUES
(@review_seed_review_id, 2),
(@review_seed_review_id, 1);

-- 04_리뷰 45행: 강남조은눈안과의원
INSERT INTO review (
    hospital_id, user_id, paid_price, rating, content, doctor_name,
    will_revisit, is_receipt_verified, is_modoodoc_visit, reservation_type, helpful_count, created_at
) VALUES (
    COALESCE(@review_seed_hospital_3, -1), @review_seed_author_39, NULL, 8.8, '검안도 꼼꼼하고 원장님 두 분 다 친절하세요! 관리도 보수적으로 가이드를 주시는 것 같아서 믿음이 갔습니다! 수술은 각막 강화술까지 해서 20분 정도는 걸린 것 같고 수술 후에 2시간쯤은 마취 깨니 아프더라고요. 이 부분도 미리 말씀주셔서 당황하지 않고 대처했습니다! 이제 일주일 째인데 먼 거리는 다 잘 보이고 가까운 건 아직 회복 중이네요! 검안 3군데 받았는데 적당히 거품없고 만족스러운 것 같습니다.', '김준식', TRUE, TRUE, TRUE, 'RESERVATION', NULL, '2026-03-31 00:00:00'
);
SET @review_seed_review_id = LAST_INSERT_ID();

INSERT INTO review_treatment (review_id, treatment_id) VALUES
(@review_seed_review_id, 583),
(@review_seed_review_id, 1),
(@review_seed_review_id, 2);

-- 04_리뷰 47행: 하늘안과의원 강남
INSERT INTO review (
    hospital_id, user_id, paid_price, rating, content, doctor_name,
    will_revisit, is_receipt_verified, is_modoodoc_visit, reservation_type, helpful_count, created_at
) VALUES (
    COALESCE(@review_seed_hospital_4, -1), @review_seed_author_40, NULL, 8.8, '안내원, 검사 해주는 직원들은 친절한데 원장님들은 사람마다 달라요. 그래도 무난한 편이에요. 원장님이 방문자가 별로 없어도 빨리 끝내서 당황했지만 그게 익숙한 곳 같아 그러려니 했습니다. 빠르고 효율을 추구하는 분들에게 추천해요. 수술 상담 때 관리를 위한 안약, 보안경 값이 따로 든다고 알려주셨으면 하는 아쉬움이 있어요.', '이원혁', FALSE, TRUE, TRUE, 'RESERVATION', NULL, '2026-08-21 00:00:00'
);
SET @review_seed_review_id = LAST_INSERT_ID();

INSERT INTO review_treatment (review_id, treatment_id) VALUES
(@review_seed_review_id, 4),
(@review_seed_review_id, 1),
(@review_seed_review_id, 582);

-- 04_리뷰 48행: 하늘안과의원 강남
INSERT INTO review (
    hospital_id, user_id, paid_price, rating, content, doctor_name,
    will_revisit, is_receipt_verified, is_modoodoc_visit, reservation_type, helpful_count, created_at
) VALUES (
    COALESCE(@review_seed_hospital_4, -1), @review_seed_author_41, NULL, 10, '난시가 심해 라식으로 진행했습니다. 검사과정과 수술 모두 만족했습니다. 시력교정 수술하실분들은 참고하세요. 수술하고 약 4~5시간동안은 조금 힘들어요. 눈에 이물감과 시림이 심해서 눈물이 많이 나고 눈뜨기가 힘듭니다. 저는 1시간동안 고속버스에서 사연있는 남자처럼 눈물 계속 흘렸습니다. 집 와서 안약넣고 몇시간정도 있으니 확 나아졌습니다 이틀날 되면 꽤 괜찮아집니다. 꼭 보호자 동반해서 오시고 눈물닦을 휴지나 손수건 준비하세요.', '김상명', TRUE, TRUE, TRUE, 'RESERVATION', NULL, '2026-04-07 00:00:00'
);
SET @review_seed_review_id = LAST_INSERT_ID();

INSERT INTO review_treatment (review_id, treatment_id) VALUES
(@review_seed_review_id, 4),
(@review_seed_review_id, 1),
(@review_seed_review_id, 582);

-- 04_리뷰 49행: 하늘안과의원 강남
INSERT INTO review (
    hospital_id, user_id, paid_price, rating, content, doctor_name,
    will_revisit, is_receipt_verified, is_modoodoc_visit, reservation_type, helpful_count, created_at
) VALUES (
    COALESCE(@review_seed_hospital_4, -1), @review_seed_author_42, NULL, 10, '주변인들의 추천과 모두닥에서 비교해보고 방문을 하게되었어요! 일단 들어가자마자 인테리어도 너무 이쁘고 선생님들 모두 너무 친절하셔서 감동받았습니다! 상담, 검사 모두 꼼꼼히 진행되고 당일검사와 수술도 일사천리로 진행되어 너무 좋았습니다~ 라식후 다음날 진짜 통증도 없고 광명을 찾아 너무 행복합니다ㅠㅠ!! 감사합니다.', '김상명', TRUE, TRUE, TRUE, 'RESERVATION', NULL, '2026-03-20 00:00:00'
);
SET @review_seed_review_id = LAST_INSERT_ID();

INSERT INTO review_treatment (review_id, treatment_id) VALUES
(@review_seed_review_id, 1),
(@review_seed_review_id, 4);

-- 04_리뷰 50행: 하늘안과의원 강남
INSERT INTO review (
    hospital_id, user_id, paid_price, rating, content, doctor_name,
    will_revisit, is_receipt_verified, is_modoodoc_visit, reservation_type, helpful_count, created_at
) VALUES (
    COALESCE(@review_seed_hospital_4, -1), @review_seed_author_43, NULL, 10, '직원 분들이 엄청 많았고 친절하셨습니다 하나하나 친절히 설명해주시고 이해하기 쉽게 알려주시고 수술도 빠르고 안전하고 친절히 진행되었습니다. 영업도 일절 없고 본인의 선택으로 결정할 수 있어서 매우 좋았습니다.', '서두리', TRUE, TRUE, TRUE, 'CONSULTATION', NULL, '2025-03-15 00:00:00'
);
SET @review_seed_review_id = LAST_INSERT_ID();

INSERT INTO review_treatment (review_id, treatment_id) VALUES
(@review_seed_review_id, 4),
(@review_seed_review_id, 1);

-- 04_리뷰 51행: 하늘안과의원 강남
INSERT INTO review (
    hospital_id, user_id, paid_price, rating, content, doctor_name,
    will_revisit, is_receipt_verified, is_modoodoc_visit, reservation_type, helpful_count, created_at
) VALUES (
    COALESCE(@review_seed_hospital_4, -1), @review_seed_author_44, NULL, 10, '병원 건물이라 찾아가기도 쉬웠고, 너무 깔끔했습니다. 직원분들도 많아서 친절한 설명도 해주시고 기구마다 어떤 검사인지 상세히 설명해주셨습니다. 아무래도 눈이다 보니까 무서운 느낌이 있었는데 의사선생님도 10명이었고 전문적인 느낌을 많이 받았습니다.', '김상명', FALSE, TRUE, TRUE, 'CONSULTATION', NULL, '2026-10-02 00:00:00'
);
SET @review_seed_review_id = LAST_INSERT_ID();

INSERT INTO review_treatment (review_id, treatment_id) VALUES
(@review_seed_review_id, 4);

-- 04_리뷰 52행: 하늘안과의원 강남
INSERT INTO review (
    hospital_id, user_id, paid_price, rating, content, doctor_name,
    will_revisit, is_receipt_verified, is_modoodoc_visit, reservation_type, helpful_count, created_at
) VALUES (
    COALESCE(@review_seed_hospital_4, -1), @review_seed_author_45, NULL, 10, '위아래 동공이 작아 라식이 힘들줄 알았는데 다행스럽게도 실패 없이 라식이 잘 되었습니다. 라식 진행 전, 후로 직원분들이 설명도 꼼꼼하고 자세하게 해주셨고 밝게 대해주셨습니다. 집에 가서도 카카오톡 상담 톡을 통해 질문도 할 수 있었고 사후관리도 열심히 도와주셨습니다. 수술 시 장비 이름은 기억이 잘 안 나네요. 리뷰가 괜히 많은 게 아니라는 생각이 듭니다.', '이창건', TRUE, TRUE, TRUE, 'RESERVATION', NULL, '2026-09-30 00:00:00'
);
SET @review_seed_review_id = LAST_INSERT_ID();

INSERT INTO review_treatment (review_id, treatment_id) VALUES
(@review_seed_review_id, 4);

-- 04_리뷰 53행: 하늘안과의원 강남
INSERT INTO review (
    hospital_id, user_id, paid_price, rating, content, doctor_name,
    will_revisit, is_receipt_verified, is_modoodoc_visit, reservation_type, helpful_count, created_at
) VALUES (
    COALESCE(@review_seed_hospital_4, -1), @review_seed_author_46, NULL, 10, '진료부터 검사까지 정말 친절했습니다. 덕분에 크게 긴장하지 않고 수술 받을 수 있었습니다. 무엇보다 부작용 등에 대한 설명을 자세히 해주셔서 정말 좋았고 같은 내용을 몇번 물어봐도 친절하게 대답해 주셔서 불안함을 많이 줄일 수 있었습니다. 만약 제 주변에서 수술을 고민하고 있다면 이 병원을 추천해야겠다는 생각이 들정도로 만족스럽게 수술 받을 수 있었습니다.', '김상명', TRUE, TRUE, TRUE, 'RESERVATION', NULL, '2026-09-30 00:00:00'
);
SET @review_seed_review_id = LAST_INSERT_ID();

INSERT INTO review_treatment (review_id, treatment_id) VALUES
(@review_seed_review_id, 4);

-- 04_리뷰 54행: 하늘안과의원 강남
INSERT INTO review (
    hospital_id, user_id, paid_price, rating, content, doctor_name,
    will_revisit, is_receipt_verified, is_modoodoc_visit, reservation_type, helpful_count, created_at
) VALUES (
    COALESCE(@review_seed_hospital_4, -1), @review_seed_author_47, NULL, 9.8, '모두닥 할인 이벤트로 진행 했는데 상담 해주시는 분들이 굉장히 친절하고 설명도 깔끔하게 다 해주셔서 잘 모르는 사람도 이해하기 쉬웠습니다 시력도 안경 안껴도 또렷하게 다 잘 보일 정도로 회복도 돼서 아주 만족스럽습니다!', '이원혁', FALSE, TRUE, TRUE, 'CONSULTATION', NULL, '2026-09-16 00:00:00'
);
SET @review_seed_review_id = LAST_INSERT_ID();

INSERT INTO review_treatment (review_id, treatment_id) VALUES
(@review_seed_review_id, 4);

-- 04_리뷰 55행: 하늘안과의원 강남
INSERT INTO review (
    hospital_id, user_id, paid_price, rating, content, doctor_name,
    will_revisit, is_receipt_verified, is_modoodoc_visit, reservation_type, helpful_count, created_at
) VALUES (
    COALESCE(@review_seed_hospital_4, -1), @review_seed_author_48, NULL, 10, '접수부터 검사, 진료, 수술까지 프로세스가 체계적으로 되어있습니다. 직원분들도 친절하고 알아듣기 쉽게 안내해주시고 검사도 편안한 마음으로 잘 받았습니다. 수술 후 당일은 눈이 좀많이 불편했고 다음날까지는 이물감이 살짝 있지만 3일차부터는 불편한거 없이 일상생활 할수있었습니다.', '이지영', TRUE, TRUE, TRUE, 'RESERVATION', NULL, '2026-09-16 00:00:00'
);
SET @review_seed_review_id = LAST_INSERT_ID();

INSERT INTO review_treatment (review_id, treatment_id) VALUES
(@review_seed_review_id, 4);

-- 04_리뷰 56행: 하늘안과의원 강남
INSERT INTO review (
    hospital_id, user_id, paid_price, rating, content, doctor_name,
    will_revisit, is_receipt_verified, is_modoodoc_visit, reservation_type, helpful_count, created_at
) VALUES (
    COALESCE(@review_seed_hospital_4, -1), @review_seed_author_49, NULL, 10, '라식 계속 고민하다가 모두닥에서 보고 예약해서 갔는데 너무 좋았어요!! 일단 당일수술까지 하면 3~4시간? 정도 걸린다고 했는데 검사랑 수술까지해서 2시간도 안걸린거같아요!! 그리고 데스크에 계신분들이랑 의사선생님들 다 너무 친절하시고 모르는거에도 잘 답변해주셔서 너무 좋았어요!!! 지금 4일차인데 너무 잘보이고 아직 초반이라 건조한거 빼면 너무 좋아요!! 가족들이랑 지인분들한테 추천할거같습니다!', '김상명', TRUE, TRUE, TRUE, 'RESERVATION', NULL, '2026-09-09 00:00:00'
);
SET @review_seed_review_id = LAST_INSERT_ID();

INSERT INTO review_treatment (review_id, treatment_id) VALUES
(@review_seed_review_id, 4);

-- 04_리뷰 57행: 하늘안과의원 강남
INSERT INTO review (
    hospital_id, user_id, paid_price, rating, content, doctor_name,
    will_revisit, is_receipt_verified, is_modoodoc_visit, reservation_type, helpful_count, created_at
) VALUES (
    COALESCE(@review_seed_hospital_4, -1), @review_seed_author_50, NULL, 9.8, '수술 전 시력 교정에 대해 걱정이 많았는데, 검사부터 수술까지 모든 과정이 매우 전문적이고 친절하여 안심할 수 있었습니다. 수술 도중에도 상황을 자세히 설명해 주셔서 원활하게 마쳤습니다. 수술 후 통증도 거의 없었고 회복 속도도 빨라 다음 날부터 눈에 띄게 밝은 세상을 보게 되었습니다. 진작 받을 걸 그랬다는 생각이 들 정도로 만족도가 매우 높고, 시력 교정 수술을 고민 중인 분들께 적극 추천하고 싶습니다.', '이원혁', TRUE, TRUE, TRUE, 'RESERVATION', NULL, '2026-09-09 00:00:00'
);
SET @review_seed_review_id = LAST_INSERT_ID();

INSERT INTO review_treatment (review_id, treatment_id) VALUES
(@review_seed_review_id, 4);

-- 04_리뷰 59행: 명동콤비치과의원
INSERT INTO review (
    hospital_id, user_id, paid_price, rating, content, doctor_name,
    will_revisit, is_receipt_verified, is_modoodoc_visit, reservation_type, helpful_count, created_at
) VALUES (
    COALESCE(@review_seed_hospital_5, -1), @review_seed_author_51, 2400000, 9.3, '병원 위치가 명동 롯데백화점 건너편으로 교통이 편리합니다. 병원은 깔끔하고 직원분들도 친절하고 했습니다. 예약하고 가서 잠시 대기 후 진료받았고 가격은 합리적이라 생각하고 바로 치료 받았습니다. 치료에 대한 설명도 잘해주셨고 앞으로 치료 잘 받아보겠습니다.', '황용인', TRUE, TRUE, TRUE, 'RESERVATION', NULL, '2026-08-28 00:00:00'
);
SET @review_seed_review_id = LAST_INSERT_ID();

INSERT INTO review_treatment (review_id, treatment_id) VALUES
(@review_seed_review_id, 26);

-- 04_리뷰 60행: 명동콤비치과의원
INSERT INTO review (
    hospital_id, user_id, paid_price, rating, content, doctor_name,
    will_revisit, is_receipt_verified, is_modoodoc_visit, reservation_type, helpful_count, created_at
) VALUES (
    COALESCE(@review_seed_hospital_5, -1), @review_seed_author_52, NULL, 10, '엄청 친절하게 잘 대해주셨어요! 발치만 하고 뼈이식, 임플란트가 필요했는데 설명도 잘해주시고 빠르게 진행해주셔서 당일 뼈이식하고 왔어요~ 생각보다 갑자기 하게되어서 무서웠는데 의사선생님이 안무섭게 상황 설명해주시고 달래주셔서 잘 받고 온 것 같아요ㅎㅎ 이식하고 4시간동안 피가 안멎어서 계속 병원에 전화했는데 친절하게 응대해주셨구요. 다행히 밥 먹고 약 먹으니 피도 멎었어요! 다음에도 여기서 임플란트 하려구요~', '황용인', FALSE, TRUE, TRUE, 'RESERVATION', NULL, '2026-08-26 00:00:00'
);
SET @review_seed_review_id = LAST_INSERT_ID();

INSERT INTO review_treatment (review_id, treatment_id) VALUES
(@review_seed_review_id, 26);

-- 04_리뷰 61행: 명동콤비치과의원
INSERT INTO review (
    hospital_id, user_id, paid_price, rating, content, doctor_name,
    will_revisit, is_receipt_verified, is_modoodoc_visit, reservation_type, helpful_count, created_at
) VALUES (
    COALESCE(@review_seed_hospital_5, -1), @review_seed_author_53, NULL, 8.8, '실장님은 매우 친절. 치위생과 선생님은 좀 지쳐보임. 인레이치료 여자쌤 최고.. 최선을 다해 고객의 니즈를 맞춰주심. 마취하는 의사선생님은 약간 무관심. 이빨뽑아주신 선생님은 테토남. 황원장님은 처음만 보고 그냥 전달식. 여튼… 아무 생각없이 바로 돈 결제랑 치료가 돼버림..', '구자철', TRUE, TRUE, TRUE, 'RESERVATION', NULL, '2026-08-26 00:00:00'
);
SET @review_seed_review_id = LAST_INSERT_ID();

INSERT INTO review_treatment (review_id, treatment_id) VALUES
(@review_seed_review_id, 26),
(@review_seed_review_id, 586),
(@review_seed_review_id, 585);

-- 04_리뷰 62행: 명동콤비치과의원
INSERT INTO review (
    hospital_id, user_id, paid_price, rating, content, doctor_name,
    will_revisit, is_receipt_verified, is_modoodoc_visit, reservation_type, helpful_count, created_at
) VALUES (
    COALESCE(@review_seed_hospital_5, -1), @review_seed_author_54, NULL, 10, '4개 발치 후 3개 임플란트 한 개 브릿지로 견적받고 바로 진행했어요. 긴장했었는데 치료과정을 세세히 말씀해주시며 진행해주셔서 긴장을 많이 풀 수 있었어요. 꾸준히 치료 잘 받아보려고요.', '구자철', TRUE, TRUE, TRUE, 'RESERVATION', NULL, '2026-08-25 00:00:00'
);
SET @review_seed_review_id = LAST_INSERT_ID();

INSERT INTO review_treatment (review_id, treatment_id) VALUES
(@review_seed_review_id, 585),
(@review_seed_review_id, 26);

-- 04_리뷰 63행: 명동콤비치과의원
INSERT INTO review (
    hospital_id, user_id, paid_price, rating, content, doctor_name,
    will_revisit, is_receipt_verified, is_modoodoc_visit, reservation_type, helpful_count, created_at
) VALUES (
    COALESCE(@review_seed_hospital_5, -1), @review_seed_author_55, NULL, 10, '접수하시는 분부터 의사쌤까지 너무 친절하시고 특히 원장선생님이 시술중에 세심하고 꼼꼼하게 신경써주셔서 무서웠던 치료가 금방 끝났네요. 과잉진료 절대없으시고 너무 저렴하고 신속하게 치료 잘받고 왔습니다.', '황용인', TRUE, TRUE, TRUE, 'RESERVATION', NULL, '2026-04-30 00:00:00'
);
SET @review_seed_review_id = LAST_INSERT_ID();

INSERT INTO review_treatment (review_id, treatment_id) VALUES
(@review_seed_review_id, 26);

-- 04_리뷰 64행: 명동콤비치과의원
INSERT INTO review (
    hospital_id, user_id, paid_price, rating, content, doctor_name,
    will_revisit, is_receipt_verified, is_modoodoc_visit, reservation_type, helpful_count, created_at
) VALUES (
    COALESCE(@review_seed_hospital_5, -1), @review_seed_author_56, NULL, 10, '임플란트 진행방식이나 일정관련해서 자세하게 설명해주셔서 믿고 시작합니다. 직원들도 질문에 친절하게 답해주시고 깔끔한 공간과 초진 검사실과 진료공간을 구분하여 상담해주시는것도 좋네요.', '황용인', TRUE, TRUE, TRUE, 'RESERVATION', NULL, '2026-05-16 00:00:00'
);
SET @review_seed_review_id = LAST_INSERT_ID();

INSERT INTO review_treatment (review_id, treatment_id) VALUES
(@review_seed_review_id, 26);

-- 04_리뷰 65행: 명동콤비치과의원
INSERT INTO review (
    hospital_id, user_id, paid_price, rating, content, doctor_name,
    will_revisit, is_receipt_verified, is_modoodoc_visit, reservation_type, helpful_count, created_at
) VALUES (
    COALESCE(@review_seed_hospital_5, -1), @review_seed_author_57, NULL, 9.8, '임플란트가 처음이라 여기저기 알아보다 명동콤비치과 리뷰가 너무 좋아서 예약하고 이용했어요! 가기전에는 금액이 다른데 보다 저렴하게 해주셔서 혹시나 실력이 없는 선생님께 받으면 어쩌지?라는 불안함이 있었는데, 가서 보니 여기서 하길 잘했다는 생각이 듭니다. 분야별로 전공하는 의사분들이 따로있어 믿을 수 있었어요! 들어가기전 설명해주시는 직원분들도 친절하시고 설명도 잘해주셨습니다. 아 그리고 제가 받아본 마취중..... 가장 안아프게 놔주셨어', '황용인', TRUE, TRUE, TRUE, 'RESERVATION', NULL, '2026-03-20 00:00:00'
);
SET @review_seed_review_id = LAST_INSERT_ID();

INSERT INTO review_treatment (review_id, treatment_id) VALUES
(@review_seed_review_id, 584),
(@review_seed_review_id, 587),
(@review_seed_review_id, 26);
