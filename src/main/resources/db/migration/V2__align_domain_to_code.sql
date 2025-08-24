-- ==========================================
-- V2__align_domain_to_code.sql  (PostgreSQL)
-- - camelCase → snake_case 리네임(안전 함수)
-- - FK/인덱스/체크 보정
-- - 도메인(JPA) 스키마와 정합화
-- ==========================================

-- 안전 리네임 유틸: old와 new가 둘 다 있으면 데이터 병합 후 old 삭제,
-- old만 있으면 RENAME, old가 없으면 아무 것도 안 함.
CREATE OR REPLACE FUNCTION safe_rename_column(tbl regclass, old_name text, new_name text)
RETURNS void
LANGUAGE plpgsql
AS $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM pg_attribute WHERE attrelid = tbl AND attname = old_name AND NOT attisdropped
  ) THEN
    RETURN;
END IF;

  IF EXISTS (
    SELECT 1 FROM pg_attribute WHERE attrelid = tbl AND attname = new_name AND NOT attisdropped
  ) THEN
    EXECUTE format('UPDATE %s SET %I = COALESCE(%I, %I)', tbl, new_name, new_name, old_name);
EXECUTE format('ALTER TABLE %s DROP COLUMN %I', tbl, old_name);
ELSE
    EXECUTE format('ALTER TABLE %s RENAME COLUMN %I TO %I', tbl, old_name, new_name);
END IF;
END
$$;

-------------------------------
-- employer
-------------------------------
DO $$
BEGIN
  PERFORM safe_rename_column('employer','bossname','boss_name');
  PERFORM safe_rename_column('employer','phonenumber','phone_number');
  PERFORM safe_rename_column('employer','companyname','company_name');
  PERFORM safe_rename_column('employer','companylocation','company_location');
  PERFORM safe_rename_column('employer','companynumber','company_number');
  PERFORM safe_rename_column('employer','defaultquestionlist','default_question_list');
END$$;

-- NOT NULL (NULL 존재 시 실패하므로 사전 점검 권장)
ALTER TABLE employer
    ALTER COLUMN name             SET NOT NULL,
ALTER COLUMN email            SET NOT NULL,
  ALTER COLUMN boss_name        SET NOT NULL,
  ALTER COLUMN phone_number     SET NOT NULL,
  ALTER COLUMN company_name     SET NOT NULL,
  ALTER COLUMN company_location SET NOT NULL,
  ALTER COLUMN company_number   SET NOT NULL;

-- 유니크 인덱스(없을 때만)
DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE tablename='employer' AND indexname='ux_employer_email') THEN
    EXECUTE 'CREATE UNIQUE INDEX ux_employer_email ON employer(email)';
END IF;
  IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE tablename='employer' AND indexname='ux_employer_phone') THEN
    EXECUTE 'CREATE UNIQUE INDEX ux_employer_phone ON employer(phone_number)';
END IF;
  IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE tablename='employer' AND indexname='ux_employer_company_number') THEN
    EXECUTE 'CREATE UNIQUE INDEX ux_employer_company_number ON employer(company_number)';
END IF;
END$$;

-- employerJobField → employer_job_field
DO $$ BEGIN
  IF to_regclass('public.employer_job_field') IS NULL AND to_regclass('public.employerjobfield') IS NOT NULL THEN
    EXECUTE 'ALTER TABLE employerjobfield RENAME TO employer_job_field';
END IF;
END $$;
DO $$ BEGIN
  PERFORM safe_rename_column('employer_job_field','employerid','employer_id');
  PERFORM safe_rename_column('employer_job_field','jobfield','job_field');
END $$;

DO $$ BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname='fk_employer_job_field_employer') THEN
    EXECUTE 'ALTER TABLE employer_job_field
             ADD CONSTRAINT fk_employer_job_field_employer
             FOREIGN KEY (employer_id) REFERENCES employer(id) ON DELETE CASCADE';
END IF;
END $$;

-------------------------------
-- worker 컬렉션 (ElementCollection)
-------------------------------
-- workerBlgs → worker_blg
DO $$ BEGIN
  IF to_regclass('public.worker_blg') IS NULL AND to_regclass('public.workerblgs') IS NOT NULL THEN
    EXECUTE 'ALTER TABLE workerblgs RENAME TO worker_blg';
END IF;
END $$;
DO $$ BEGIN
  PERFORM safe_rename_column('worker_blg','workerid','worker_id');
END $$;

