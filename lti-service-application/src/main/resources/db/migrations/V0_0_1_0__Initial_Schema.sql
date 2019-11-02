CREATE TABLE users
(
    id       BIGSERIAL PRIMARY KEY NOT NULL,
    username VARCHAR(64)           NOT NULL,
    active   BOOLEAN               NOT NULL DEFAULT TRUE
);

CREATE UNIQUE INDEX users_username_unique_index
    ON users (username);

CREATE TABLE user_credentials
(
    id              BIGSERIAL PRIMARY KEY NOT NULL,
    user_id         BIGINT                NOT NULL,
    hashed_password VARCHAR               NOT NULL,
    created_at      TIMESTAMP             NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE tool_deployments
(
    id                           UUID PRIMARY KEY NOT NULL,
    deployment_id                VARCHAR,
    client_id                    VARCHAR,
    issuer                       VARCHAR,
    oidc_authentication_endpoint VARCHAR,
    jwks_endpoint                VARCHAR,
    private_key                  VARCHAR,
    signature_algorithm          VARCHAR
);

CREATE TABLE exam_takings
(
    id                 UUID PRIMARY KEY NOT NULL,
    exam_id            BIGINT           NOT NULL,
    subject            VARCHAR,
    line_item_url      VARCHAR,
    tool_deployment_id UUID,
    FOREIGN KEY (tool_deployment_id) REFERENCES tool_deployments (id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE UNIQUE INDEX tool_deployments_issuer_client_id_deployment_id_unique_index
    ON tool_deployments (issuer, client_id, deployment_id);

CREATE INDEX tool_deployments_issuer_client_id_index
    ON tool_deployments (issuer, client_id);

CREATE INDEX tool_deployments_issuer_index
    ON tool_deployments (issuer);

CREATE INDEX exam_takings_exam_id_subject_unique_index
    ON exam_takings (exam_id, subject);
