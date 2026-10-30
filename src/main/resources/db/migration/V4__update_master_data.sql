-- 기존 참조가 있으면 외래키 제약으로 삭제가 중단된다.
DELETE FROM treatment
WHERE treatment_id IN (256, 257, 258, 259, 260);

UPDATE treatment_category
SET name = '다이어트 주사'
WHERE treatment_category_id IN (28, 41);

INSERT INTO treatment_category (treatment_category_id, medical_category_id, name) VALUES
(99, 1, '기타'),
(100, 2, '기타'),
(101, 3, '반영구문신');

INSERT INTO treatment (treatment_id, treatment_category_id, name, display_order) VALUES
(578, 2, '모노비전 라식', NULL),
(579, 2, 'LBV 라식', NULL),
(580, 2, '노안 라식', NULL),
(581, 5, '안구건조증 IPL', 2),
(582, 99, '인공눈물 처방', NULL),
(583, 99, '각막강화술', NULL),
(584, 8, '치조골', NULL),
(585, 9, '지르코니아 크라운', NULL),
(586, 13, '잇몸치료', 2),
(587, 100, '발치', NULL),
(588, 3, '백내장수술 렌즈', 7),
(589, 11, '치아미백', 2),
(590, 12, '라미네이트', 2),
(591, 18, '절개모발이식', 8),
(592, 18, '비절개모발이식(삭발)', 9),
(593, 18, '비절개모발이식(무삭발)', 10),
(594, 101, '눈썹 문신', 2),
(595, 101, '입술 문신', 3),
(596, 101, '아이라인 문신', 4),
(597, 101, '두피 문신', 5),
(598, 101, '헤어라인 문신', 6),
(599, 101, '유륜 문신', 7),
(600, 41, '지방분해주사', 2),
(601, 41, '주 1회 맞는 다이어트주사 처방', 3),
(602, 41, '하루 1회 맞는 다이어트주사 처방', 4),
(603, 85, '도수치료', 2),
(604, 89, '체외충격파', 2);

INSERT INTO region (region_id, parent_region_id, name, region_type) VALUES
(153, 2, '오산시', 'SIGUNGU'),
(154, 2, '용인시', 'SIGUNGU'),
(155, 2, '의왕시', 'SIGUNGU'),
(156, 2, '의정부시', 'SIGUNGU'),
(157, 2, '이천시', 'SIGUNGU'),
(158, 2, '파주시', 'SIGUNGU'),
(159, 2, '평택시', 'SIGUNGU'),
(160, 2, '포천시', 'SIGUNGU'),
(161, 2, '하남시', 'SIGUNGU'),
(162, 2, '화성시', 'SIGUNGU');
