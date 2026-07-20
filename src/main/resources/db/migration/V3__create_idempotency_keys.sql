CREATE TABLE idempotency_keys (
    id                  BIGSERIAL      PRIMARY KEY,
    subject             VARCHAR(255)   NOT NULL,
    operation           VARCHAR(50)    NOT NULL,
    idempotency_key     VARCHAR(255)   NOT NULL,
    request_hash        VARCHAR(64)    NOT NULL,
    resource_id         BIGINT         NOT NULL,
    response_status     INTEGER        NOT NULL,
    response_body       JSONB          NOT NULL,
    created_at          TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at          TIMESTAMP      NOT NULL,
    CONSTRAINT uq_idempotency_keys_subject_operation_key UNIQUE (subject, operation, idempotency_key)
);

CREATE INDEX idx_idempotency_keys_expires_at ON idempotency_keys (expires_at);
