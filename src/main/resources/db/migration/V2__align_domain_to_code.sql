-- ==========================================
-- V2__align_domain_to_code.sql  (PostgreSQL, Flyway)
-- - camelCase → snake_case 정리
-- - FK/인덱스/제약 보정
-- - 도메인 코드(JPA) 컬럼/타입에 맞춤
-- ==========================================

------------------------------------------------------------
-- 0) 유틸: 안전 드롭용 함수 (제약 이름 불확실한 경우 대비)
------------------------------------------------------------
DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_proc WHERE proname = 'drop_constraint_if_exists') THEN
    CREATE OR REPLACE FUNCTION drop_constraint_if_exists(tbl regclass, cname text)
    RETURNS void AS $f$
BEGIN
EXECUTE format('ALTER TABLE %s DROP CONSTRAINT IF EXISTS %I', tbl, cname);
END
    $f$ LANGUAGE plpgsql;
END IF;
END$$;


------------------------------------------------------------
-- 1) employer 테이블 정리
------------------------------------------------------------
-- camelCase → snake_case
ALTER TABLE IF EXISTS employer
    RENAME COLUMN IF EXISTS bossname           TO boss_name;
ALTER TABLE IF EXISTS employer
    RENAME COLUMN IF EXISTS phonenumber        TO phone_number;
ALTER TABLE IF EXISTS employer
    RENAME COLUMN IF EXISTS companyname        TO company_name;
ALTER TABLE IF EXISTS employer
    RENAME COLUMN IF EXISTS companylocation    TO company_location;
ALTER TABLE IF EXISTS employer
    RENAME COLUMN IF EXISTS companynumber      TO company_number;
ALTER TABLE IF EXISTS employer
    RENAME COLUMN IF EXISTS defaultquestionlist TO default_question_list;

-- nullable/unique 보정(도메인 어노테이션 반영: NOT NULL & UNIQUE)
ALTER TABLE employer
    ALTER COLUMN name               SET NOT NULL,
ALTER COLUMN email              SET NOT NULL,
  ALTER COLUMN boss_name          SET NOT NULL,
  ALTER COLUMN phone_number       SET NOT NULL,
  ALTER COLUMN company_name       SET NOT NULL,
  ALTER COLUMN company_location   SET NOT NULL,
  ALTER COLUMN company_number     SET NOT NULL;

-- 유니크 인덱스(없으면 생성)
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

-- employer JobField(컬렉션) 테이블 리네임 + 컬럼 정리
-- V1: employerJobField(employerId, jobField)
-- 물리명: employerjobfield / employerid / jobfield
DO $$
BEGIN
  IF to_regclass('public.employer_job_field') IS NULL AND to_regclass('public.employerjobfield') IS NOT NULL THEN
    EXECUTE 'ALTER TABLE employerjobfield RENAME TO employer_job_field';
END IF;
END$$;

ALTER TABLE IF EXISTS employer_job_field
    RENAME COLUMN IF EXISTS employerid TO employer_id;
ALTER TABLE IF EXISTS employer_job_field
    RENAME COLUMN IF EXISTS jobfield   TO job_field;

-- FK 재정의
SELECT drop_constraint_if_exists('employer_job_field','employerjobfield_employerid_fkey');
ALTER TABLE IF EXISTS employer_job_field
    ADD CONSTRAINT IF NOT EXISTS fk_employer_job_field_employer
    FOREIGN KEY (employer_id) REFERENCES employer(id) ON DELETE CASCADE;


------------------------------------------------------------
-- 2) worker 테이블 & 컬렉션 정리
------------------------------------------------------------
-- V1의 컬렉션: workerBlgs(workerId, blg), workerJobInterests(workerId, jobField)
-- 물리명: workerblgs / workerid / blg
DO $$
BEGIN
  IF to_regclass('public.worker_blg') IS NULL AND to_regclass('public.workerblgs') IS NOT NULL THEN
    EXECUTE 'ALTER TABLE workerblgs RENAME TO worker_blg';
END IF;
END$$;

ALTER TABLE IF EXISTS worker_blg
    RENAME COLUMN IF EXISTS workerid TO worker_id;

DO $$
BEGIN
  IF to_regclass('public.worker_job_interests') IS NULL AND to_regclass('public.workerjobinterests') IS NOT NULL THEN
    EXECUTE 'ALTER TABLE workerjobinterests RENAME TO worker_job_interests';
