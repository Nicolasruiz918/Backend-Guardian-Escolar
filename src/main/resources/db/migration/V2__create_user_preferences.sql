CREATE TABLE user_preferences (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE,
    language VARCHAR(10) NOT NULL DEFAULT 'es',
    timezone VARCHAR(60) NOT NULL DEFAULT 'America/Bogota',
    push_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    email_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    sms_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    location_alerts_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    route_alerts_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    safe_zone_alerts_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_user_preferences_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
