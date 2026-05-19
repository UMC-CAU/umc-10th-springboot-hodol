SET @member_social_type_exists = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'member'
      AND column_name = 'social_type'
);

SET @member_social_type_has_local = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'member'
      AND column_name = 'social_type'
      AND column_type LIKE '%LOCAL%'
);

SET @migration_sql = IF(
    @member_social_type_exists = 1 AND @member_social_type_has_local = 0,
    "ALTER TABLE member MODIFY COLUMN social_type ENUM('APPLE','GOOGLE','KAKAO','LOCAL') NOT NULL",
    "SELECT 1"
);

PREPARE stmt FROM @migration_sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
