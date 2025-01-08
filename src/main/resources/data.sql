INSERT INTO genre(name) values ('Комедия'), ('Драма'), ('Мультфильм'), ('Триллер'), ('Документальный'), ('Боевик');

INSERT INTO mpa(name) values ('G'), ('PG'), ('PG-13'), ('R'), ('NC-17');

INSERT INTO users(email, login, name, birthday) values
                ('gorych15200@yandex.ru', 'gorych152', 'Vladimir', '1998-07-25'),
                ('gorych152@yandex.ru', 'gorych15200', 'Vladimir2', '2000-08-26'),
                ('gorych@yandex.ru', 'gorych', 'Vladimir3', '1970-03-18');

INSERT INTO films(name, description, releaseDate, duration, mpa_id) values
                 ('film1', 'description1', '2005-08-24', 200, 1),
                 ('film2', 'description2', '2000-05-04', 120, 2),
                 ('film3', 'description3', '1980-04-27', 100, 3);