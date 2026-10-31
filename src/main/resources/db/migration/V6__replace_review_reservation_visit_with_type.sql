ALTER TABLE review
ADD COLUMN reservation_type ENUM('RESERVATION', 'CONSULTATION') NULL;

UPDATE review
SET reservation_type = 'RESERVATION'
WHERE is_reservation_visit = TRUE;

ALTER TABLE review
DROP COLUMN is_reservation_visit;
