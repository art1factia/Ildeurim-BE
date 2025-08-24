-- V3__normalize_question_and_answer_lists_to_arrays.sql
-- 1) object{"items":[...]} → array[...] 정규화
-- 2) 컬럼 타입 jsonb 보정
-- 3) 배열만 허용하는 CHECK 제약 재정의
-- 4) GIN 인덱스 보장

-- 0) 컬럼 타입 보정(jsonb)
DO $$
BEGIN
  IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='job_post' AND column_name='question_list') THEN
    EXECUTE 'ALTER TABLE job_post ALTER COLUMN question_list TYPE jsonb USING question_list::jsonb';
END IF;

  IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='employer' AND column_name='default_question_list') THEN
    EXECUTE 'ALTER TABLE employer ALTER COLUMN default_question_list TYPE jsonb USING default_question_list::jsonb';
END IF;

  IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='application' AND column_name='answers') THEN
    EXECUTE 'ALTER TABLE application ALTER COLUMN answers TYPE jsonb USING answers::jsonb';
END IF;
END $$;

-- 1) job_post.question_list: object{"items":[...]} → array[...]
DO $$
BEGIN
  IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='job_post' AND column_name='question_list') THEN
    EXECUTE $SQL$
UPDATE job_post
SET question_list = CASE
                        WHEN question_list IS NULL THEN NULL
                        WHEN jsonb_typeof(question_list) = 'object'
                            AND question_list ? 'items'
    AND jsonb_typeof(question_list->'items') = 'array'
    THEN (question_list->'items')
        ELSE question_list
END
WHERE question_list IS NOT NULL;
    $SQL$;
END IF;
END $$;

-- 2) employer.default_question_list: object{"items":[...]} → array[...]
DO $$
BEGIN
  IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='employer' AND column_name='default_question_list') THEN
    EXECUTE $SQL$
UPDATE employer
SET default_question_list = CASE
                                WHEN default_question_list IS NULL THEN NULL
                                WHEN jsonb_typeof(default_question_list) = 'object'
                                    AND default_question_list ? 'items'
    AND jsonb_typeof(default_question_list->'items') = 'array'
    THEN (default_question_list->'items')
        ELSE default_question_list
END
WHERE default_question_list IS NOT NULL;
    $SQL$;
END IF;
END $$;

-- 3) application.answers: object{"items":[...]} → array[...]
DO $$
BEGIN
  IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='application' AND column_name='answers') THEN
    EXECUTE $SQL$
UPDATE application
SET answers = CASE
                  WHEN answers IS NULL THEN NULL
                  WHEN jsonb_typeof(answers) = 'object'
                      AND answers ? 'items'
    AND jsonb_typeof(answers->'items') = 'array'
    THEN (answers->'items')
        ELSE answers
END
WHERE answers IS NOT NULL;
    $SQL$;
END IF;
END $$;

-- 4) CHECK 제약 재정의: 배열만 허용
-- job_post.question_list
DO $$
BEGIN
  IF EXISTS (SELECT 1 FROM pg_constraint WHERE conname='chk_jobpost_questionlist_json') THEN
    EXECUTE 'ALTER TABLE job_post DROP CONSTRAINT chk_jobpost_questionlist_json';
END IF;
  IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='job_post' AND column_name='question_list') THEN
    EXECUTE 'ALTER TABLE job_post
             ADD CONSTRAINT chk_jobpost_questionlist_json
             CHECK (question_list IS NULL OR jsonb_typeof(question_list) = ''array'')';
END IF;
END $$;

-- application.answers
DO $$
BEGIN
  IF EXISTS (SELECT 1 FROM pg_constraint WHERE conname='chk_application_answers_json') THEN
    EXECUTE 'ALTER TABLE application DROP CONSTRAINT chk_application_answers_json';
END IF;
  IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='application' AND column_name='answers') THEN
    EXECUTE 'ALTER TABLE application
             ADD CONSTRAINT chk_application_answers_json
             CHECK (answers IS NULL OR jsonb_typeof(answers) = ''array'')';
END IF;
END $$;

-- (선택) employer.default_question_list에도 동일 제약 추가
DO $$
BEGIN
  IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='employer' AND column_name='default_question_list') THEN
    IF EXISTS (SELECT 1 FROM pg_constraint WHERE conname='chk_employer_default_question_list_json') THEN
      EXECUTE 'ALTER TABLE employer DROP CONSTRAINT chk_employer_default_question_list_json';
END IF;
EXECUTE 'ALTER TABLE employer
             ADD CONSTRAINT chk_employer_default_question_list_json
             CHECK (default_question_list IS NULL OR jsonb_typeof(default_question_list) = ''array'')';
END IF;
END $$;

-- 5) GIN 인덱스 보장(이미 있으면 건너뜀)
CREATE INDEX IF NOT EXISTS idx_job_post_questionlist_gin ON job_post USING GIN (question_list);
CREATE INDEX IF NOT EXISTS idx_application_answers_gin    ON application USING GIN (answers);
