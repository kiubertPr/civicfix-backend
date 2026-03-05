DROP TABLE IF EXISTS PointTransactions;
DROP TABLE IF EXISTS survey_response_options;
Drop TABLE IF EXISTS SurveyResponses;
DROP TABLE IF EXISTS survey_options;
DROP TABLE IF EXISTS Surveys;
Drop TABLE IF EXISTS PostVotes;
DROP TABLE IF EXISTS Posts_files;
DROP TABLE IF EXISTS Posts_images;
DROP TABLE IF EXISTS related_posts;
DROP TABLE IF EXISTS Posts;
DROP TABLE IF EXISTS Users;

CREATE TABLE Users (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(60) NOT NULL,
    password VARCHAR(60) NOT NULL, 
    firstName VARCHAR(60) NOT NULL,
    lastName VARCHAR(60), 
    email VARCHAR(60) NOT NULL,
    avatar VARCHAR(255) NOT NULL,
    avatarId VARCHAR(255),
    role TINYINT NOT NULL,
    provider VARCHAR(50) NOT NULL,
    googleId VARCHAR(255)
);


CREATE TABLE Posts (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(120) NOT NULL,
    content TEXT NOT NULL,
    location VARCHAR(120) NOT NULL,
    latitude DECIMAL(9, 6),
    longitude DECIMAL(9, 6),
    date TIMESTAMP,
    category ENUM('USER', 'ADMINISTRATION') NOT NULL,
    userId BIGINT NOT NULL,
    solved TINYINT NOT NULL DEFAULT 0,
    FOREIGN KEY (userId) REFERENCES Users(id) ON DELETE CASCADE
);

CREATE TABLE related_posts (
    postId BIGINT NOT NULL,
    id BIGINT,
    title VARCHAR(255),
    PRIMARY KEY (postId, id),
    FOREIGN KEY (postId) REFERENCES posts(id)
);

CREATE TABLE posts_images (
    postsId BIGINT NOT NULL,
    publicId VARCHAR(255) NOT NULL,
    url VARCHAR(512),
    PRIMARY KEY (postsId, publicId),
    FOREIGN KEY (postsId) REFERENCES Posts(id) ON DELETE CASCADE
);

CREATE TABLE posts_files (
    postsId BIGINT NOT NULL,
    publicId VARCHAR(255) NOT NULL,
    url VARCHAR(512),
    PRIMARY KEY (postsId, publicId),
    FOREIGN KEY (postsId) REFERENCES Posts(id) ON DELETE CASCADE
);

CREATE TABLE PostVotes (
    userId BIGINT NOT NULL,
    postId BIGINT NOT NULL,
    vote SMALLINT NOT NULL,
    PRIMARY KEY (userId, postId),
    FOREIGN KEY (userId) REFERENCES Users(id) ON DELETE CASCADE,
    FOREIGN KEY (postId) REFERENCES Posts(id) ON DELETE CASCADE
);

CREATE TABLE Surveys (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    question VARCHAR(255) NOT NULL,
    createdAt TIMESTAMP,
    endDateTime TIMESTAMP,
    type TINYINT NOT NULL
);

CREATE TABLE survey_options (
    surveyId BIGINT NOT NULL,
    optionId INTEGER NOT NULL,
    optionText VARCHAR(255),
    PRIMARY KEY (surveyId, optionId),
    FOREIGN KEY (surveyId) REFERENCES Surveys(id) ON DELETE CASCADE
);

CREATE TABLE SurveyResponses (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    surveyId BIGINT NOT NULL,
    userId BIGINT NOT NULL,
    FOREIGN KEY (surveyId) REFERENCES Surveys(id) ON DELETE CASCADE
);


CREATE TABLE survey_response_options (
    surveyResponseId BIGINT NOT NULL,
    optionId INTEGER NOT NULL,
    PRIMARY KEY (surveyResponseId, optionId),
    FOREIGN KEY (surveyResponseId) REFERENCES SurveyResponses(id) ON DELETE CASCADE
);

CREATE TABLE PointTransactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    userId BIGINT NOT NULL,
    points INT NOT NULL,
    reason VARCHAR(50) NOT NULL,
    entityType VARCHAR(50),
    entityId BIGINT,
    createdAt TIMESTAMP
);

