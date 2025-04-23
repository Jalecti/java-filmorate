
INSERT INTO friendship_statuses (friendship_status_name)
VALUES ('CONFIRMED'),
       ('UNCONFIRMED')
;

INSERT INTO genres (genre_name)
VALUES ('Комедия'),
       ('Драма'),
       ('Мультфильм'),
       ('Триллер'),
       ('Документальный'),
       ('Боевик')
;

INSERT INTO ratings (rating_name)
VALUES ('G'),
       ('PG'),
       ('PG-13'),
       ('R'),
       ('NC-17')
;

INSERT INTO user_event_types (uet_name)
VALUES ('LIKE'),
       ('REVIEW'),
       ('FRIEND')
;

INSERT INTO event_operations (eo_name)
VALUES ('REMOVE'),
       ('ADD'),
       ('UPDATE')
;