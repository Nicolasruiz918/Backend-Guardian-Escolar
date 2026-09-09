CREATE TABLE users (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(160) NOT NULL,
    phone VARCHAR(40),
    role VARCHAR(30) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    two_factor_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    two_factor_method VARCHAR(20) NOT NULL DEFAULT 'EMAIL'
);

CREATE TABLE refresh_tokens (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    user_id UUID NOT NULL,
    token_hash VARCHAR(128) NOT NULL UNIQUE,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    revoked_at TIMESTAMP WITH TIME ZONE,
    CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens(user_id);
CREATE INDEX idx_refresh_tokens_token_hash ON refresh_tokens(token_hash);

CREATE TABLE verification_codes (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    user_id UUID NOT NULL,
    type VARCHAR(30) NOT NULL,
    contact VARCHAR(255) NOT NULL,
    code_hash VARCHAR(128) NOT NULL,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    attempts INT NOT NULL DEFAULT 0,
    max_attempts INT NOT NULL,
    used_at TIMESTAMP WITH TIME ZONE,
    reset_token_hash VARCHAR(128),
    reset_token_expires_at TIMESTAMP WITH TIME ZONE,
    CONSTRAINT fk_verification_codes_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE INDEX idx_verification_codes_user_type ON verification_codes(user_id, type);
CREATE INDEX idx_verification_codes_reset_token_hash ON verification_codes(reset_token_hash);

CREATE TABLE two_factor_challenges (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    user_id UUID NOT NULL,
    method VARCHAR(20) NOT NULL,
    code_hash VARCHAR(128) NOT NULL,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    attempts INT NOT NULL DEFAULT 0,
    max_attempts INT NOT NULL,
    used_at TIMESTAMP WITH TIME ZONE,
    CONSTRAINT fk_two_factor_challenges_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE INDEX idx_two_factor_challenges_user_id ON two_factor_challenges(user_id);
