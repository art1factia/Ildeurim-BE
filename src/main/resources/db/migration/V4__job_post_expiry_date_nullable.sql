-- job_post.expiry_date 를 NULL 허용으로 변경
DO $$
BEGIN
  IF EXISTS (
    SELECT 1
    FROM information_schema.columns
    WHERE table_schema = 'public'
      AND table_name   = 'job_post'
      AND column_name  = 'expiry_date'
      AND is_nullable  = 'NO'
  ) THEN
    EXECUTE 'ALTER TABLE job_post ALTER COLUMN expiry_date DROP NOT NULL';
END IF;
END $$;