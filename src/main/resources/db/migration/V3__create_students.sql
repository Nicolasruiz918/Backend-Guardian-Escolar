CREATE TABLE students (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    full_name VARCHAR(160) NOT NULL,
    grade VARCHAR(80),
    age INTEGER,
    school VARCHAR(160),
    avatar_url VARCHAR(500),
    emergency_contact_name VARCHAR(160),
    emergency_contact_phone VARCHAR(40),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_students_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_students_user_id ON students(user_id);
