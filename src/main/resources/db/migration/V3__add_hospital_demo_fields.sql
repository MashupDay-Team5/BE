ALTER TABLE `hospital` ADD COLUMN `rating` DECIMAL(3, 1) NULL;
ALTER TABLE `hospital` ADD COLUMN `review_count` INT NULL;
ALTER TABLE `hospital` ADD COLUMN `is_popular` BIT(1) NULL;
ALTER TABLE `hospital` ADD COLUMN `is_open_today` BIT(1) NULL;
ALTER TABLE `hospital` ADD COLUMN `address` VARCHAR(255) NULL;
ALTER TABLE `hospital` ADD COLUMN `has_special_offer` BIT(1) NULL;
ALTER TABLE `hospital_treatment` DROP COLUMN `is_special_offer`;
