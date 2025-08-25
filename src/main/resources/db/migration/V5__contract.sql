ALTER TABLE job
    ALTER COLUMN contract_core DROP DEFAULT,
    ALTER COLUMN contract_core TYPE text USING contract_core::text,
    ALTER COLUMN contract_core SET DEFAULT '{}',
    ALTER COLUMN contract_core SET NOT NULL;
