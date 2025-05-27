CREATE TABLE IF NOT EXISTS halls (
    id SERIAL PRIMARY KEY,
    fair_id INTEGER NOT NULL,
    hall_id INTEGER NOT NULL,
    hall_name VARCHAR(255),
    available_square_meters INTEGER NOT NULL,
    UNIQUE(fair_id, hall_id) 
);

-- Insert some sample data
INSERT INTO halls (fair_id, hall_id, hall_name, available_square_meters) VALUES
(1, 1, 'Hall A', 45) ON CONFLICT (fair_id, hall_id) DO NOTHING;

INSERT INTO halls (fair_id, hall_id, hall_name, available_square_meters) VALUES
(1, 2, 'Hall B', 75) ON CONFLICT (fair_id, hall_id) DO NOTHING;

INSERT INTO halls (fair_id, hall_id, hall_name, available_square_meters) VALUES
(1, 3, 'Hall C', 100) ON CONFLICT (fair_id, hall_id) DO NOTHING;

INSERT INTO halls (fair_id, hall_id, hall_name, available_square_meters) VALUES
(2, 1, 'Hall A', 0) ON CONFLICT (fair_id, hall_id) DO NOTHING;

INSERT INTO halls (fair_id, hall_id, hall_name, available_square_meters) VALUES
(2, 2, 'Hall B', 0) ON CONFLICT (fair_id, hall_id) DO NOTHING;

INSERT INTO halls (fair_id, hall_id, hall_name, available_square_meters) VALUES
(1, 3, 'Hall C', 100) ON CONFLICT (fair_id, hall_id) DO NOTHING;

INSERT INTO halls (fair_id, hall_id, hall_name, available_square_meters) VALUES
(3, 1, 'Hall A', 0) ON CONFLICT (fair_id, hall_id) DO NOTHING;

INSERT INTO halls (fair_id, hall_id, hall_name, available_square_meters) VALUES
(3, 2, 'Hall B', 0) ON CONFLICT (fair_id, hall_id) DO NOTHING;

INSERT INTO halls (fair_id, hall_id, hall_name, available_square_meters) VALUES
(3, 3, 'Hall C', 0) ON CONFLICT (fair_id, hall_id) DO NOTHING;