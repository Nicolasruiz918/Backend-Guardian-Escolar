CREATE TABLE student_devices (
    id UUID PRIMARY KEY,
    student_id UUID NOT NULL,
    name VARCHAR(120) NOT NULL,
    identifier VARCHAR(120) NOT NULL UNIQUE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    last_seen_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_student_devices_student FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE
);

CREATE INDEX idx_student_devices_student_id ON student_devices(student_id);

CREATE TABLE locations (
    id UUID PRIMARY KEY,
    student_id UUID NOT NULL,
    device_id UUID,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    accuracy DOUBLE PRECISION,
    speed DOUBLE PRECISION,
    battery_level INTEGER,
    recorded_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_locations_student FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    CONSTRAINT fk_locations_device FOREIGN KEY (device_id) REFERENCES student_devices(id) ON DELETE SET NULL
);

CREATE INDEX idx_locations_student_recorded ON locations(student_id, recorded_at DESC);