-- workerJobInterests → worker_job_interests
DO $$ BEGIN
  IF to_regclass('public.worker_job_interests') IS NULL AND to_regclass('public.workerjobinterests') IS NOT NULL THEN
    EXECUTE 'ALTER TABLE workerjobinterests RENAME TO worker_job_interests';
END IF;
END $$;
DO $$ BEGIN
  PERFORM safe_rename_column('worker_job_interests','workerid','worker_id');
  PERFORM safe_rename_column('worker_job_interests','jobfield','job_field');
END $$;

-------------------------------
-- career
-------------------------------
DO $$ BEGIN
  PERFORM safe_rename_column('career','workerid','worker_id');
  PERFORM safe_rename_column('career','mainduties','main_duties');
  PERFORM safe_rename_column('career','companyname','company_name');
  PERFORM safe_rename_column('career','jobfield','job_field');
  PERFORM safe_rename_column('career','startdate','start_date');
  PERFORM safe_rename_column('career','enddate','end_date');
  PERFORM safe_rename_column('career','isopening','is_opening');
END $$;

DO $$ BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname='fk_career_worker') THEN
    EXECUTE 'ALTER TABLE career
             ADD CONSTRAINT fk_career_worker
             FOREIGN KEY (worker_id) REFERENCES worker(id) ON DELETE CASCADE';
END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_career_worker ON career(worker_id);

-------------------------------
-- job_post
-------------------------------
-- 새 컬럼(없을 때만)
ALTER TABLE job_post
    ADD COLUMN IF NOT EXISTS employer_id           BIGINT,
    ADD COLUMN IF NOT EXISTS payment_type          VARCHAR(32),
    ADD COLUMN IF NOT EXISTS payment               BIGINT,
    ADD COLUMN IF NOT EXISTS location              TEXT,
    ADD COLUMN IF NOT EXISTS rest_time             INTEGER,
    ADD COLUMN IF NOT EXISTS work_type             VARCHAR(32),
    ADD COLUMN IF NOT EXISTS work_days_count       INTEGER,
    ADD COLUMN IF NOT EXISTS career_requirement    BOOLEAN,
    ADD COLUMN IF NOT EXISTS education_requirement VARCHAR(32),
    ADD COLUMN IF NOT EXISTS employment_type       VARCHAR(32),
    ADD COLUMN IF NOT EXISTS job_field             VARCHAR(64),
    ADD COLUMN IF NOT EXISTS start_date            TIMESTAMP,
    ADD COLUMN IF NOT EXISTS expiry_date           TIMESTAMP,
    ADD COLUMN IF NOT EXISTS work_place            VARCHAR(64),
    ADD COLUMN IF NOT EXISTS work_start_time       TIME,
    ADD COLUMN IF NOT EXISTS work_end_time         TIME,
    ADD COLUMN IF NOT EXISTS question_list         JSONB,
    ADD COLUMN IF NOT EXISTS save_question_list    BOOLEAN DEFAULT FALSE;

-- camelCase → snake_case 값 이관(있을 때만)
DO $$ BEGIN
  PERFORM CASE WHEN EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='job_post' AND column_name='employerid')
               THEN (EXECUTE 'UPDATE job_post SET employer_id = COALESCE(employer_id, employerid)') END;

  PERFORM CASE WHEN EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='job_post' AND column_name='worktype')
               THEN (EXECUTE 'UPDATE job_post SET work_type = COALESCE(work_type, worktype)') END;

  PERFORM CASE WHEN EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='job_post' AND column_name='workdayscount')
               THEN (EXECUTE 'UPDATE job_post SET work_days_count = COALESCE(work_days_count, workdayscount)') END;

  PERFORM CASE WHEN EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='job_post' AND column_name='educationrequirement')
               THEN (EXECUTE 'UPDATE job_post SET education_requirement = COALESCE(education_requirement, educationrequirement)') END;

  PERFORM CASE WHEN EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='job_post' AND column_name='employmenttype')
               THEN (EXECUTE 'UPDATE job_post SET employment_type = COALESCE(employment_type, employmenttype)') END;

  PERFORM CASE WHEN EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='job_post' AND column_name='jobfield')
               THEN (EXECUTE 'UPDATE job_post SET job_field = COALESCE(job_field, jobfield)') END;

  PERFORM CASE WHEN EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='job_post' AND column_name='workplace')
               THEN (EXECUTE 'UPDATE job_post SET work_place = COALESCE(work_place, workplace)') END;

  PERFORM CASE WHEN EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='job_post' AND column_name='workstarttime')
               THEN (EXECUTE 'UPDATE job_post SET work_start_time = COALESCE(work_start_time, workstarttime)') END;

  PERFORM CASE WHEN EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='job_post' AND column_name='workendtime')
               THEN (EXECUTE 'UPDATE job_post SET work_end_time = COALESCE(work_end_time, workendtime)') END;

  PERFORM CASE WHEN EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='job_post' AND column_name='questionlist')
               THEN (EXECUTE 'UPDATE job_post SET question_list = COALESCE(question_list, questionlist)') END;

  -- expirydate(date) → expiry_date(timestamp)
  PERFORM CASE WHEN EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='job_post' AND column_name='expirydate')
               THEN (EXECUTE 'UPDATE job_post SET expiry_date = COALESCE(expiry_date, expirydate::timestamp) WHERE expiry_date IS NULL') END;