END IF;
END$$;

ALTER TABLE IF EXISTS worker_job_interests
    RENAME COLUMN IF EXISTS workerid TO worker_id;
ALTER TABLE IF EXISTS worker_job_interests
    RENAME COLUMN IF EXISTS jobfield TO job_field;

-- worker 자체 컬럼은 V1이 이미 snake_case라 추가 조치 없음(필요시 여기에 보정 추가)


------------------------------------------------------------
-- 3) career 테이블 정리
------------------------------------------------------------
ALTER TABLE IF EXISTS career
    RENAME COLUMN IF EXISTS workerid      TO worker_id,
    RENAME COLUMN IF EXISTS mainduties    TO main_duties,
    RENAME COLUMN IF EXISTS companyname   TO company_name,
    RENAME COLUMN IF EXISTS workplace     TO workplace,          -- 동일명 유지(열거형 매핑 용도)
    RENAME COLUMN IF EXISTS jobfield      TO job_field,
    RENAME COLUMN IF EXISTS startdate     TO start_date,
    RENAME COLUMN IF EXISTS enddate       TO end_date,
    RENAME COLUMN IF EXISTS isopening     TO is_opening;

-- FK 재정의
SELECT drop_constraint_if_exists('career','career_workerid_fkey');
ALTER TABLE IF EXISTS career
    ADD CONSTRAINT IF NOT EXISTS fk_career_worker
    FOREIGN KEY (worker_id) REFERENCES worker(id) ON DELETE CASCADE;

CREATE INDEX IF NOT EXISTS idx_career_worker ON career(worker_id);


------------------------------------------------------------
-- 4) job_post 테이블 대정리 (핵심)
------------------------------------------------------------
-- 4-1) 카멜 → 스네이크 컬럼 생성/값 이관
-- 새 컬럼이 없으면 추가
ALTER TABLE IF EXISTS job_post
    ADD COLUMN IF NOT EXISTS employer_id         BIGINT,
    ADD COLUMN IF NOT EXISTS payment_type        VARCHAR(32),
    ADD COLUMN IF NOT EXISTS payment             BIGINT,
    ADD COLUMN IF NOT EXISTS location            TEXT,
    ADD COLUMN IF NOT EXISTS rest_time           INTEGER,
    ADD COLUMN IF NOT EXISTS work_type           VARCHAR(32),
    ADD COLUMN IF NOT EXISTS work_days_count     INTEGER,
    ADD COLUMN IF NOT EXISTS career_requirement  BOOLEAN,
    ADD COLUMN IF NOT EXISTS education_requirement VARCHAR(32),
    ADD COLUMN IF NOT EXISTS employment_type     VARCHAR(32),
    ADD COLUMN IF NOT EXISTS job_field           VARCHAR(64),
    ADD COLUMN IF NOT EXISTS start_date          TIMESTAMP,
    ADD COLUMN IF NOT EXISTS expiry_date         TIMESTAMP,
    ADD COLUMN IF NOT EXISTS work_place          VARCHAR(64),
    ADD COLUMN IF NOT EXISTS work_start_time     TIME,
    ADD COLUMN IF NOT EXISTS work_end_time       TIME,
    ADD COLUMN IF NOT EXISTS question_list       JSONB,
    ADD COLUMN IF NOT EXISTS save_question_list  BOOLEAN DEFAULT FALSE;

-- 기존 camelCase 컬럼에서 새 컬럼으로 값 이동 (존재할 때만)
UPDATE job_post SET
                    employer_id         = COALESCE(employer_id, employerid),
                    work_type           = COALESCE(work_type, worktype),
                    work_days_count     = COALESCE(work_days_count, workdayscount),
                    education_requirement = COALESCE(education_requirement, educationrequirement),
                    employment_type     = COALESCE(employment_type, employmenttype),
                    job_field           = COALESCE(job_field, jobfield),
                    work_place          = COALESCE(work_place, workplace),
                    work_start_time     = COALESCE(work_start_time, workstarttime),
                    work_end_time       = COALESCE(work_end_time, workendtime),
                    question_list       = COALESCE(question_list, questionlist)
WHERE TRUE;

