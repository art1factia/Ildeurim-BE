-- ==========================================
-- V2__align_domain_to_code.sql  (PostgreSQL, Flyway)
-- - camelCase → snake_case 정리
-- - FK/인덱스/제약 보정
-- - 도메인 코드(JPA) 컬럼/타입에 맞춤
-- ==========================================

-------------------------------
-- helper: 테이블 존재 검사
-------------------------------
DO $$
BEGIN
  -- nothing, just ensure DO-blocks are allowed
END$$;

-------------------------------
-- 1) employer 컬럼 리네임
-------------------------------
DO $$
BEGIN
  IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='employer' AND column_name='bossname') THEN
    EXECUTE 'ALTER TABLE employer RENAME COLUMN bossname TO boss_name';
END IF;
  IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='employer' AND column_name='phonenumber') THEN
    EXECUTE 'ALTER TABLE employer RENAME COLUMN phonenumber TO phone_number';
END IF;
  IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='employer' AND column_name='companyname') THEN
    EXECUTE 'ALTER TABLE employer RENAME COLUMN companyname TO company_name';
END IF;
  IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='employer' AND column_name='companylocation') THEN
    EXECUTE 'ALTER TABLE employer RENAME COLUMN companylocation TO company_location';
END IF;
  IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='employer' AND column_name='companynumber') THEN
    EXECUTE 'ALTER TABLE employer RENAME COLUMN companynumber TO company_number';
END IF;
  IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='employer' AND column_name='defaultquestionlist') THEN
    EXECUTE 'ALTER TABLE employer RENAME COLUMN defaultquestionlist TO default_question_list';
END IF;
END$$;

-- NOT NULL 보정(데이터에 NULL 있으면 실패하므로, 적용 전 미리 점검 권장)
ALTER TABLE employer
    ALTER COLUMN name             SET NOT NULL,
ALTER COLUMN email            SET NOT NULL,
  ALTER COLUMN boss_name        SET NOT NULL,
  ALTER COLUMN phone_number     SET NOT NULL,
  ALTER COLUMN company_name     SET NOT NULL,
  ALTER COLUMN company_location SET NOT NULL,
  ALTER COLUMN company_number   SET NOT NULL;

-- 유니크 인덱스(존재하지 않을 때만 생성)
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
DO $$
BEGIN
  IF to_regclass('public.employer_job_field') IS NULL AND to_regclass('public.employerjobfield') IS NOT NULL THEN
    EXECUTE 'ALTER TABLE employerjobfield RENAME TO employer_job_field';
END IF;
END$$;

DO $$
BEGIN
  IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='employer_job_field' AND column_name='employerid') THEN
    EXECUTE 'ALTER TABLE employer_job_field RENAME COLUMN employerid TO employer_id';
END IF;
  IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='employer_job_field' AND column_name='jobfield') THEN
    EXECUTE 'ALTER TABLE employer_job_field RENAME COLUMN jobfield TO job_field';
END IF;
END$$;

-- FK 보정(없을 때만 추가)
DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname='fk_employer_job_field_employer') THEN
    EXECUTE 'ALTER TABLE employer_job_field ADD CONSTRAINT fk_employer_job_field_employer
             FOREIGN KEY (employer_id) REFERENCES employer(id) ON DELETE CASCADE';
END IF;
END$$;

-------------------------------
-- 2) worker 컬렉션 테이블 리네임
-------------------------------
-- workerBlgs → worker_blg
DO $$
BEGIN
  IF to_regclass('public.worker_blg') IS NULL AND to_regclass('public.workerblgs') IS NOT NULL THEN
    EXECUTE 'ALTER TABLE workerblgs RENAME TO worker_blg';
END IF;
END$$;

DO $$
BEGIN
  IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='worker_blg' AND column_name='workerid') THEN
    EXECUTE 'ALTER TABLE worker_blg RENAME COLUMN workerid TO worker_id';
END IF;
END$$;

