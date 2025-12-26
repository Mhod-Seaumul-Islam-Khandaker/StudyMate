CREATE TYPE task_status_enum AS ENUM ('pending', 'completed', 'postponed');
CREATE TYPE priority_enum AS ENUM ('Low', 'Medium', 'High');
CREATE TYPE goal_status_enum AS ENUM ('completed', 'pending');
CREATE TYPE timer_status_enum AS ENUM ('running', 'completed');

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE tasklist (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    status task_status_enum NOT NULL,
    due_date DATE NOT NULL,
    priority priority_enum NOT NULL,
    user_id INT,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE goals (
    id SERIAL PRIMARY KEY,
    statement VARCHAR(255) NOT NULL,
    status goal_status_enum NOT NULL,
    user_id INT,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE timers (
    id SERIAL PRIMARY KEY,
    duration TIME NOT NULL,
    status timer_status_enum NOT NULL,
    user_id INT,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE notifications (
    id SERIAL PRIMARY KEY,
    text VARCHAR(255) NOT NULL,
    user_id INT,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
