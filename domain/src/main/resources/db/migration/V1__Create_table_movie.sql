CREATE TABLE IF NOT EXISTS movie
(
    ID          SERIAL       NOT NULL,
    TITLE       VARCHAR(100) NOT NULL,
    DESCRIPTION VARCHAR(500) NOT NULL,
    RELEASE     VARCHAR(10)  NOT NULL,
    GENRE       VARCHAR(50)  NOT NULL,
    PRODUCTION  VARCHAR(20)  NOT NULL,
    ACTORS      VARCHAR(500) NOT NULL,
    IMAGE  BYTEA,
    TRAILER VARCHAR (100),
    RATING INTEGER NOT NULL
)