-- workerJobInterests → worker_job_interests
DO $$
BEGIN
  IF to_regclass('public.worker_job_interests') IS NULL AND to_regclass('public.workerjobinterests') IS NOT NULL THEN
    EXECUTE 'ALTER TABLE workerjobinterests RENAME TO worker_job_interests';
END IF;
END$$;

DO $$
BEGIN
  IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='worker_job_interests' AND column_name='workerid') THEN
    EXECUTE 'ALTER TABLE worker_job_interests RENAME COLUMN workerid TO worker_id';
END IF;
  IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='worker_job_interests' AND column_name='jobfield') THEN
    EXECUTE 'ALTER TABLE worker_job_interests RENAME COLUMN jobfield TO job_field';
END IF;
END$$;

-------------------------------
-- 3) career 컬럼 리네임 + FK
-------------------------------
DO $$
BEGIN
  IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='career' AND column_name='workerid') THEN
    EXECUTE 'ALTER TABLE career RENAME COLUMN workerid TO worker_id';
END IF;
  IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='career' AND column_name='mainduties') THEN
    EXECUTE 'ALTER TABLE career RENAME COLUMN mainduties TO main_duties';
END IF;
  IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='career' AND column_name='companyname') THEN
    EXECUTE 'ALTER TABLE career RENAME COLUMN companyname TO company_name';
END IF;
  IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='career' AND column_name='jobfield') THEN
    EXECUTE 'ALTER TABLE career RENAME COLUMN jobfield TO job_field';
END IF;
  IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='career' AND column_name='startdate') THEN
    EXECUTE 'ALTER TABLE career RENAME COLUMN startdate TO start_date';
END IF;
  IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='career' AND column_name='enddate') THEN
    EXECUTE 'ALTER TABLE career RENAME COLUMN enddate TO end_date';
END IF;
  IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='career' AND column_name='isopening') THEN
    EXECUTE 'ALTER TABLE career RENAME COLUMN isopening TO is_opening';
END IF;
END$$;

-- FK 재정의(이미 있으면 스킵)
DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1
    FROM   pg_constraint
    WHERE  conname='fk_career_worker'
  ) THEN
    EXECUTE 'ALTER TABLE career ADD CONSTRAINT fk_career_worker
             FOREIGN KEY (worker_id) REFERENCES worker(id) ON DELETE CASCADE';
END IF;
END$$;

CREATE INDEX IF NOT EXISTS idx_career_worker ON career(worker_id);

-------------------------------
-- 4) job_post 대정리
-------------------------------
-- 새 컬럼 생성
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

-- camelCase → snake_case 값 이관 (있는 컬럼만)
DO $$
BEGIN
  IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='job_post' AND column_name='employerid') THEN
    EXECUTE 'UPDATE job_post SET employer_id = COALESCE(employer_id, employerid)';
END IF;
  IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='job_post' AND column_name='worktype') THEN
    EXECUTE 'UPDATE job_post SET work_type = COALESCE(work_type, worktype)';
END IF;
  IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='job_post' AND column_name='workdayscount') THEN
    EXECUTE 'UPDATE job_post SET work_days_count = COALESCE(work_days_count, workdayscount)';
END IF;
  IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='job_post' AND column_name='educationrequirement') THEN
    EXECUTE 'UPDATE job_post SET education_requirement = COALESCE(education_requirement, educationrequirement)';
END IF;
  IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='job_post' AND column_name='employmenttype') THEN
    EXECUTE 'UPDATE job_post SET employment_type = COALESCE(employment_type, employmenttype)';
END IF;
  IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='job_post' AND column_name='jobfield') THEN
    EXECUTE 'UPDATE job_post SET job_field = COALESCE(job_field, jobfield)';
END IF;
  IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='job_post' AND column_name='workplace') THEN
    EXECUTE 'UPDATE job_post SET work_place = COALESCE(work_place, workplace)';
END IF;
  IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='job_post' AND column_name='workstarttime') THEN
    EXECUTE 'UPDATE job_post SET work_start_time = COALESCE(work_start_time, workstarttime)';
