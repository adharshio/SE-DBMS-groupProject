CREATE DATABASE IF NOT EXISTS civicpulse;
USE civicpulse;
 
CREATE TABLE users (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    name        VARCHAR(100) NOT NULL,
    email       VARCHAR(100) UNIQUE NOT NULL,
    password    VARCHAR(255) NOT NULL,
    phone       VARCHAR(20),
    citizen_id  VARCHAR(20) UNIQUE,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
 
CREATE TABLE complaints (
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    complaint_ref VARCHAR(25) UNIQUE,
    user_id       BIGINT,
    category      VARCHAR(50) NOT NULL,
    title         VARCHAR(200) NOT NULL,
    description   TEXT,
    status        ENUM('SUBMITTED','ASSIGNED','IN_PROGRESS','RESOLVED') DEFAULT 'SUBMITTED',
    priority      ENUM('HIGH','MEDIUM','LOW') DEFAULT 'MEDIUM',
    latitude      DECIMAL(10,8),
    longitude     DECIMAL(11,8),
    location_name VARCHAR(200),
    image_url     VARCHAR(500),
    assigned_dept VARCHAR(100),
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
 
CREATE TABLE complaint_timeline (
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    complaint_id BIGINT,
    status       VARCHAR(50),
    note         TEXT,
    updated_by   VARCHAR(100),
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (complaint_id) REFERENCES complaints(id)
);
 
CREATE TABLE notifications (
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id      BIGINT,
    title        VARCHAR(200),
    body         TEXT,
    is_read      BOOLEAN DEFAULT FALSE,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
