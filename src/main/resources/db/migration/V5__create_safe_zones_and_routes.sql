CREATE TABLE safe_zones (
    id UUID PRIMARY KEY,
    student_id UUID NOT NULL,
    name VARCHAR(120) NOT NULL,
    center_latitude DOUBLE PRECISION NOT NULL,
    center_longitude DOUBLE PRECISION NOT NULL,
    radius_meters DOUBLE PRECISION NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_safe_zones_student FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE
);

CREATE INDEX idx_safe_zones_student_id ON safe_zones(student_id);

CREATE TABLE routes (
    id UUID PRIMARY KEY,
    student_id UUID NOT NULL,
    name VARCHAR(120) NOT NULL,
    origin_name VARCHAR(160),
    destination_name VARCHAR(160),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_routes_student FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE
);

CREATE INDEX idx_routes_student_id ON routes(student_id);

CREATE TABLE route_points (
    id UUID PRIMARY KEY,
    route_id UUID NOT NULL,
    sequence_number INTEGER NOT NULL,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_route_points_route FOREIGN KEY (route_id) REFERENCES routes(id) ON DELETE CASCADE
);

CREATE INDEX idx_route_points_route_sequence ON route_points(route_id, sequence_number);