END IF;
  IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='job_post' AND column_name='workendtime') THEN
    EXECUTE 'UPDATE job_post SET work_end_time = COALESCE(work_end_time, workendtime)';
END IF;
  IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='job_post' AND column_name='questionlist') THEN
    EXECUTE 'UPDATE job_post SET question_list = COALESCE(question_list, questionlist)';
END IF;
  -- expirydate(date) → expiry_date(timestamp)
  IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='job_post' AND column_name='expirydate') THEN
    EXECUTE 'UPDATE job_post SET expiry_date = COALESCE(expiry_date, expirydate::timestamp) WHERE expiry_date IS NULL';
END IF;
END$$;

-- start_date가 비어있으면 created_at로 보정
UPDATE job_post SET start_date = COALESCE(start_date, created_at) WHERE start_date IS NULL;

-- careerRequirement(VARCHAR) → BOOLEAN 유추
DO $$
BEGIN
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
END$$;

-- 낡은 컬럼 제거
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

-- NOT NULL 제약(데이터에 NULL 있으면 실패함)
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

-- FK(기존 employerid FK 있었을 수 있으니 먼저 제거 후 새로 추가)
DO $$
DECLARE
c_name text;
BEGIN
FOR c_name IN
SELECT conname
FROM   pg_constraint
WHERE  conrelid = 'job_post'::regclass
    AND    contype = 'f'
    AND    conkey  @> ARRAY[
      (SELECT attnum FROM pg_attribute WHERE attrelid='job_post'::regclass AND attname='employer_id')
    ]::smallint[]
  LOOP
    -- 이미 employer_id에 대한 FK가 있으면 제거하고 새 이름으로 재생성
    EXECUTE format('ALTER TABLE job_post DROP CONSTRAINT %I', c_name);
END LOOP;
  -- 새 FK 추가(없으면)
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

DO $$
BEGIN
  IF EXISTS (
    SELECT 1 FROM pg_constraint
    WHERE conrelid = 'job_post'::regclass AND conname='chk_jobpost_questionlist_json'
  ) THEN
    EXECUTE 'ALTER TABLE job_post DROP CONSTRAINT chk_jobpost_questionlist_json';
END IF;
EXECUTE 'ALTER TABLE job_post
           ADD CONSTRAINT chk_jobpost_questionlist_json
           CHECK (question_list IS NULL OR jsonb_typeof(question_list) = ''array'')';
END$$;

-- 컬렉션 테이블들 리네임/보정
-- jobPostWorkDays → job_post_work_days
DO $$
BEGIN
  IF to_regclass('public.job_post_work_days') IS NULL AND to_regclass('public.jobpostworkdays') IS NOT NULL THEN
    EXECUTE 'ALTER TABLE jobpostworkdays RENAME TO job_post_work_days';
END IF;
END$$;

DO $$
BEGIN
  IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='job_post_work_days' AND column_name='jobpostid') THEN
    EXECUTE 'ALTER TABLE job_post_work_days RENAME COLUMN jobpostid TO job_post_id';
END IF;
  IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='job_post_work_days' AND column_name='workday') THEN
    EXECUTE 'ALTER TABLE job_post_work_days RENAME COLUMN workday TO work_day';
END IF;
END$$;

DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname='fk_job_post_work_days_post') THEN
    EXECUTE 'ALTER TABLE job_post_work_days
             ADD CONSTRAINT fk_job_post_work_days_post
             FOREIGN KEY (job_post_id) REFERENCES job_post(id) ON DELETE CASCADE';
END IF;
END$$;

-- jobPostApplyMethods → job_post_apply_methods
DO $$
BEGIN
  IF to_regclass('public.job_post_apply_methods') IS NULL AND to_regclass('public.jobpostapplymethods') IS NOT NULL THEN
    EXECUTE 'ALTER TABLE jobpostapplymethods RENAME TO job_post_apply_methods';
END IF;
END$$;

DO $$
BEGIN
  IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='job_post_apply_methods' AND column_name='jobpostid') THEN
    EXECUTE 'ALTER TABLE job_post_apply_methods RENAME COLUMN jobpostid TO job_post_id';