END $$;

-- start_date가 없으면 created_at로 보정
UPDATE job_post SET start_date = COALESCE(start_date, created_at) WHERE start_date IS NULL;

-- careerRequirement(string/enum) → boolean 유추(있을 때만)
DO $$ BEGIN
  IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='job_post' AND column_name='careerrequirement') THEN
    EXECUTE $Q$
UPDATE job_post
SET career_requirement = COALESCE(
        career_requirement,
        CASE
            WHEN careerrequirement ~* '^(required|true|y|yes|1)$' THEN TRUE
            WHEN careerrequirement ~* '^(none|false|n|no|0)$'    THEN FALSE
            ELSE FALSE
            END
                         )
    $Q$;
END IF;
END $$;

-- 낡은 컬럼 제거(있으면)
ALTER TABLE job_post
DROP COLUMN IF EXISTS employerid,
  DROP COLUMN IF EXISTS worktype,
  DROP COLUMN IF EXISTS workdayscount,
  DROP COLUMN IF EXISTS educationrequirement,
  DROP COLUMN IF EXISTS employmenttype,
  DROP COLUMN IF EXISTS jobfield,
  DROP COLUMN IF EXISTS workplace,
  DROP COLUMN IF EXISTS workstarttime,
  DROP COLUMN IF EXISTS workendtime,
  DROP COLUMN IF EXISTS questionlist,
  DROP COLUMN IF EXISTS expirydate,
  DROP COLUMN IF EXISTS careerrequirement;

-- 기본값/NOT NULL 강화 (NULL 존재 시 실패 가능)
UPDATE job_post SET save_question_list = COALESCE(save_question_list, FALSE);
ALTER TABLE job_post
    ALTER COLUMN title                 SET NOT NULL,
ALTER COLUMN content               SET NOT NULL,
  ALTER COLUMN payment_type          SET NOT NULL,
  ALTER COLUMN payment               SET NOT NULL,
  ALTER COLUMN location              SET NOT NULL,
  ALTER COLUMN rest_time             SET NOT NULL,
  ALTER COLUMN work_type             SET NOT NULL,
  ALTER COLUMN career_requirement    SET NOT NULL,
  ALTER COLUMN job_field             SET NOT NULL,
  ALTER COLUMN start_date            SET NOT NULL,
  ALTER COLUMN expiry_date           SET NOT NULL,
  ALTER COLUMN status                SET NOT NULL,
  ALTER COLUMN work_start_time       SET NOT NULL,
  ALTER COLUMN work_end_time         SET NOT NULL,
  ALTER COLUMN education_requirement SET NOT NULL,
  ALTER COLUMN employment_type       SET NOT NULL,
  ALTER COLUMN work_place            SET NOT NULL,
  ALTER COLUMN save_question_list    SET NOT NULL,
  ALTER COLUMN employer_id           SET NOT NULL;

-- FK 재정의(중복/이전 이름 FK 제거 후 보정)
DO $$
DECLARE c_name text;
BEGIN
FOR c_name IN
SELECT conname
FROM   pg_constraint
WHERE  conrelid = 'job_post'::regclass
    AND    contype = 'f'
  LOOP
    -- 모든 FK 검사해서 employer_id 대상 아닌 것만 유지하려면 더 정교한 필터가 필요하지만,
    -- 여기선 동일 이름 재생성을 위해 중복 FK가 있으면 제거
    IF c_name = 'fk_job_post_employer' THEN
      EXECUTE format('ALTER TABLE job_post DROP CONSTRAINT %I', c_name);
