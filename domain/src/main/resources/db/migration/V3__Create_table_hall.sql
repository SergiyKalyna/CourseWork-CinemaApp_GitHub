CREATE TABLE IF NOT EXISTS hall
(
    ID   SERIAL     NOT NULL,
    NAME     VARCHAR(100) NOT NULL UNIQUE,
    PLACES INTEGER NOT NULL
)