-- 날짜/시간 타입 보정
-- V1의 expirydate(date) → expiry_date(timestamp), start_date가 없으면 created_at 사용
UPDATE job_post
SET expiry_date = COALESCE(expiry_date, expirydate::timestamp)
WHERE expiry_date IS NULL AND EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_name='job_post' AND column_name='expirydate'
);

UPDATE job_post
SET start_date = COALESCE(start_date, created_at)
WHERE start_date IS NULL;

-- careerRequirement(VARCHAR) → BOOLEAN 로 변환 시도
-- 'REQUIRED/TRUE/YES/1' => true, 'NONE/FALSE/NO/0' => false, 그 외/NULL은 false로
UPDATE job_post
SET career_requirement = COALESCE(
        career_requirement,
        CASE
            WHEN careerrequirement ~* '^(required|true|y|yes|1)$' THEN TRUE
            WHEN careerrequirement ~* '^(none|false|n|no|0)$'    THEN FALSE
            ELSE FALSE
            END
                         );

-- 4-2) 낡은 컬럼 드롭 (존재할 때만)
ALTER TABLE IF EXISTS job_post
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

-- 4-3) NOT NULL 제약 정리 (도메인 nullable=false 반영 + 기본값 보정)
UPDATE job_post SET save_question_list = COALESCE(save_question_list, FALSE);
ALTER TABLE job_post
    ALTER COLUMN title                SET NOT NULL,
ALTER COLUMN content              SET NOT NULL,
  ALTER COLUMN payment_type         SET NOT NULL,
  ALTER COLUMN payment              SET NOT NULL,
  ALTER COLUMN location             SET NOT NULL,
  ALTER COLUMN rest_time            SET NOT NULL,
  ALTER COLUMN work_type            SET NOT NULL,
  ALTER COLUMN career_requirement   SET NOT NULL,
  ALTER COLUMN job_field            SET NOT NULL,
  ALTER COLUMN start_date           SET NOT NULL,
  ALTER COLUMN expiry_date          SET NOT NULL,
  ALTER COLUMN status               SET NOT NULL,
  ALTER COLUMN work_start_time      SET NOT NULL,
  ALTER COLUMN work_end_time        SET NOT NULL,
  ALTER COLUMN education_requirement SET NOT NULL,
  ALTER COLUMN employment_type      SET NOT NULL,
  ALTER COLUMN work_place           SET NOT NULL,
  ALTER COLUMN save_question_list   SET NOT NULL,
  ALTER COLUMN employer_id          SET NOT NULL;

-- 4-4) FK/인덱스 재정의
SELECT drop_constraint_if_exists('job_post','job_post_employerid_fkey');
SELECT drop_constraint_if_exists('job_post','fk_job_post_employer');
ALTER TABLE job_post
    ADD CONSTRAINT IF NOT EXISTS fk_job_post_employer
    FOREIGN KEY (employer_id) REFERENCES employer(id) ON DELETE CASCADE;

-- 인덱스 재생성
DROP INDEX IF EXISTS idx_job_post_employer;
CREATE INDEX IF NOT EXISTS idx_job_post_employer ON job_post(employer_id);
DROP INDEX IF EXISTS idx_job_post_status_created;
CREATE INDEX IF NOT EXISTS idx_job_post_status_created ON job_post(status, created_at DESC);

-- 4-5) JSONB 제약 보정
SELECT drop_constraint_if_exists('job_post','chk_jobpost_questionlist_json');
ALTER TABLE job_post
    ADD CONSTRAINT chk_jobpost_questionlist_json
        CHECK (question_list IS NULL OR jsonb_typeof(question_list) = 'array');

-- 4-6) ElementCollection 테이블들 리네임 + 컬럼 보정
-- jobPostWorkDays(jobPostId, workDay) → job_post_work_days(job_post_id, work_day)
DO $$
BEGIN
  IF to_regclass('public.job_post_work_days') IS NULL AND to_regclass('public.jobpostworkdays') IS NOT NULL THEN
    EXECUTE 'ALTER TABLE jobpostworkdays RENAME TO job_post_work_days';
END IF;
END$$;

ALTER TABLE IF EXISTS job_post_work_days
    RENAME COLUMN IF EXISTS jobpostid TO job_post_id;