END IF;
  IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='job_post_apply_methods' AND column_name='applymethod') THEN
    EXECUTE 'ALTER TABLE job_post_apply_methods RENAME COLUMN applymethod TO apply_method';
END IF;
END$$;

DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname='fk_job_post_apply_methods_post') THEN
    EXECUTE 'ALTER TABLE job_post_apply_methods
             ADD CONSTRAINT fk_job_post_apply_methods_post
             FOREIGN KEY (job_post_id) REFERENCES job_post(id) ON DELETE CASCADE';
END IF;
END$$;

-- jobPostJobFields → job_post_job_fields  (현재 도메인 필드명이 Set<ApplyMethod> applyMethod 라서 값 컬럼을 apply_method로 맞춤)
DO $$
BEGIN
  IF to_regclass('public.job_post_job_fields') IS NULL AND to_regclass('public.jobpostjobfields') IS NOT NULL THEN
    EXECUTE 'ALTER TABLE jobpostjobfields RENAME TO job_post_job_fields';
END IF;
END$$;

DO $$
BEGIN
  IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='job_post_job_fields' AND column_name='jobpostid') THEN
    EXECUTE 'ALTER TABLE job_post_job_fields RENAME COLUMN jobpostid TO job_post_id';
END IF;
  IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='job_post_job_fields' AND column_name='jobfield') THEN
    EXECUTE 'ALTER TABLE job_post_job_fields RENAME COLUMN jobfield TO apply_method';
END IF;
END$$;

DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname='fk_job_post_job_fields_post') THEN
    EXECUTE 'ALTER TABLE job_post_job_fields
             ADD CONSTRAINT fk_job_post_job_fields_post
             FOREIGN KEY (job_post_id) REFERENCES job_post(id) ON DELETE CASCADE';
END IF;
END$$;

-------------------------------
-- 5) application 정리
-------------------------------
DO $$
BEGIN
  IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='application' AND column_name='jobpostid') THEN
    EXECUTE 'ALTER TABLE application RENAME COLUMN jobpostid TO job_post_id';
END IF;
  IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='application' AND column_name='workerid') THEN
    EXECUTE 'ALTER TABLE application RENAME COLUMN workerid TO worker_id';
END IF;
  IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='application' AND column_name='applicationstatus') THEN
    EXECUTE 'ALTER TABLE application RENAME COLUMN applicationstatus TO application_status';
END IF;
  IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='application' AND column_name='applymethod') THEN
    EXECUTE 'ALTER TABLE application RENAME COLUMN applymethod TO apply_method';
END IF;
  IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='application' AND column_name='submissiontime') THEN
    EXECUTE 'ALTER TABLE application RENAME COLUMN submissiontime TO submission_time';
END IF;
END$$;

-- 유니크키/인덱스/체크
DO $$
BEGIN
  -- 기존 복합 유니크 제약 제거(있으면)
  IF EXISTS (
    SELECT 1
    FROM   pg_constraint
    WHERE  conrelid='application'::regclass
    AND    conname='application_jobpostid_workerid_key'
  ) THEN
    EXECUTE 'ALTER TABLE application DROP CONSTRAINT application_jobpostid_workerid_key';
END IF;

  -- 새 유니크
  IF NOT EXISTS (
    SELECT 1 FROM pg_constraint WHERE conname='ux_application_jobpost_worker'
  ) THEN
    EXECUTE 'ALTER TABLE application ADD CONSTRAINT ux_application_jobpost_worker UNIQUE(job_post_id, worker_id)';
END IF;
END$$;

CREATE INDEX IF NOT EXISTS idx_application_job_post ON application(job_post_id);
CREATE INDEX IF NOT EXISTS idx_application_worker   ON application(worker_id);

DO $$
BEGIN
  IF EXISTS (
    SELECT 1 FROM pg_constraint
    WHERE conrelid = 'application'::regclass AND conname='chk_application_answers_json'
  ) THEN
    EXECUTE 'ALTER TABLE application DROP CONSTRAINT chk_application_answers_json';
