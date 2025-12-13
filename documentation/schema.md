CREATE TABLE users (
    id INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE tasklist (
    id INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description LONGTEXT NOT NULL,
    status ENUM('pending', 'completed', 'postponed') NOT NULL,
    due_date DATE NOT NULL,
    priority ENUM('Low', 'Medium', 'High') NOT NULL,
    user_id INT,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE goals (
    id INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
    statement VARCHAR(255) NOT NULL,
    status ENUM('completed', 'pending') NOT NULL,
    user_id INT,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE timers (
    id INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
    duration TIME NOT NULL,
    status ENUM('running', 'completed') NOT NULL,
    user_id INT,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE notifications (
    id INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
    text VARCHAR(255) NOT NULL,
    user_id INT,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);