ALTER TABLE IF EXISTS job_post_work_days
    RENAME COLUMN IF EXISTS workday   TO work_day;

SELECT drop_constraint_if_exists('job_post_work_days','jobpostworkdays_jobpostid_fkey');
ALTER TABLE IF EXISTS job_post_work_days
    ADD CONSTRAINT IF NOT EXISTS fk_job_post_work_days_post
    FOREIGN KEY (job_post_id) REFERENCES job_post(id) ON DELETE CASCADE;

-- jobPostApplyMethods(jobPostId, applyMethod) → job_post_apply_methods(job_post_id, apply_method)
DO $$
BEGIN
  IF to_regclass('public.job_post_apply_methods') IS NULL AND to_regclass('public.jobpostapplymethods') IS NOT NULL THEN
    EXECUTE 'ALTER TABLE jobpostapplymethods RENAME TO job_post_apply_methods';
END IF;
END$$;

ALTER TABLE IF EXISTS job_post_apply_methods
    RENAME COLUMN IF EXISTS jobpostid   TO job_post_id;
ALTER TABLE IF EXISTS job_post_apply_methods
    RENAME COLUMN IF EXISTS applymethod TO apply_method;

SELECT drop_constraint_if_exists('job_post_apply_methods','jobpostapplymethods_jobpostid_fkey');
ALTER TABLE IF EXISTS job_post_apply_methods
    ADD CONSTRAINT IF NOT EXISTS fk_job_post_apply_methods_post
    FOREIGN KEY (job_post_id) REFERENCES job_post(id) ON DELETE CASCADE;

-- jobPostJobFields(jobPostId, jobField) → (도메인 현재 코드: 필드명이 applyMethod Set<ApplyMethod>로 선언되어 있음)
-- 테이블은 job_post_job_fields 로, 값 컬럼은 apply_method 로 맞춘다(현 도메인 코드에 호환)
DO $$
BEGIN
  IF to_regclass('public.job_post_job_fields') IS NULL AND to_regclass('public.jobpostjobfields') IS NOT NULL THEN
    EXECUTE 'ALTER TABLE jobpostjobfields RENAME TO job_post_job_fields';
END IF;
END$$;

ALTER TABLE IF EXISTS job_post_job_fields
    RENAME COLUMN IF EXISTS jobpostid TO job_post_id;
-- 값 컬럼이 'jobfield'로 존재하면 'apply_method'로 변경 (도메인 코드의 필드명에 맞춤)
DO $$
BEGIN
  IF EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_name='job_post_job_fields' AND column_name='jobfield'
  ) AND NOT EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_name='job_post_job_fields' AND column_name='apply_method'
  ) THEN
    EXECUTE 'ALTER TABLE job_post_job_fields RENAME COLUMN jobfield TO apply_method';
END IF;
END$$;

SELECT drop_constraint_if_exists('job_post_job_fields','jobpostjobfields_jobpostid_fkey');
ALTER TABLE IF EXISTS job_post_job_fields
    ADD CONSTRAINT IF NOT EXISTS fk_job_post_job_fields_post
    FOREIGN KEY (job_post_id) REFERENCES job_post(id) ON DELETE CASCADE;


------------------------------------------------------------
-- 5) application 테이블 정리
------------------------------------------------------------
ALTER TABLE IF EXISTS application
    RENAME COLUMN IF EXISTS jobpostid        TO job_post_id,
    RENAME COLUMN IF EXISTS workerid         TO worker_id,
    RENAME COLUMN IF EXISTS applicationstatus TO application_status,
    RENAME COLUMN IF EXISTS applymethod      TO apply_method,
    RENAME COLUMN IF EXISTS submissiontime   TO submission_time;

-- FK 재정의
SELECT drop_constraint_if_exists('application','application_jobpostid_fkey');
SELECT drop_constraint_if_exists('application','application_workerid_fkey');
ALTER TABLE IF EXISTS application
    ADD CONSTRAINT IF NOT EXISTS fk_application_job_post
    FOREIGN KEY (job_post_id) REFERENCES job_post(id),
    ADD CONSTRAINT IF NOT EXISTS fk_application_worker
    FOREIGN KEY (worker_id) REFERENCES worker(id);