END IF;
EXECUTE 'ALTER TABLE application
           ADD CONSTRAINT chk_application_answers_json
           CHECK (answers IS NULL OR jsonb_typeof(answers) IN (''array'',''object''))';
END$$;

-------------------------------
-- 6) job 정리
-------------------------------
DO $$
BEGIN
  IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='job' AND column_name='applicationid') THEN
    EXECUTE 'ALTER TABLE job RENAME COLUMN applicationid TO application_id';
END IF;
  IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='job' AND column_name='workerid') THEN
    EXECUTE 'ALTER TABLE job RENAME COLUMN workerid TO worker_id';
END IF;
END$$;

-- FK/인덱스
DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname='fk_job_application') THEN
    EXECUTE 'ALTER TABLE job ADD CONSTRAINT fk_job_application
             FOREIGN KEY (application_id) REFERENCES application(id)';
END IF;
END$$;

CREATE UNIQUE INDEX IF NOT EXISTS ux_job_application ON job(application_id);
CREATE INDEX IF NOT EXISTS idx_job_worker ON job(worker_id);

-------------------------------
-- 7) review 및 컬렉션 정리
-------------------------------
DO $$
BEGIN
  IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='review' AND column_name='workerid') THEN
    EXECUTE 'ALTER TABLE review RENAME COLUMN workerid TO worker_id';
END IF;
  IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='review' AND column_name='employerid') THEN
    EXECUTE 'ALTER TABLE review RENAME COLUMN employerid TO employer_id';
END IF;
END$$;

DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname='fk_review_worker') THEN
    EXECUTE 'ALTER TABLE review ADD CONSTRAINT fk_review_worker
             FOREIGN KEY (worker_id) REFERENCES worker(id)';
END IF;
  IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname='fk_review_employer') THEN
    EXECUTE 'ALTER TABLE review ADD CONSTRAINT fk_review_employer
             FOREIGN KEY (employer_id) REFERENCES employer(id)';
END IF;
END$$;

CREATE INDEX IF NOT EXISTS idx_review_worker   ON review(worker_id);
CREATE INDEX IF NOT EXISTS idx_review_employer ON review(employer_id);

-- reviewHashtags → review_hashtags
DO $$
BEGIN
  IF to_regclass('public.review_hashtags') IS NULL AND to_regclass('public.reviewhashtags') IS NOT NULL THEN
    EXECUTE 'ALTER TABLE reviewhashtags RENAME TO review_hashtags';
END IF;
END$$;

DO $$
BEGIN
  IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='review_hashtags' AND column_name='reviewid') THEN
    EXECUTE 'ALTER TABLE review_hashtags RENAME COLUMN reviewid TO review_id';
END IF;
END$$;

DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname='fk_review_hashtags_review') THEN
    EXECUTE 'ALTER TABLE review_hashtags ADD CONSTRAINT fk_review_hashtags_review
             FOREIGN KEY (review_id) REFERENCES review(id) ON DELETE CASCADE';
END IF;
END$$;

-- reviewAnswers → review_answers
DO $$
BEGIN
  IF to_regclass('public.review_answers') IS NULL AND to_regclass('public.reviewanswers') IS NOT NULL THEN
    EXECUTE 'ALTER TABLE reviewanswers RENAME TO review_answers';
END IF;
END$$;

DO $$
BEGIN
  IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='review_answers' AND column_name='reviewid') THEN
    EXECUTE 'ALTER TABLE review_answers RENAME COLUMN reviewid TO review_id';
END IF;
  IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='review_answers' AND column_name='evaluationtype') THEN
    EXECUTE 'ALTER TABLE review_answers RENAME COLUMN evaluationtype TO evaluation_type';
END IF;
END$$;

DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname='fk_review_answers_review') THEN
    EXECUTE 'ALTER TABLE review_answers ADD CONSTRAINT fk_review_answers_review
             FOREIGN KEY (review_id) REFERENCES review(id) ON DELETE CASCADE';
END IF;
END$$;

-- 끝.
