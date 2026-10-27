CREATE TABLE `medical_category` (
    `medical_category_id` BIGINT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(255) NULL,
    PRIMARY KEY (`medical_category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `treatment_category` (
    `treatment_category_id` BIGINT NOT NULL AUTO_INCREMENT,
    `medical_category_id` BIGINT NULL,
    `name` VARCHAR(255) NULL,
    PRIMARY KEY (`treatment_category_id`),
    KEY `idx_treatment_category_medical_category` (`medical_category_id`),
    CONSTRAINT `fk_treatment_category_medical_category`
        FOREIGN KEY (`medical_category_id`) REFERENCES `medical_category` (`medical_category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `treatment` (
    `treatment_id` BIGINT NOT NULL AUTO_INCREMENT,
    `treatment_category_id` BIGINT NULL,
    `name` VARCHAR(255) NULL,
    `display_order` INT NULL,
    PRIMARY KEY (`treatment_id`),
    UNIQUE KEY `uk_treatment_category_name` (`treatment_category_id`, `name`),
    CONSTRAINT `fk_treatment_treatment_category`
        FOREIGN KEY (`treatment_category_id`) REFERENCES `treatment_category` (`treatment_category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `region` (
    `region_id` BIGINT NOT NULL AUTO_INCREMENT,
    `parent_region_id` BIGINT NULL,
    `name` VARCHAR(255) NULL,
    `region_type` ENUM('DONG', 'SIDO', 'SIGUNGU') NULL,
    PRIMARY KEY (`region_id`),
    UNIQUE KEY `uk_region_parent_name` (`parent_region_id`, `name`),
    CONSTRAINT `fk_region_parent_region`
        FOREIGN KEY (`parent_region_id`) REFERENCES `region` (`region_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `hospital` (
    `hospital_id` BIGINT NOT NULL AUTO_INCREMENT,
    `region_id` BIGINT NOT NULL,
    `name` VARCHAR(255) NULL,
    `latitude` DECIMAL(10, 7) NULL,
    `longitude` DECIMAL(10, 7) NULL,
    `has_specialist` BIT(1) NULL,
    `has_public_price` BIT(1) NULL,
    `has_night_clinic` BIT(1) NULL,
    `has_holiday_clinic` BIT(1) NULL,
    `is_reservable` BIT(1) NULL,
    `monthly_visit_count` INT NULL,
    PRIMARY KEY (`hospital_id`),
    KEY `idx_hospital_region` (`region_id`),
    CONSTRAINT `fk_hospital_region`
        FOREIGN KEY (`region_id`) REFERENCES `region` (`region_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `hospital_treatment` (
    `hospital_treatment_id` BIGINT NOT NULL AUTO_INCREMENT,
    `hospital_id` BIGINT NULL,
    `treatment_id` BIGINT NULL,
    `regular_price` INT NULL,
    `is_special_offer` BIT(1) NULL,
    PRIMARY KEY (`hospital_treatment_id`),
    UNIQUE KEY `uk_hospital_treatment_hospital_treatment` (`hospital_id`, `treatment_id`),
    KEY `idx_hospital_treatment_treatment` (`treatment_id`),
    CONSTRAINT `fk_hospital_treatment_hospital`
        FOREIGN KEY (`hospital_id`) REFERENCES `hospital` (`hospital_id`),
    CONSTRAINT `fk_hospital_treatment_treatment`
        FOREIGN KEY (`treatment_id`) REFERENCES `treatment` (`treatment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `mall` (
    `mall_id` BIGINT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(255) NULL,
    PRIMARY KEY (`mall_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `mall_price` (
    `mall_price_id` BIGINT NOT NULL AUTO_INCREMENT,
    `hospital_treatment_id` BIGINT NOT NULL,
    `mall_id` BIGINT NOT NULL,
    `price` INT NOT NULL,
    PRIMARY KEY (`mall_price_id`),
    UNIQUE KEY `uk_mall_price_mall_hospital_treatment` (`mall_id`, `hospital_treatment_id`),
    KEY `idx_mall_price_hospital_treatment` (`hospital_treatment_id`),
    CONSTRAINT `fk_mall_price_hospital_treatment`
        FOREIGN KEY (`hospital_treatment_id`) REFERENCES `hospital_treatment` (`hospital_treatment_id`),
    CONSTRAINT `fk_mall_price_mall`
        FOREIGN KEY (`mall_id`) REFERENCES `mall` (`mall_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `user` (
    `user_id` BIGINT NOT NULL AUTO_INCREMENT,
    `mall_id` BIGINT NULL,
    `email` VARCHAR(255) NULL,
    `name` VARCHAR(255) NULL,
    `nickname` VARCHAR(255) NULL,
    `phone_number` VARCHAR(255) NULL,
    PRIMARY KEY (`user_id`),
    KEY `idx_user_mall` (`mall_id`),
    CONSTRAINT `fk_user_mall`
        FOREIGN KEY (`mall_id`) REFERENCES `mall` (`mall_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `review` (
    `review_id` BIGINT NOT NULL AUTO_INCREMENT,
    `hospital_id` BIGINT NULL,
    `user_id` BIGINT NULL,
    `paid_price` INT NULL,
    `rating` DECIMAL(3, 1) NULL,
    `content` TEXT NULL,
    `doctor_name` VARCHAR(255) NULL,
    `will_revisit` BIT(1) NULL,
    `is_receipt_verified` BIT(1) NULL,
    `is_modoodoc_visit` BIT(1) NULL,
    `is_reservation_visit` BIT(1) NULL,
    `helpful_count` INT NULL,
    `created_at` DATETIME NULL,
    PRIMARY KEY (`review_id`),
    KEY `idx_review_hospital` (`hospital_id`),
    KEY `idx_review_user` (`user_id`),
    CONSTRAINT `fk_review_hospital`
        FOREIGN KEY (`hospital_id`) REFERENCES `hospital` (`hospital_id`),
    CONSTRAINT `fk_review_user`
        FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `review_treatment` (
    `review_treatment_id` BIGINT NOT NULL AUTO_INCREMENT,
    `review_id` BIGINT NULL,
    `treatment_id` BIGINT NULL,
    PRIMARY KEY (`review_treatment_id`),
    KEY `idx_review_treatment_review` (`review_id`),
    KEY `idx_review_treatment_treatment` (`treatment_id`),
    CONSTRAINT `fk_review_treatment_review`
        FOREIGN KEY (`review_id`) REFERENCES `review` (`review_id`),
    CONSTRAINT `fk_review_treatment_treatment`
        FOREIGN KEY (`treatment_id`) REFERENCES `treatment` (`treatment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `review_keyword` (
    `review_keyword_id` BIGINT NOT NULL AUTO_INCREMENT,
    `hospital_treatment_id` BIGINT NOT NULL,
    `keywords` JSON NOT NULL,
    `last_analyzed_review_id` BIGINT NOT NULL,
    `created_at` DATETIME NOT NULL,
    `updated_at` DATETIME NOT NULL,
    PRIMARY KEY (`review_keyword_id`),
    UNIQUE KEY `uk_review_keyword_hospital_treatment` (`hospital_treatment_id`),
    CONSTRAINT `fk_review_keyword_hospital_treatment`
        FOREIGN KEY (`hospital_treatment_id`) REFERENCES `hospital_treatment` (`hospital_treatment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
