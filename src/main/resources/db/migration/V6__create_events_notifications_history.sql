CREATE TABLE guardian_events (
    id UUID PRIMARY KEY,
    student_id UUID NOT NULL,
    location_id UUID,
    type VARCHAR(60) NOT NULL,
    message VARCHAR(500) NOT NULL,
    severity VARCHAR(20) NOT NULL,
    occurred_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_events_student FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    CONSTRAINT fk_events_location FOREIGN KEY (location_id) REFERENCES locations(id) ON DELETE SET NULL
);

CREATE INDEX idx_events_student_occurred ON guardian_events(student_id, occurred_at DESC);

CREATE TABLE notifications (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    student_id UUID,
    event_id UUID,
    title VARCHAR(160) NOT NULL,
    body VARCHAR(600) NOT NULL,
    type VARCHAR(60) NOT NULL,
    read_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_notifications_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_notifications_student FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    CONSTRAINT fk_notifications_event FOREIGN KEY (event_id) REFERENCES guardian_events(id) ON DELETE SET NULL
);

CREATE INDEX idx_notifications_user_created ON notifications(user_id, created_at DESC);

CREATE TABLE journey_history (
    id UUID PRIMARY KEY,
    student_id UUID NOT NULL,
    location_id UUID,
    event_id UUID,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    recorded_at TIMESTAMP NOT NULL,
    status VARCHAR(60) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_history_student FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    CONSTRAINT fk_history_location FOREIGN KEY (location_id) REFERENCES locations(id) ON DELETE SET NULL,
    CONSTRAINT fk_history_event FOREIGN KEY (event_id) REFERENCES guardian_events(id) ON DELETE SET NULL
);

CREATE INDEX idx_history_student_recorded ON journey_history(student_id, recorded_at DESC);