-- 유니크키 보정
ALTER TABLE IF EXISTS application
DROP CONSTRAINT IF EXISTS application_jobpostid_workerid_key,
  ADD CONSTRAINT IF NOT EXISTS ux_application_jobpost_worker UNIQUE(job_post_id, worker_id);

-- JSON 제약
SELECT drop_constraint_if_exists('application','chk_application_answers_json');
ALTER TABLE application
    ADD CONSTRAINT chk_application_answers_json
        CHECK (answers IS NULL OR jsonb_typeof(answers) IN ('array','object'));

CREATE INDEX IF NOT EXISTS idx_application_job_post ON application(job_post_id);
CREATE INDEX IF NOT EXISTS idx_application_worker   ON application(worker_id);


------------------------------------------------------------
-- 6) job 테이블 정리
------------------------------------------------------------
ALTER TABLE IF EXISTS job
    RENAME COLUMN IF EXISTS applicationid TO application_id,
    RENAME COLUMN IF EXISTS workerid      TO worker_id;

-- 제약 재정의 (application_id UNIQUE + FK)
SELECT drop_constraint_if_exists('job','job_applicationid_fkey');
ALTER TABLE IF EXISTS job
    ADD CONSTRAINT IF NOT EXISTS fk_job_application
    FOREIGN KEY (application_id) REFERENCES application(id);

CREATE UNIQUE INDEX IF NOT EXISTS ux_job_application ON job(application_id);
CREATE INDEX IF NOT EXISTS idx_job_worker ON job(worker_id);


------------------------------------------------------------
-- 7) review 및 컬렉션 정리
------------------------------------------------------------
ALTER TABLE IF EXISTS review
    RENAME COLUMN IF EXISTS workerid   TO worker_id,
    RENAME COLUMN IF EXISTS employerid TO employer_id;

-- FK 보정
SELECT drop_constraint_if_exists('review','review_workerid_fkey');
SELECT drop_constraint_if_exists('review','review_employerid_fkey');
ALTER TABLE IF EXISTS review
    ADD CONSTRAINT IF NOT EXISTS fk_review_worker
    FOREIGN KEY (worker_id) REFERENCES worker(id),
    ADD CONSTRAINT IF NOT EXISTS fk_review_employer
    FOREIGN KEY (employer_id) REFERENCES employer(id);

CREATE INDEX IF NOT EXISTS idx_review_worker   ON review(worker_id);
CREATE INDEX IF NOT EXISTS idx_review_employer ON review(employer_id);

-- reviewHashtags(reviewId, hashtag) → review_hashtags(review_id, hashtag)
DO $$
BEGIN
  IF to_regclass('public.review_hashtags') IS NULL AND to_regclass('public.reviewhashtags') IS NOT NULL THEN
    EXECUTE 'ALTER TABLE reviewhashtags RENAME TO review_hashtags';
END IF;
END$$;
ALTER TABLE IF EXISTS review_hashtags
    RENAME COLUMN IF EXISTS reviewid TO review_id;

SELECT drop_constraint_if_exists('review_hashtags','reviewhashtags_reviewid_fkey');
ALTER TABLE IF EXISTS review_hashtags
    ADD CONSTRAINT IF NOT EXISTS fk_review_hashtags_review
    FOREIGN KEY (review_id) REFERENCES review(id) ON DELETE CASCADE;

-- reviewAnswers(reviewId, evaluationType, answer) → review_answers(...)
DO $$
BEGIN
  IF to_regclass('public.review_answers') IS NULL AND to_regclass('public.reviewanswers') IS NOT NULL THEN
    EXECUTE 'ALTER TABLE reviewanswers RENAME TO review_answers';
END IF;
END$$;
ALTER TABLE IF EXISTS review_answers
    RENAME COLUMN IF EXISTS reviewid       TO review_id,
    RENAME COLUMN IF EXISTS evaluationtype TO evaluation_type;

SELECT drop_constraint_if_exists('review_answers','reviewanswers_reviewid_fkey');
ALTER TABLE IF EXISTS review_answers
    ADD CONSTRAINT IF NOT EXISTS fk_review_answers_review
    FOREIGN KEY (review_id) REFERENCES review(id) ON DELETE CASCADE;


------------------------------------------------------------
-- 8) 마무리: created_at/updated_at, 기타 공통은 V1 유지
-- 필요 시 추가 보정은 여기에 이어서 작성
------------------------------------------------------------
