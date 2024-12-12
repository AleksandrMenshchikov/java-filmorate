CREATE TABLE IF NOT EXISTS users (
	id BIGSERIAL PRIMARY KEY,
	email VARCHAR NOT NULL UNIQUE CHECK (email ~* '^[A-Za-z0-9._+%-]+@[A-Za-z0-9.-]+[.][A-Za-z]+$'),
	login VARCHAR NOT NULL CHECK (LENGTH(TRIM(login)) > 0),
	name VARCHAR NOT NULL CHECK (LENGTH(TRIM(name)) > 0),
	birthday DATE NOT NULL CHECK (birthday <= CURRENT_DATE)
);

CREATE TABLE IF NOT EXISTS mpa (
	id BIGINT PRIMARY KEY,
	name VARCHAR NOT NULL UNIQUE CHECK (UPPER(name) IN ('G', 'PG', 'PG-13', 'R', 'NC-17'))
);
MERGE INTO mpa KEY(ID) VALUES
(1, 'G'),
(2, 'PG'),
(3, 'PG-13'),
(4, 'R'),
(5, 'NC-17');

CREATE TABLE IF NOT EXISTS films (
	id BIGSERIAL PRIMARY KEY,
	name VARCHAR NOT NULL CHECK (LENGTH(TRIM(name)) > 0),
	description VARCHAR(200) NOT NULL CHECK (LENGTH(TRIM(description)) > 0),
	release_date DATE NOT NULL CHECK (release_date >= '1895-12-28' AND release_date <= CURRENT_DATE),
	duration INTEGER NOT NULL CHECK (duration > 0),
	mpa_id BIGINT NOT NULL REFERENCES mpa (id)
);

CREATE TABLE IF NOT EXISTS genres (
	id BIGINT PRIMARY KEY,
	name VARCHAR NOT NULL UNIQUE CHECK (LOWER(name) IN ('комедия', 'драма', 'мультфильм', 'триллер', 'документальный', 'боевик'))
);
MERGE INTO genres KEY(ID) VALUES
(1, 'Комедия'),
(2, 'Драма'),
(3, 'Мультфильм'),
(4, 'Триллер'),
(5, 'Документальный'),
(6, 'Боевик');

CREATE TABLE IF NOT EXISTS films_genres (
	id BIGSERIAL PRIMARY KEY,
	film_id BIGINT NOT NULL REFERENCES films (id),
	genre_id BIGINT NOT NULL REFERENCES genres (id),
	UNIQUE (film_id, genre_id)
);

CREATE TABLE IF NOT EXISTS friends (
    id BIGSERIAL PRIMARY KEY,
	user_id BIGINT NOT NULL CHECK (user_id != friend_id) REFERENCES users (id),
	friend_id BIGINT NOT NULL CHECK (user_id != friend_id) REFERENCES users (id),
	UNIQUE (user_id, friend_id)
);

CREATE TABLE IF NOT EXISTS likes (
	id BIGSERIAL PRIMARY KEY,
	film_id BIGINT NOT NULL REFERENCES films (id),
	user_id BIGINT NOT NULL REFERENCES users (id),
	UNIQUE (film_id, user_id)
);