END IF;
END LOOP;

  IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname='fk_job_post_employer') THEN
    EXECUTE 'ALTER TABLE job_post
             ADD CONSTRAINT fk_job_post_employer
             FOREIGN KEY (employer_id) REFERENCES employer(id) ON DELETE CASCADE';
END IF;
END$$;

-- 인덱스/체크
DROP INDEX IF EXISTS idx_job_post_employer;
CREATE INDEX IF NOT EXISTS idx_job_post_employer ON job_post(employer_id);
DROP INDEX IF EXISTS idx_job_post_status_created;
CREATE INDEX IF NOT EXISTS idx_job_post_status_created ON job_post(status, created_at DESC);

DO $$ BEGIN
  IF EXISTS (
    SELECT 1 FROM pg_constraint
    WHERE conrelid='job_post'::regclass AND conname='chk_jobpost_questionlist_json'
  ) THEN
    EXECUTE 'ALTER TABLE job_post DROP CONSTRAINT chk_jobpost_questionlist_json';
END IF;
EXECUTE 'ALTER TABLE job_post
           ADD CONSTRAINT chk_jobpost_questionlist_json
           CHECK (question_list IS NULL OR jsonb_typeof(question_list) = ''array'')';
END $$;

-- JSONB GIN
CREATE INDEX IF NOT EXISTS idx_job_post_questionlist_gin ON job_post USING GIN (question_list);

-- 컬렉션 테이블 리네임/보정
-- jobPostWorkDays → job_post_work_days
DO $$ BEGIN
  IF to_regclass('public.job_post_work_days') IS NULL AND to_regclass('public.jobpostworkdays') IS NOT NULL THEN
    EXECUTE 'ALTER TABLE jobpostworkdays RENAME TO job_post_work_days';
END IF;
END $$;
DO $$ BEGIN
  PERFORM safe_rename_column('job_post_work_days','jobpostid','job_post_id');
  PERFORM safe_rename_column('job_post_work_days','workday','work_day');
END $$;
DO $$ BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname='fk_job_post_work_days_post') THEN
    EXECUTE 'ALTER TABLE job_post_work_days
             ADD CONSTRAINT fk_job_post_work_days_post
             FOREIGN KEY (job_post_id) REFERENCES job_post(id) ON DELETE CASCADE';
END IF;
END $$;

-- jobPostApplyMethods → job_post_apply_methods
DO $$ BEGIN
  IF to_regclass('public.job_post_apply_methods') IS NULL AND to_regclass('public.jobpostapplymethods') IS NOT NULL THEN
    EXECUTE 'ALTER TABLE jobpostapplymethods RENAME TO job_post_apply_methods';
END IF;
END $$;
DO $$ BEGIN
  PERFORM safe_rename_column('job_post_apply_methods','jobpostid','job_post_id');
  PERFORM safe_rename_column('job_post_apply_methods','applymethod','apply_method');
END $$;
DO $$ BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname='fk_job_post_apply_methods_post') THEN
    EXECUTE 'ALTER TABLE job_post_apply_methods
             ADD CONSTRAINT fk_job_post_apply_methods_post
             FOREIGN KEY (job_post_id) REFERENCES job_post(id) ON DELETE CASCADE';
END IF;
END $$;

-- jobPostJobFields → job_post_job_fields (값 컬럼을 apply_method로 통일)
DO $$ BEGIN
  IF to_regclass('public.job_post_job_fields') IS NULL AND to_regclass('public.jobpostjobfields') IS NOT NULL THEN
    EXECUTE 'ALTER TABLE jobpostjobfields RENAME TO job_post_job_fields';
END IF;
END $$;
DO $$ BEGIN
  PERFORM safe_rename_column('job_post_job_fields','jobpostid','job_post_id');
  PERFORM safe_rename_column('job_post_job_fields','jobfield','apply_method');
END $$;
DO $$ BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname='fk_job_post_job_fields_post') THEN
    EXECUTE 'ALTER TABLE job_post_job_fields
             ADD CONSTRAINT fk_job_post_job_fields_post
             FOREIGN KEY (job_post_id) REFERENCES job_post(id) ON DELETE CASCADE';
END IF;
END $$;

-------------------------------
-- application
-------------------------------
DO $$ BEGIN
  PERFORM safe_rename_column('application','jobpostid','job_post_id');
  PERFORM safe_rename_column('application','workerid','worker_id');
  PERFORM safe_rename_column('application','applicationstatus','application_status');
  PERFORM safe_rename_column('application','applymethod','apply_method');
  PERFORM safe_rename_column('application','submissiontime','submission_time');
