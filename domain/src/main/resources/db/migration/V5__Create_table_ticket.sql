CREATE TABLE IF NOT EXISTS ticket
(
    ID             BIGSERIAL       NOT NULL,
    OWNER          VARCHAR(50)  NOT NULL,
    USER_ID        BIGSERIAL      NOT NULL,
    MOVIE_NAME     VARCHAR(100) NOT NULL,
    PLACE_QUANTITY INTEGER      NOT NULL,
    TIME           VARCHAR(100)  NOT NULL,
    HALL_NAME      VARCHAR(100) NOT NULL,
    EVENT_ID       BIGSERIAL      NOT NULL,
    COMMON_AMOUNT INTEGER NOT NULL
)