END $$;

DO $$ BEGIN
  -- 기존 중복 유니크 제거(있으면)
  IF EXISTS (
    SELECT 1 FROM pg_constraint
    WHERE  conrelid='application'::regclass AND conname='application_jobpostid_workerid_key'
  ) THEN
    EXECUTE 'ALTER TABLE application DROP CONSTRAINT application_jobpostid_workerid_key';
END IF;

  IF NOT EXISTS (
    SELECT 1 FROM pg_constraint WHERE conname='ux_application_jobpost_worker'
  ) THEN
    EXECUTE 'ALTER TABLE application
             ADD CONSTRAINT ux_application_jobpost_worker UNIQUE(job_post_id, worker_id)';
END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_application_job_post ON application(job_post_id);
CREATE INDEX IF NOT EXISTS idx_application_worker   ON application(worker_id);

DO $$ BEGIN
  IF EXISTS (
    SELECT 1 FROM pg_constraint
    WHERE conrelid='application'::regclass AND conname='chk_application_answers_json'
  ) THEN
    EXECUTE 'ALTER TABLE application DROP CONSTRAINT chk_application_answers_json';
END IF;
EXECUTE 'ALTER TABLE application
           ADD CONSTRAINT chk_application_answers_json
           CHECK (answers IS NULL OR jsonb_typeof(answers) IN (''array'',''object''))';
END $$;

CREATE INDEX IF NOT EXISTS idx_application_answers_gin ON application USING GIN (answers);

-------------------------------
-- job
-------------------------------
DO $$ BEGIN
  PERFORM safe_rename_column('job','applicationid','application_id');
  PERFORM safe_rename_column('job','workerid','worker_id');
END $$;

DO $$ BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname='fk_job_application') THEN
    EXECUTE 'ALTER TABLE job
             ADD CONSTRAINT fk_job_application
             FOREIGN KEY (application_id) REFERENCES application(id)';
END IF;
END $$;

CREATE UNIQUE INDEX IF NOT EXISTS ux_job_application ON job(application_id);
CREATE INDEX IF NOT EXISTS idx_job_worker ON job(worker_id);

-------------------------------
-- review + 컬렉션
-------------------------------
DO $$ BEGIN
  PERFORM safe_rename_column('review','workerid','worker_id');
  PERFORM safe_rename_column('review','employerid','employer_id');
END $$;

DO $$ BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname='fk_review_worker') THEN
    EXECUTE 'ALTER TABLE review
             ADD CONSTRAINT fk_review_worker
             FOREIGN KEY (worker_id) REFERENCES worker(id)';
END IF;
  IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname='fk_review_employer') THEN
    EXECUTE 'ALTER TABLE review
             ADD CONSTRAINT fk_review_employer
             FOREIGN KEY (employer_id) REFERENCES employer(id)';
END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_review_worker   ON review(worker_id);
CREATE INDEX IF NOT EXISTS idx_review_employer ON review(employer_id);

-- reviewHashtags → review_hashtags
DO $$ BEGIN
  IF to_regclass('public.review_hashtags') IS NULL AND to_regclass('public.reviewhashtags') IS NOT NULL THEN
    EXECUTE 'ALTER TABLE reviewhashtags RENAME TO review_hashtags';
END IF;
END $$;
DO $$ BEGIN
  PERFORM safe_rename_column('review_hashtags','reviewid','review_id');
END $$;
DO $$ BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname='fk_review_hashtags_review') THEN
    EXECUTE 'ALTER TABLE review_hashtags
             ADD CONSTRAINT fk_review_hashtags_review
             FOREIGN KEY (review_id) REFERENCES review(id) ON DELETE CASCADE';
END IF;
END $$;

-- reviewAnswers → review_answers
DO $$ BEGIN
  IF to_regclass('public.review_answers') IS NULL AND to_regclass('public.reviewanswers') IS NOT NULL THEN
    EXECUTE 'ALTER TABLE reviewanswers RENAME TO review_answers';
END IF;
END $$;
DO $$ BEGIN
  PERFORM safe_rename_column('review_answers','reviewid','review_id');
  PERFORM safe_rename_column('review_answers','evaluationtype','evaluation_type');
END $$;
DO $$ BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname='fk_review_answers_review') THEN
    EXECUTE 'ALTER TABLE review_answers
             ADD CONSTRAINT fk_review_answers_review
             FOREIGN KEY (review_id) REFERENCES review(id) ON DELETE CASCADE';
END IF;
END $$;

-- 끝.
